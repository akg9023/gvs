
/*************************************************************************
 * Description: This file contains ExtractBankTransactions class and Transaction class.
 * ExtractBankTransactions.java
 * Author : Ashish Goyal(akg9023@gmail.com)
 ************************************************************************/

package com.voice.yatraRegistration.memberReg.utils.readExcelAndUpdate;

import com.voice.yatraRegistration.memberReg.model.Transaction;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ExtractBankTransactions reads the bank transaction file and extracts the
 * transactions from it.
 */
public class ExtractBankTransactions {

    private final InputStream transactionFileStream;
    private final Map<String, Transaction> transactions;
    private final LocalDate lastDate;

    public ExtractBankTransactions(InputStream transactionFileStream) {

        // TODO: for multipartfile received from web, send
        // multipartfile.getInputStream() as parameter
        this.transactionFileStream = transactionFileStream;
        transactions = parseTransactions(transactionFileStream);

        lastDate = findMaxDate();
    }

    public LocalDate getLastDate() {
        return lastDate;
    }

    private LocalDate findMaxDate() {
        LocalDate maxDate = transactions.entrySet().stream()
                .map(Map.Entry::getValue)
                .map((Transaction t) -> t.getTransactionDate())
                .max(Comparator.naturalOrder()).get();

        return maxDate;
    }

    private Map<String, Transaction> parseTransactions(InputStream transactionFileStream) {
        Map<String, Transaction> trans = new HashMap<>();
        try {
            List<String> lines = IOUtils.readLines(transactionFileStream, StandardCharsets.UTF_8.name());
            // Transactions start at line number 80.
            lines = lines.subList(80, lines.size());
            // filter strings containing UPI keywo rd in it from lines list and create a new
            // list of strings.
            lines = lines.stream().filter(line -> line.contains("UPI")).collect(Collectors.toList());
            int[] indices = { 11, 23, 40, 90, 111, 132 };

            // fill trans map with transaction object from the list of strings extracting
            // each part based on indices.
            trans = lines.stream().map(line -> new Transaction(line.substring(indices[0], indices[1]).trim(),
                    line.substring(indices[2], indices[3]).trim(),
                    line.substring(indices[3], indices[4]).trim(),
                    line.substring(indices[4], indices[5]).trim()))
                    .collect(Collectors.toMap(Transaction::getTransactionId, transaction -> transaction));
        } catch (IndexOutOfBoundsException e) {
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