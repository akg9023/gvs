package com.voice.v1.yatraRegistration.memberReg.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Transaction class packs transaction details like transaction date,
 * transaction amount, transaction description, UPI ID, transaction id etc.
 */
public class Transaction {
    private final LocalDate transactionDate;
    private final Double transactionAmount;
    private final String transactionDescription;
    private final String upiId;
    private final String transactionId;

    private final LocalTime transactionTime;

    /**
     * This method checks if the transaction object matches with database entity.
     *
     * @param transactionAmount
     * @param transactionId
     * @return true, if matches, else false
     */
    public boolean matches(String transactionId, Double transactionAmount) {
        // TODO: add extra comparison logic as necessary.
        return this.transactionAmount.equals(transactionAmount) &&
                this.transactionId.equals(transactionId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionDate=" + transactionDate +
                ", transactionAmount=" + transactionAmount +
                ", transactionDescription='" + transactionDescription + '\'' +
                ", upiId='" + upiId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", transactionTime=" + transactionTime +
                '}';
    }

    public Transaction(String transactionDate, String transactionDescription, String debitAmount, String creditAmount) {
        this.transactionDate = LocalDate.parse(transactionDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String transactionAmt = creditAmount.isEmpty() ? ('-' + debitAmount) : (creditAmount);
        transactionAmt = transactionAmt.replace(",", "");
        this.transactionAmount = Double.parseDouble(transactionAmt);
        this.transactionDescription = transactionDescription;
        Map<String, String> infoMap = parseTransactionDescription(transactionDescription);
        this.upiId = infoMap.get("upiId");
        this.transactionId = infoMap.get("transactionId");
        this.transactionTime = LocalTime.parse(infoMap.get("transactionTime"), DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private Map<String, String> parseTransactionDescription(String transactionDescription) {
        Map<String, String> infoMap = new HashMap<>();
        String[] info = transactionDescription.split("/");
        infoMap.put("transactionId", info[1]);
        infoMap.put("transactionTime", info[2]);
        infoMap.put("upiId", info[4]);
        return infoMap;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public String getUpiId() {
        return upiId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalTime getTransactionTime() {
        return transactionTime;
    }
}