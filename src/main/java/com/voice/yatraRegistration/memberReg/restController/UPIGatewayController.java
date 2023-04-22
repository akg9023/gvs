package com.voice.yatraRegistration.memberReg.restController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.voice.dbRegistration.model.GetIDFnameGender;
import com.voice.yatraRegistration.memberReg.dao.RegisterMemDao;
import com.voice.yatraRegistration.memberReg.model.CheckStatus;
import com.voice.yatraRegistration.memberReg.model.Customer;
import com.voice.yatraRegistration.memberReg.model.Member;
import com.voice.yatraRegistration.memberReg.model.RegisteredMember;
import com.voice.yatraRegistration.memberReg.model.Response;
import com.voice.yatraRegistration.memberReg.model.YatraCreateOrderRequest;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/pay")
public class UPIGatewayController {

    static RestTemplate restTemplate = new RestTemplate();

    @Value("${upi.callback.url}")
    private String callbackUrl;

    @Autowired
    private RegisterMemDao registerMemDao;

    @Value("${upi_gateway_secret}")
    private String key;

    @Value("${yatra.success.page.url}")
    private String successUrl;

    @PostMapping("/createOrder")
    public ResponseEntity sendRequest(@RequestBody Map<String, Object> input) {

        String amount = (String) input.get("amount");
        String userEmail = (String) input.get("customerEmail");
        String clientTxtId = (String) input.get("clientTransactionId");
        List<Map<String,Object>> devoteeList = (List<Map<String,Object>>) input.get("memberDetails");
        List<Member> membersList = new ArrayList<>();

        for (Map<String,Object> one : devoteeList) {
            Member mem = new Member();
            mem.setDbDevId((String)one.get("id"));
            mem.setDbDevName((String)one.get("fname"));
            mem.setDbDevGender((String)one.get("gender"));
            membersList.add(mem);
        }

        RegisteredMember registeredMember = new RegisteredMember();
        registeredMember.setAmount(amount);
        registeredMember.setUserEmail(userEmail);
        registeredMember.setMemberIdList(membersList);
        registeredMember.setPaymentStatus("pending");
        registeredMember.setCustomerTxnId(clientTxtId);
        RegisteredMember savedRegResponse = registerMemDao.save(registeredMember);

        YatraCreateOrderRequest upiRequest = new YatraCreateOrderRequest();
        upiRequest.setClientTransactionId(clientTxtId);
        upiRequest.setAmount(amount);
        upiRequest.setInfo((String) input.get("info"));
        upiRequest.setCustomerName("Yatra");
        upiRequest.setCustomerEmail(userEmail);
        upiRequest.setCustomerMobile("8969264042");

        JSONObject jsonUpiRequest = new JSONObject();
        jsonUpiRequest.put("key", key);
        jsonUpiRequest.put("client_txn_id", upiRequest.getClientTransactionId());
        jsonUpiRequest.put("amount", upiRequest.getAmount());
        jsonUpiRequest.put("p_info", upiRequest.getInfo());
        jsonUpiRequest.put("customer_name", upiRequest.getCustomerName());
        jsonUpiRequest.put("customer_email", upiRequest.getCustomerEmail());
        jsonUpiRequest.put("customer_mobile", upiRequest.getCustomerMobile());
        jsonUpiRequest.put("redirect_url", callbackUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonUpiRequest.toString(), headers);

        ResponseEntity<String> result = restTemplate.exchange("https://merchant.upigateway.com/api/create_order",
                HttpMethod.POST, request, String.class);

        return result;
    }

    @GetMapping("/getTxn")
    public String getTransaction(@RequestParam("client_txn_id") String txnId, HttpServletRequest req,HttpServletResponse res)
            throws URISyntaxException, IOException {
        final String baseUrl = "https://merchant.upigateway.com/api/check_order_status";
        URI uri = new URI(baseUrl);
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        formatDate.setTimeZone(TimeZone.getTimeZone("IST"));
        String txnDate = formatDate.format(date);

        CheckStatus body = new CheckStatus(txnId, txnDate, key);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> reqeustEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Response> responseEntity = restTemplate.exchange(uri,
                HttpMethod.POST, reqeustEntity, Response.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();

        if (responseEntity.getBody().getStatus().equalsIgnoreCase("true")) {
            if (responseEntity.getBody().getData().getStatus().equalsIgnoreCase("success")) {
                HttpSession session = req.getSession();
                Customer customer = responseEntity.getBody().getData();
                // save in db
                RegisteredMember foundRec = registerMemDao.findByCustomerTxnId(customer.getClient_txn_id());
                foundRec.setCustomerEmail(customer.getCustomer_email());
                foundRec.setCustomerVPA(customer.getCustomer_vpa());
                foundRec.setPaymentStatus(customer.getStatus());
                foundRec.setTxnDate(customer.getTxnAt());
                foundRec.setUpiTxnId(customer.getUpi_txn_id());
                
                registerMemDao.save(foundRec);
                res.sendRedirect("/v1/pay/success/"+foundRec.getCustomerTxnId());
                return "success";
            } else if (responseEntity.getBody().getData().getStatus().equalsIgnoreCase("scanning")){
                res.sendRedirect("/failure");
                return "failure";
            }else
                return "error";
        } else
            return "unathorized";
    }


    @GetMapping("/failure")
    public ResponseEntity<Void> failure(){
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://fullstackdeveloper.guru")).build();
    }

    @GetMapping("/success/{clientTxnId}")
    public ResponseEntity<Void> success(@PathVariable("clientTxnId") String clientTxnId){
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(successUrl+clientTxnId)).build();
    }

}
