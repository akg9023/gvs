package com.voice.dbRegistration.dao;

import com.voice.dbRegistration.model.PermittedUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermittedUsersDao extends  JpaRepository<PermittedUsers, String> {
    public PermittedUsers getByEmail(String email);


    @Query(value = "SELECT COALESCE(user.devotee_id,'') AS user_id, " +
            "COALESCE(user.email,'') AS user_email, " +
            "COALESCE(user.fname,'') AS user_fname, " +
            "COALESCE(user.lname,'') AS user_lname, " +
            "COALESCE(di2.devotee_id,'') AS member_id, " +
            "COALESCE(di2.fname,''), " +
            "COALESCE(di2.lname,''), " +
            "COALESCE(di2.date_of_birth,''), " +
            "COALESCE(di2.primary_phone,''), " +
            "COALESCE(di2.gender,''), " +
            "COALESCE (city, '')" +
            "FROM (SELECT di.devotee_id, pu.email, di.fname, di.lname " +
            "      FROM permitted_users pu " +
            "      JOIN devotee_info di ON pu.email = di.email " +
            "      WHERE di.connected_to = 'guru') AS user " +
            "JOIN devotee_info di2 ON user.devotee_id = di2.connected_to " +
            "JOIN address a ON a.address_id = di2.current_address_address_id",
            nativeQuery = true)
    public List<Object[]> getAllUserWithMembersRegistredDB();
}