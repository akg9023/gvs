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

public interface DevoteeInfoDao extends JpaRepository<DevoteeInfo, String> {
//   Todo need to check query
//    @Query(value = "select * FROM DevoteeInfo where connectedTo like '%guru%'")
//    public DevoteeInfo findByEmail(String email);
    public DevoteeInfo findByEmailAndConnectedTo(String email, String connectedTo);
    public DevoteeInfo findOneById(String id);

    public void deleteAllByEmail(String email);

    boolean existsByEmail(String email);

    List<DevoteeInfo> findAllByConnectedTo(String Id);

    @Query(value = "SELECT new com.voice.dbRegistration.model.GetIDFnameGender(d.id,d.fname,d.gender,d.age) FROM DevoteeInfo d")
    public List<GetIDFnameGender> findAllDev();

    @Query(value = "select max(d.id) FROM DevoteeInfo d")
    public String getLastDevId();

    @Query("SELECT di FROM DevoteeInfo di WHERE di.createdDateTime >= :startDate AND di.createdDateTime <= :endDate")
    List<DevoteeInfo> findAllByCreatedDateTimeBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new com.voice.auth.model.DevoteeInfoMigration(di.id, di.fname, di.email, di.createdDateTime) " +
            "FROM DevoteeInfo di " +
            "WHERE di.email = :email AND di.connectedTo = 'guru'")
    public DevoteeInfoMigration getDevoteeInfoForUserAuth(@Param("email") String email);
}