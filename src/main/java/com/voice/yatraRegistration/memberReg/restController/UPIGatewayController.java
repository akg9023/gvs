package com.voice.yatraRegistration.memberReg.restController;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.voice.yatraRegistration.memberReg.model.CheckStatus;
import com.voice.yatraRegistration.memberReg.model.Response;
import com.voice.yatraRegistration.memberReg.model.YatraCreateOrderRequest;

import ch.qos.logback.classic.util.EnvUtil;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/pay")
public class UPIGatewayController {

    static RestTemplate restTemplate = new RestTemplate();

	@Value("${upi_gateway_secret}")
	private String key;

    @PostMapping("/createOrder")
    public ResponseEntity sendRequest(@RequestBody YatraCreateOrderRequest upiRequest) {

        JSONObject jsonUpiRequest = new JSONObject();
        jsonUpiRequest.put("key", key);
        jsonUpiRequest.put("client_txn_id", upiRequest.getClientTransactionId());
        jsonUpiRequest.put("amount", upiRequest.getAmount());
        jsonUpiRequest.put("p_info", upiRequest.getInfo());
        jsonUpiRequest.put("customer_name", upiRequest.getCustomerName());
        jsonUpiRequest.put("customer_email", upiRequest.getCustomerEmail());
        jsonUpiRequest.put("customer_mobile", upiRequest.getCustomerMobile());
        jsonUpiRequest.put("redirect_url", "http://127.0.0.1:8080/v1/pay/getTxn");
        jsonUpiRequest.put("udf1", upiRequest.getUserDefined1());
        jsonUpiRequest.put("udf2", upiRequest.getUserDefined2());
        jsonUpiRequest.put("udf3", upiRequest.getUserDefined3()); // System.out.println(jsonUpiRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonUpiRequest.toString(), headers);

        ResponseEntity<String> result = restTemplate.exchange("https://merchant.upigateway.com/api/create_order",
                HttpMethod.POST, request, String.class);
        return result;
    }

    @GetMapping("/getTxn")
    public String getTransaction(@RequestParam("client_txn_id") String txnId, HttpServletRequest req)
            throws URISyntaxException {
        final String baseUrl = "https://merchant.upigateway.com/api/check_order_status";
        URI uri = new URI(baseUrl);
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        formatDate.setTimeZone(TimeZone.getTimeZone("IST"));
        String txnDate = formatDate.format(date);

        // LocalDate l = LocalDate.now();
        // DateTimeFormatter format =
        // DateTimeFormatter.ofPattern("dd-MM-yyyy");
        // String txnDate = l.format(format);

        CheckStatus body = new CheckStatus(txnId, txnDate, key);
        // System.out.println(body);
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
                    System.out.println(responseEntity.getBody().getData());
                return "success";
            } else if (responseEntity.getBody().getData().getStatus().equalsIgnoreCase("scanning"))
                return "failure";
            else
                return "error";
        } else
            return "unathorized";
    }

}
