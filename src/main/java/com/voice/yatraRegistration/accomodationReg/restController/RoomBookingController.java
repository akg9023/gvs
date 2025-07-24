package com.voice.yatraRegistration.accomodationReg.restController;

import java.util.*;

import com.voice.auth.model.UserAuth;
import com.voice.auth.service.UserAuthService;
import com.voice.payments.response.PaymentTokenResponse;
import com.voice.payments.service.PaymentService;
import com.voice.yatraRegistration.accomodationReg.dao.RoomBookingDao;
import com.voice.yatraRegistration.accomodationReg.dao.RoomDao;
import com.voice.yatraRegistration.accomodationReg.model.RoomBooking;
import com.voice.yatraRegistration.accomodationReg.model.RoomSet;
import com.voice.yatraRegistration.accomodationReg.service.AsyncService;
import com.voice.yatraRegistration.accomodationReg.service.RoomBookingService;
import com.voice.yatraRegistration.accomodationReg.utils.Constants;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.voice.dbRegistration.dao.DevoteeInfoDao;
//import com.voice.dbRegistration.service.SendSmsService;
import com.voice.yatraRegistration.memberReg.dao.MemberDao;
import com.voice.yatraRegistration.memberReg.model.Member;

@RestController
@RequestMapping("/v1/room/bookings")
@CrossOrigin("*")
@EnableAsync
public class RoomBookingController {

    @Autowired
    RoomBookingDao bookingDao;

    @Autowired
    DevoteeInfoDao devoteeInfoDao;

    @Autowired
    RoomDao roomDao;

    @Autowired
    MemberDao memberDao;

    @Autowired
    AsyncService asyncService;

    @Autowired
    RoomBookingService roomBookingService;
    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/fetchAllBookingsByEmail")
    public ResponseEntity<List<RoomBooking>> fetchAllByEmail(Authentication  authentication) {
        Optional<UserAuth> user=userAuthService.getUserAuthFromAuthentication(authentication);
        return user.map(userAuth -> ResponseEntity.ok(bookingDao.findAllByCustomerEmail(userAuth.getUserEmail()))).orElseGet(()->ResponseEntity.internalServerError().build());
    }

    @PostMapping("/saveBooking")
    public RoomBooking saveRoom(@RequestBody RoomBooking booking) {
        return bookingDao.save(booking);
    }

    @PostMapping("/reserveRoomAndProceedForPayment")
    public ResponseEntity<PaymentTokenResponse> reserveRoomAndProceedForPayment (@RequestBody RoomBooking booking) {


        try {
             // calculate amount
            String amount = roomBookingService.validateCountAndCalculateAmount(booking.getRoomSet());
            booking.setAmount(amount);
            booking.setCustomerTxnId("GVS" + System.currentTimeMillis() + (int)(Math.random() * 1000));
            booking.setTxnDate(String.valueOf(LocalDateTime.now()));
            Long bookingId = roomBookingService.reserveRoom(booking);
            asyncService.waitAsync(bookingId);
            paymentService.initiatePayment(booking);

            System.out.println("Booking reserved for 8 min. Please proceed for txn.");

            return paymentService.initiatePayment(booking);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.unprocessableEntity().build();

    }

    @PostMapping("/saveTxn")
    public RoomBooking saveTxnDetail(@RequestBody Map<String, String> req) throws Exception {

        String bookingId = req.get("bookingId");
        // transaction
        String customerTxnId = "";
        String customerVPA = req.get("customerVPA");
        String customerEmail = req.get("customerEmail");
        String customerName = req.get("customerName");
        String customerPhoneNo = req.get("customerPhoneNo");
        String upiTxnId = req.get("upiTxnId");
        String txnDate = String.valueOf(LocalDateTime.now());

        if (bookingId == null) {
            throw new Exception("BookingId doesnt found.");
        }

        if (upiTxnId == null) {
            throw new Exception("UPI txn id should be unique");
        }

        RoomBooking rm = bookingDao.findOneById(Long.parseLong(bookingId));

        if (rm.getPaymentStatus().equals(Constants.TIMEOUT)) {
            throw new Exception("Payment Timeout!!!");
        }

        rm.setUpiTxnId(upiTxnId);
        rm.setTxnDate(txnDate);
        rm.setCustomerEmail(customerEmail);
        rm.setCustomerVPA(customerVPA);
        rm.setPaymentStatus(Constants.PENDING);
        rm.setCustomerName(customerName);
        rm.setCustomerPhoneNo(customerPhoneNo);

        RoomBooking res = bookingDao.save(rm);

        return res;
    }

    @GetMapping("/fetchAllPendingBookings")
    public List<RoomBooking> getAllPendingBookings(){
        return bookingDao.findAllByPaymentStatus(Constants.PENDING);
    }

    @GetMapping("/fetchAllApprovedBookings")
    public List<RoomBooking> getAllApprovedBookings(){
        return bookingDao.findAllByPaymentStatus(Constants.SUCCESS);
    }

    @GetMapping("/fetchAllDeclineBookings")
    public List<RoomBooking> getAllDeclineBookings(){
        return bookingDao.findAllByPaymentStatusLike(Constants.DECLINE+"%");
    }

    @GetMapping("/fetchAllPendingMembers")
    public List<Member> getAllPendingMembers(){
        List<Member> pendingMem = new LinkedList<>();
        List<RoomBooking> pendingBook = bookingDao.findAllByPaymentStatus(Constants.PENDING);
        for(RoomBooking one:pendingBook){
            List<RoomSet> rm = one.getRoomSet();
            for(RoomSet r:rm){
                pendingMem.addAll(r.getMember());
            }
        }
        return pendingMem;
    }

     @GetMapping("/fetchAllApprovedMembers")
    public List<Member> getAllApprovedMembers(){
        List<Member> approvedMem = new LinkedList<>();
        List<RoomBooking> approvedBook = bookingDao.findAllByPaymentStatus(Constants.SUCCESS);
        for(RoomBooking one:approvedBook){
            List<RoomSet> rm = one.getRoomSet();
            for(RoomSet r:rm){
                approvedMem.addAll(r.getMember());
            }
        }
        return approvedMem;
    }

     @PostMapping("/approve/{id}")
    public RoomBooking approveBooking(@PathVariable("id") Long roomBookingId){
        RoomBooking rm = bookingDao.findOneById(roomBookingId);

        rm.setPaymentStatus(Constants.SUCCESS);

        RoomBooking roomBooked = bookingDao.save(rm);
//        SendSmsService sendSmsService = new SendSmsService();
//        sendSmsService.sendSms(Constants.SMS_APPROVED_MESSAGE,roomBooked.getCustomerPhoneNo());
        return roomBooked;
    }

}