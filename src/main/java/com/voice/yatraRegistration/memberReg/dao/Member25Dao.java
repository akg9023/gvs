package com.voice.yatraRegistration.memberReg.dao;

import com.voice.yatraRegistration.memberReg.model.Member25;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Member25Dao extends JpaRepository<Member25,Long>, Member25DaoCustom {

//    @Query("SELECT mem FROM Member mem GROUP BY mem.dbDevId HAVING count(*)>1")
//    public List<Member24> findDuplicates();

        public Member25 findOneByDbDevId(String dbDevId);


}
