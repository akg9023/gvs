package com.voice.dbRegistration.restController;

import java.util.List;
import java.util.Map;

import com.voice.dbRegistration.model.PermittedUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.dbRegistration.dao.PermittedUsersDao;


@RestController
@RequestMapping("/v1/hlzGlobalReg")
@CrossOrigin(origins = "*")
public class PermittedUsersRestController {

    @Autowired
    PermittedUsersDao permittedUsersDao;

    @PostMapping("/addPermittedUser")
    public List<String> savePermittedUser(@RequestBody Map<String,List<String>> userMap){
        List<String> allUsersEmail = userMap.get("email");
		for(String one:allUsersEmail){
			PermittedUsers newPUser = new PermittedUsers();
			newPUser.setEmail(one);
			permittedUsersDao.save(newPUser);
		}
        return allUsersEmail;
    }

    @PostMapping("/permittedUser")
    public List<PermittedUsers> getAllPermittedUser(){
        return permittedUsersDao.findAll();
    }

    @PostMapping("/checkPermission")
    public boolean isPermittedUser(@RequestBody Map<String, String> input) {
        String email = input.get("email");
        return permittedUsersDao.getByEmail(email)!=null?true:false;
    }
}