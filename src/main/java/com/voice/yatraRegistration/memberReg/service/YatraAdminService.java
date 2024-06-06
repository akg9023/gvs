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
    public Optional<List<DevoteeInfo>> getDevoteeInfoWithingDateRange(String startDateStr, String endDateStr){
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
    public Map<String, EmailMember> sendEmailWithMemberDetails() {
        //Who registered his email as key
        Map<String, EmailMember> mapEmailMemberMap = new HashMap<>();

        try {
            List<Object[]> results = regMemDao.getAllUserWithMembersRegistredYatra();
            for(Object[] result : results){
                YatraRegEmail yatraRegEmail = getRegEmail(result);

                if(mapEmailMemberMap.containsKey(yatraRegEmail.getUserEmail())){
                    EmailMember emailMember = mapEmailMemberMap.get(yatraRegEmail.getUserEmail());
                    Map<String, YatraRegEmail> yatraMap = emailMember.getYatraRegistrationMemberMap();

                    yatraMap.put(yatraRegEmail.getMemberId(),yatraRegEmail);
                }else{
                    EmailMember emailMember = new EmailMember();
                    emailMember.setId(yatraRegEmail.getUserId());
                    emailMember.setEmail(yatraRegEmail.getUserEmail());
                    emailMember.setUserFname(yatraRegEmail.getUserFname());
                    emailMember.setUserLname(yatraRegEmail.getUserLname());

                    Map<String, YatraRegEmail> yatraMap = new HashMap<>();
                    yatraMap.put(yatraRegEmail.getMemberId(), yatraRegEmail);
                    emailMember.setYatraRegistrationMemberMap(yatraMap);

                    mapEmailMemberMap.put(yatraRegEmail.getUserEmail(), emailMember);
                }

            }
            List<Object[]> resultsDB = permittedUsersDao.getAllUserWithMembersRegistredDB();
            for(Object[] result : resultsDB){
                YatraRegEmail yatraRegEmail = getDBRegEmail(result);

                if(mapEmailMemberMap.containsKey(yatraRegEmail.getUserEmail())){

                    EmailMember emailMember = mapEmailMemberMap.get(yatraRegEmail.getUserEmail());
                    Map<String, YatraRegEmail> DBMap = emailMember.getDbRegistrationMemberMap();

                    if(DBMap!=null){
                        DBMap.put(yatraRegEmail.getMemberId(),yatraRegEmail);
                    }else{
                        Map<String, YatraRegEmail> DBMapNew = new HashMap<>();
                        DBMapNew.put(yatraRegEmail.getMemberId(), yatraRegEmail);
                        emailMember.setDbRegistrationMemberMap(DBMapNew);
                    }
                    //mapEmailMemberMap.put(yatraRegEmail.getUserEmail(), emailMember);

                }else{
                    EmailMember emailMember = new EmailMember();
                    emailMember.setId(yatraRegEmail.getUserId());
                    emailMember.setEmail(yatraRegEmail.getUserEmail());
                    emailMember.setUserFname(yatraRegEmail.getUserFname());
                    emailMember.setUserLname(yatraRegEmail.getUserLname());

                    Map<String, YatraRegEmail> DBMap = new HashMap<>();
                    DBMap.put(yatraRegEmail.getMemberId(), yatraRegEmail);
                    emailMember.setDbRegistrationMemberMap(DBMap);

                    mapEmailMemberMap.put(yatraRegEmail.getUserEmail(), emailMember);
                }
            }

            emailService.sendEmailHelperRegistration(mapEmailMemberMap);

        } catch (Exception e) {
            logger.error("Send Email with Member Fetching Error {} ", e.getMessage());
        }
        return mapEmailMemberMap;
    }

    private static YatraRegEmail getRegEmail(Object[] result) {
        YatraRegEmail yatraRegEmail = new YatraRegEmail();
        yatraRegEmail.setUserId((String) result[1]);
        yatraRegEmail.setUserEmail((String) result[2]);
        yatraRegEmail.setUserFname((String) result[3]);
        yatraRegEmail.setUserLname((String) result[4]);
        yatraRegEmail.setMemberId((String) result[5]);
        yatraRegEmail.setFname((String) result[6]);
        yatraRegEmail.setLname((String) result[7]);
        yatraRegEmail.setDateOfBirth((String) result[8]);
        yatraRegEmail.setPrimaryPhone((String) result[9]);
        yatraRegEmail.setGender((String) result[10]);
        yatraRegEmail.setCity((String) result[11]);
        return yatraRegEmail;
    }

    private static YatraRegEmail getDBRegEmail(Object[] result) {
        YatraRegEmail yatraRegEmail = new YatraRegEmail();
        yatraRegEmail.setUserId((String) result[0]);
        yatraRegEmail.setUserEmail((String) result[1]);
        yatraRegEmail.setUserFname((String) result[2]);
        yatraRegEmail.setUserLname((String) result[3]);
        yatraRegEmail.setMemberId((String) result[4]);
        yatraRegEmail.setFname((String) result[5]);
        yatraRegEmail.setLname((String) result[6]);
        yatraRegEmail.setDateOfBirth((String) result[7]);
        yatraRegEmail.setPrimaryPhone((String) result[8]);
        yatraRegEmail.setGender((String) result[9]);
        yatraRegEmail.setCity((String) result[10]);
        return yatraRegEmail;
    }

}