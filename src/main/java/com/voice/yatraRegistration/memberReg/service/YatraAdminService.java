package com.voice.yatraRegistration.memberReg.service;

import com.voice.auth.dao.RoleRepository;
import com.voice.auth.model.Role;
import com.voice.common.model.YatraRegEmail;
import com.voice.common.service.EmailService;
import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.dao.PermittedUsersDao;
import com.voice.dbRegistration.model.DevoteeInfo;
import com.voice.dbRegistration.model.PermittedUsers;
import com.voice.yatraRegistration.memberReg.dao.RegisterMemDao;
import com.voice.yatraRegistration.memberReg.model.EmailMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class YatraAdminService {
    public static final String ROLE_PREFIX = "ROLE_";
    Logger logger = LoggerFactory.getLogger(YatraAdminService.class);
    @Autowired
    private RegisterMemDao regMemDao;
    @Autowired
    DevoteeInfoDao devoteeInfoDao;

    @Autowired
    PermittedUsersDao permittedUsersDao;

    @Autowired
    EmailService emailService;

    @Autowired
    RoleRepository roleRepository;

    public Optional<Role> saveRole(Role role){
        if(role!=null && role.getName().startsWith(ROLE_PREFIX)){
            if(role.getPrivileges()==null){
                role.setPrivileges(new HashSet<>());
            }
            return Optional.of(roleRepository.save(role));
        }
        return Optional.empty();
    }
    public Optional<List<DevoteeInfo>> getDevoteeInfoWithinDateRange(String startDateStr, String endDateStr){
        try{
            LocalDate from = LocalDate.parse(startDateStr);
            LocalDate to = LocalDate.parse(endDateStr);
            LocalDateTime startDate = from.atStartOfDay();
            LocalDateTime endDate = to.atTime(LocalTime.MAX);
            return  Optional.of(devoteeInfoDao.findAllByCreatedDateTimeBetween(startDate, endDate));
        }catch (Exception e){
            logger.error("Get DevoteeInfo Withing Date Range Error {}", e.getMessage());
        }
        return Optional.empty();
    }
    public Map<String, EmailMember> sendEmailWithMemberDetailsForTMPId(){
        Map<String, EmailMember> mapEmailMemberMap = new HashMap<>();
        try {
            List<Object[]> tmpIdlist = regMemDao.findDevoteeDetailsTMPIdForEmail();

            for (Object[] result : tmpIdlist) {
                YatraRegEmail yatraRegEmail = getRegEmailTMPId(result);

                if (mapEmailMemberMap.containsKey(yatraRegEmail.getSendTMPIdEmailTo())) {
                    EmailMember emailMember = mapEmailMemberMap.get(yatraRegEmail.getSendTMPIdEmailTo());
                    Map<String, YatraRegEmail> tmpRegMap = emailMember.getTmpIdRegistrationMemberMap();

                    tmpRegMap.put(yatraRegEmail.getDevoteeId(), yatraRegEmail);
                } else {
                    EmailMember emailMember = new EmailMember();
                    emailMember.setTmpIdSendEmailTo(yatraRegEmail.getSendTMPIdEmailTo());

                    Map<String, YatraRegEmail> tmpRegMap = new HashMap<>();
                    tmpRegMap.put(yatraRegEmail.getDevoteeId(), yatraRegEmail);
                    emailMember.setTmpIdRegistrationMemberMap(tmpRegMap);

                    mapEmailMemberMap.put(yatraRegEmail.getSendTMPIdEmailTo(), emailMember);
                }
            }
            emailService.sendTMPIdEmailHelperRegistration(mapEmailMemberMap);
        } catch (Exception e) {
            logger.error("Send TMP Id Email with Member Fetching Error {} ", e.getMessage());
        }
        return mapEmailMemberMap;
    }
    public Map<String, EmailMember> sendEmailWithMemberDetails() {
        //Who registered his email as key
        Map<String, EmailMember> mapEmailMemberMap = new HashMap<>();

        try {
            List<Object[]> yatraRegEmailsList = regMemDao.findDevoteeDetailsRegisteredInYatraForEmail();

            for(Object[] result : yatraRegEmailsList){
                YatraRegEmail yatraRegEmail = getRegEmail(result);

                if(mapEmailMemberMap.containsKey(yatraRegEmail.getPaidByEmail())){
                    EmailMember emailMember = mapEmailMemberMap.get(yatraRegEmail.getPaidByEmail());
                    Map<String, YatraRegEmail> yatraMap = emailMember.getYatraRegistrationMemberMap();

                    yatraMap.put(yatraRegEmail.getDevoteeId(),yatraRegEmail);
                }else{
                    EmailMember emailMember = new EmailMember();
                    emailMember.setPaidByEmail(yatraRegEmail.getPaidByEmail());

                    Map<String, YatraRegEmail> yatraMap = new HashMap<>();
                    yatraMap.put(yatraRegEmail.getDevoteeId(), yatraRegEmail);
                    emailMember.setYatraRegistrationMemberMap(yatraMap);

                    mapEmailMemberMap.put(yatraRegEmail.getPaidByEmail(), emailMember);
                }

            }
            List<Object[]> resultsDB = regMemDao.findDevoteeDetailsDBRegistrationForEmail();
            for(Object[] result : resultsDB){
                YatraRegEmail yatraRegEmail = getDBRegEmail(result);

                if(mapEmailMemberMap.containsKey(yatraRegEmail.getRegisteredByEmail())){

                    EmailMember emailMember = mapEmailMemberMap.get(yatraRegEmail.getRegisteredByEmail());
                    Map<String, YatraRegEmail> DBMap = emailMember.getDbRegistrationMemberMap();

                    if(DBMap!=null){
                        DBMap.put(yatraRegEmail.getDevoteeId(),yatraRegEmail);
                    }else{
                        Map<String, YatraRegEmail> DBMapNew = new HashMap<>();
                        DBMapNew.put(yatraRegEmail.getDevoteeId(), yatraRegEmail);
                        emailMember.setDbRegistrationMemberMap(DBMapNew);
                        emailMember.setRegisteredByEmail(yatraRegEmail.getRegisteredByEmail());
                    }
                    //mapEmailMemberMap.put(yatraRegEmail.getUserEmail(), emailMember);

                }else{
                    EmailMember emailMember = new EmailMember();
                    emailMember.setRegisteredByEmail(yatraRegEmail.getRegisteredByEmail());
                    Map<String, YatraRegEmail> DBMap = new HashMap<>();
                    DBMap.put(yatraRegEmail.getDevoteeId(), yatraRegEmail);
                    emailMember.setDbRegistrationMemberMap(DBMap);

                    mapEmailMemberMap.put(yatraRegEmail.getRegisteredByEmail(), emailMember);
                }
            }
            logger.info("Just for debug point");
           //emailService.sendEmailHelperRegistration(mapEmailMemberMap);

        } catch (Exception e) {
            logger.error("Send Email with Member Fetching Error {} ", e.getMessage());
        }
        return mapEmailMemberMap;
    }

    private static YatraRegEmail getRegEmail(Object[] result) {
        YatraRegEmail yatraRegEmail = new YatraRegEmail();
        String devoteeId = result[0]!=null? (String)result[0]:"";
        yatraRegEmail.setDevoteeId(devoteeId);

        String name = result[1]!=null?(String) result[1]: "";
        yatraRegEmail.setName(name);
        String dateOfBirth = result[2]!=null?(String) result[2]: "";
        yatraRegEmail.setDateOfBirth(dateOfBirth);

        String gender = result[3]!=null?(String) result[3]: "";
        yatraRegEmail.setGender(gender);
        String contact = result[4]!=null?(String) result[4]: "";
        yatraRegEmail.setContact(contact);
        String facilitator = result[5]!=null?(String) result[5]: "";
        yatraRegEmail.setFacilitator(facilitator);
        String chantingRounds = result[6]!=null?(String) result[6]: "";
        yatraRegEmail.setChantingRounds(chantingRounds);
        String registeredBy = result[7]!=null?(String) result[7]: "";
        yatraRegEmail.setRegisteredBy(registeredBy);
        String contactRegisteredBy = result[8]!=null?(String) result[8]: "";
        yatraRegEmail.setContactRegisteredBy(contactRegisteredBy);
        String registeredByEmail = result[9]!=null?(String) result[9]: "";
        yatraRegEmail.setRegisteredByEmail(registeredByEmail);
        String paidByEmail = result[10]!=null?(String) result[10]: "";
        yatraRegEmail.setPaidByEmail(paidByEmail);
        String city = result[11]!=null?(String) result[11]: "";
        yatraRegEmail.setCurrentCity(city);

        return yatraRegEmail;
    }
    public static YatraRegEmail getRegEmailTMPId(Object[] result){
        YatraRegEmail yatraRegEmail = new YatraRegEmail();
        String devoteeId = result[0]!=null? (String)result[0]:"";
        yatraRegEmail.setDevoteeId(devoteeId);

        String name = result[1]!=null?(String) result[1]: "";
        yatraRegEmail.setName(name);
        String dateOfBirth = result[2]!=null?(String) result[2]: "";
        yatraRegEmail.setDateOfBirth(dateOfBirth);

        String gender = result[3]!=null?(String) result[3]: "";
        yatraRegEmail.setGender(gender);
        String contact = result[4]!=null?(String) result[4]: "";
        yatraRegEmail.setContact(contact);
        String facilitator = result[5]!=null?(String) result[5]: "";
        yatraRegEmail.setFacilitator(facilitator);
        String chantingRounds = result[6]!=null?(String) result[6]: "";
        yatraRegEmail.setChantingRounds(chantingRounds);
        String registeredBy = result[7]!=null?(String) result[7]: "";
        yatraRegEmail.setRegisteredBy(registeredBy);
        String contactRegisteredBy = result[8]!=null?(String) result[8]: "";
        yatraRegEmail.setContactRegisteredBy(contactRegisteredBy);
        String registeredByEmail = result[9]!=null?(String) result[9]: "";
        yatraRegEmail.setRegisteredByEmail(registeredByEmail);
        String paidByEmail = result[10]!=null?(String) result[10]: "";
        yatraRegEmail.setPaidByEmail(paidByEmail);
        String city = result[11]!=null?(String) result[11]: "";
        yatraRegEmail.setCurrentCity(city);
        String accomEmail = result[12]!=null?(String) result[12]:"";
        yatraRegEmail.setAccomEmail(accomEmail);
        String accomName = result[13]!=null?(String) result[13]:"";
        yatraRegEmail.setAccomName(accomName);
        String accomPhone = result[14]!=null?(String) result[14]:"";
        yatraRegEmail.setAccomName(accomPhone);

        if(!accomEmail.isEmpty()){
            yatraRegEmail.setSendTMPIdEmailTo(accomEmail);
        }else if(!registeredByEmail.isEmpty()){
            yatraRegEmail.setSendTMPIdEmailTo(registeredByEmail);
        }else{
            yatraRegEmail.setSendTMPIdEmailTo(paidByEmail);
        }


        return yatraRegEmail;
    }

    private static YatraRegEmail getDBRegEmail(Object[] result) {
        YatraRegEmail yatraRegEmail = new YatraRegEmail();
        String devoteeId = result[0]!=null? (String)result[0]:"";
        yatraRegEmail.setDevoteeId(devoteeId);

        String name = result[1]!=null?(String) result[1]: "";
        yatraRegEmail.setName(name);
        String dateOfBirth = result[2]!=null?(String) result[2]: "";
        yatraRegEmail.setDateOfBirth(dateOfBirth);

        String gender = result[3]!=null?(String) result[3]: "";
        yatraRegEmail.setGender(gender);
        String contact = result[4]!=null?(String) result[4]: "";
        yatraRegEmail.setContact(contact);
        String facilitator = result[5]!=null?(String) result[5]: "";
        yatraRegEmail.setFacilitator(facilitator);
        String chantingRounds = result[6]!=null?(String) result[6]: "";
        yatraRegEmail.setChantingRounds(chantingRounds);
        String registeredBy = result[7]!=null?(String) result[7]: "";
        yatraRegEmail.setRegisteredBy(registeredBy);
        String contactRegisteredBy = result[8]!=null?(String) result[8]: "";
        yatraRegEmail.setContactRegisteredBy(contactRegisteredBy);
        String registeredByEmail = result[9]!=null?(String) result[9]: "";
        yatraRegEmail.setRegisteredByEmail(registeredByEmail);
        String city = result[10]!=null?(String) result[10]: "";
        yatraRegEmail.setCurrentCity(city);

        return yatraRegEmail;
    }

}