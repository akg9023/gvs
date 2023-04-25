package com.voice.yatraRegistration.memberReg.utils.cron;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.voice.yatraRegistration.memberReg.dao.BackupRegisterMemDao;
import com.voice.yatraRegistration.memberReg.dao.RegisterMemDao;
import com.voice.yatraRegistration.memberReg.model.BackupRegisteredMember;
import com.voice.yatraRegistration.memberReg.model.RegisteredMember;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@Transactional
public class ScheduleJob {

    @Autowired
    private RegisterMemDao registerMemDao;

    @Autowired
    private BackupRegisterMemDao backupRegisterMemDao;

    // 30min
    @Scheduled(fixedDelay = 1800000)
    public void distributeReports() throws InterruptedException {
        int count = 0;
        log.info("Backup pending request :::::");

        List<RegisteredMember> allRegMem = registerMemDao.findAll();
        for (RegisteredMember one : allRegMem) {
            if (timeDuration(one.getCreatedDateTime())) {
                if (one.getPaymentStatus().equals("pending")) {
                    BackupRegisteredMember backup = new BackupRegisteredMember();
                    backup.setMemberIdList(one.getMemberIdList().toString());
                    backup.setAmount(one.getAmount());
                    backup.setUserEmail(one.getUserEmail());
                    backup.setCustomerEmail(one.getCustomerEmail());
                    backup.setCustomerTxnId(one.getCustomerTxnId());
                    backup.setPaymentStatus(one.getPaymentStatus());
                    backup.setAttemptDateTime(one.getCreatedDateTime());
                    backupRegisterMemDao.save(backup);

                    registerMemDao.delete(one);
                    count++;
                }
            }

        }
        log.info("backed up " + count + " rows.");

    }

    private static boolean timeDuration(LocalDateTime oldDate) {
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(oldDate, currentTime);
        // total seconds of difference (using Math.abs to avoid negative values)
        long seconds = Math.abs(duration.getSeconds());
        long hours = seconds / 3600;
        seconds -= (hours * 3600);
        long minutes = seconds / 60;
        seconds -= (minutes * 60);

        if(minutes>10)
            return true;
        return false;
    }

}
