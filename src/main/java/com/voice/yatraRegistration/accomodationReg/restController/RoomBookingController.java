package com.voice.yatraRegistration.accomodationReg.restController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


//import org.apache.bcel.classfile.Constant;
import com.voice.yatraRegistration.accomodationReg.dao.RoomBookingDao;
import com.voice.yatraRegistration.accomodationReg.dao.RoomDao;
import com.voice.yatraRegistration.accomodationReg.model.RoomBooking;
import com.voice.yatraRegistration.accomodationReg.model.RoomSet;
import com.voice.yatraRegistration.accomodationReg.service.AsyncService;
import com.voice.yatraRegistration.accomodationReg.service.RoomBookingService;
import com.voice.yatraRegistration.accomodationReg.utils.Constants;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.dbRegistration.dao.DevoteeInfoDao;
//import com.voice.dbRegistration.service.SendSmsService;
import com.voice.yatraRegistration.memberReg.dao.MemberDao;
import com.voice.yatraRegistration.memberReg.model.Member;

//@RestController
//@RequestMapping("/v1/room/bookings/")
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

    @PostMapping("/fetchAllBookingsByEmail")
    public List<RoomBooking> fetchAllByEmail(@RequestBody Map<String, String> input) {
         String email = input.get("email");
        return bookingDao.findAllByCustomerEmail(email);
    }

    @PostMapping("/fetchAll")
    public List<RoomBooking> fetchAllBookings() {
        return bookingDao.findAll();
    }

    @PostMapping("/saveBooking")
    public RoomBooking saveRoom(@RequestBody RoomBooking booking) {
        return bookingDao.save(booking);
    }

    @PostMapping("/reserveRoomAndProceedForPayment")
    public Map<String, String> reserveRoomAndProceedForPayment (@RequestBody RoomBooking booking) {

        HashMap<String, String> response = new HashMap<>();

        try {
             // calculate amount
            String amount = roomBookingService.validateCountAndCalculateAmount(booking.getRoomSet());

            Long bookingId = roomBookingService.reserveRoom(booking, amount);

            asyncService.waitAsync(bookingId);

            System.out.println("Booking reserved for 5min. Please proceed for txn.");

            response.put("bookingId",String.valueOf(bookingId));
            response.put("amount",amount);
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
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

    @PostMapping("/fetchAllPendingBookings")
    public List<RoomBooking> getAllPendingBookings(){
        return bookingDao.findAllByPaymentStatus(Constants.PENDING);
    }

    @PostMapping("/fetchAllApprovedBookings")
    public List<RoomBooking> getAllApprovedBookings(){
        return bookingDao.findAllByPaymentStatus(Constants.SUCCESS);
    }

    @PostMapping("/fetchAllDeclineBookings")
    public List<RoomBooking> getAllDeclineBookings(){
        return bookingDao.findAllByPaymentStatusLike(Constants.DECLINE+"%");
    }

    @PostMapping("/fetchAllPendingMembers")
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

     @PostMapping("/fetchAllApprovedMembers")
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

    @PostMapping("/decline/{id}")
    public RoomBooking declineBooking(@PathVariable("id") Long roomBookingId,@RequestBody Map<String,String> input){
        String pStaus = input.get("paymentStatus");
        RoomBooking rm = bookingDao.findOneById(roomBookingId);

        //increase the room count
        roomBookingService.manageRoomCount(rm.getRoomSet(), true);

        //set status as decline
        rm.setPaymentStatus(pStaus);
        RoomBooking booked = bookingDao.save(rm);
//        SendSmsService sendSmsService = new SendSmsService();
//        sendSmsService.sendSms(Constants.SMS_DECLINE_MESSAGE,booked.getCustomerPhoneNo());
        return booked;
    }

}