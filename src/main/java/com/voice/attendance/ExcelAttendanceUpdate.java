
/*************************************************************************
 * Description: This file contains ExtractBankTransactions class and Transaction class.
 * ExtractBankTransactions.java
 * Author : Ashish Goyal(akg9023@gmail.com)
 ************************************************************************/

package com.voice.attendance;

import org.apache.commons.io.IOUtils;

import com.voice.yatraRegistration.memberReg.model.Transaction;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ExtractBankTransactions reads the bank transaction file and extracts the
 * transactions from it.
 */
public class ExcelAttendanceUpdate {

    private final InputStream attendanceFileStream;
    private final Map<String, Transaction> transactions;

    public ExcelAttendanceUpdate(InputStream attendanceFileStream) {

        // TODO: for multipartfile received from web, send
        // multipartfile.getInputStream() as parameter
        this.attendanceFileStream = attendanceFileStream;
        transactions = parseAttendance(attendanceFileStream);

    }

    private Map<String, Transaction> parseAttendance(InputStream attendanceFileStream) {
        Map<String, Transaction> trans = new HashMap<>();
        try {
            List<String> lines = IOUtils.readLines(attendanceFileStream, StandardCharsets.UTF_8.name());

            //attendacne start from line 4
            lines = lines.subList(4, lines.size());


        } catch (IndexOutOfBoundsException | IOException e) {
            // TODO: log error and throw appropriate exception for sending error message to
            // UI.
            throw new RuntimeException("Error reading transaction file", e);
        }
        return trans;
    }

    public Transaction extractTransaction(String transactionId) {
        return transactions.get(transactionId);
    }

}