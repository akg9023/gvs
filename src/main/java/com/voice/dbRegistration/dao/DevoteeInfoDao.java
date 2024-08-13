package com.voice.dbRegistration.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.voice.auth.model.DevoteeInfoMigration;
import com.voice.dbRegistration.model.GetIDFnameGender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.voice.dbRegistration.model.DevoteeInfo;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface DevoteeInfoDao extends JpaRepository<DevoteeInfo, String> {
//   Todo need to check query
//    @Query(value = "select * FROM DevoteeInfo where connectedTo like '%guru%'")
//    public DevoteeInfo findByEmail(String email);
    public DevoteeInfo findByEmailAndConnectedTo(String email, String connectedTo);
    public DevoteeInfo findOneById(String id);


    public void deleteAllByEmail(String email);

    boolean existsByEmail(String email);

    List<DevoteeInfo> findAllByConnectedTo(String Id);

    @Query(value = "SELECT new com.voice.dbRegistration.model.GetIDFnameGender(d.id,d.fname,d.gender,d.dateOfBirth) FROM DevoteeInfo d")
    public List<GetIDFnameGender> findAllDev();

    @Query(value = "SELECT new com.voice.dbRegistration.model.GetIDFnameGender(d.id,d.fname,d.gender,d.dateOfBirth) FROM DevoteeInfo d where d.id = :devId AND d.id < 'TMP'")
    public GetIDFnameGender findDev(@Param("devId") String devId);
    @Query(value = "SELECT new com.voice.dbRegistration.model.GetIDFnameGender(d.id,d.fname,d.gender,d.dateOfBirth) FROM DevoteeInfo d where (d.connectedTo = :devId OR d.id = :devId) AND d.id < 'TMP'")
    public List<GetIDFnameGender> findDevHavingConnectedTo(@Param("devId") String devId);

    @Query(value="SELECT di.devotee_id, CONCAT(di.fname, ' ', di.mname, ' ', di.lname) AS Name, di.age, IF(di.whatsapp_phone <> '', di.whatsapp_phone, di.primary_phone) AS contact, di.facilitator, di.chanting_rounds, CONCAT(cdi.fname, ' ', cdi.mname,' ', cdi.lname) AS Registered_by,"
    +"IF (cdi.whatsapp_phone <> '', cdi.whatsapp_phone,cdi.primary_phone) AS Contact_Registered_by,"
    +"IFNULL(cdi.email, di.email) as registered_by_email, CONCAT(ca.city, ', ', ca.state) AS Current_City from devotee_info di "
           + "JOIN address ca ON di.current_address_address_id = ca.address_id LEFT JOIN devotee_info cdi ON di.connected_to = cdi.devotee_id WHERE di.created_date_time >= :startDate AND di.created_date_time <= :endDate",nativeQuery = true)
    List<Object> findAllByCreatedDateTimeBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new com.voice.auth.model.DevoteeInfoMigration(di.id, di.fname, di.email, di.createdDateTime) " +
            "FROM DevoteeInfo di " +
            "WHERE di.email = :email AND di.connectedTo = 'guru'")
    public DevoteeInfoMigration getDevoteeInfoForUserAuth(@Param("email") String email);
}