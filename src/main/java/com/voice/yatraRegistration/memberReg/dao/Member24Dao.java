package com.voice.yatraRegistration.memberReg.dao;

import com.voice.yatraRegistration.memberReg.model.Member24;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

    public interface Member24Dao extends JpaRepository<Member24,Long>,Member24DaoCustom {

//    @Query("SELECT mem FROM Member mem GROUP BY mem.dbDevId HAVING count(*)>1")
//    public List<Member24> findDuplicates();

        public Member24 findOneByDbDevId(String dbDevId);


}
