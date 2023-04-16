package com.voice.dbRegistration.restController;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.common.Constants;
import com.voice.dbRegistration.dao.PermittedUsersDao;
import com.voice.dbRegistration.model.PermittedUsers;


@RestController
@RequestMapping("/v1/hlzGlobalReg")
@CrossOrigin(origins = "*")
public class PermittedUsersRestController {

    @Autowired
    PermittedUsersDao permittedUsersDao;
    
    @PostMapping("/addPermittedUser")
    public PermittedUsers savePermittedUser(@RequestBody PermittedUsers user){
        //one time setup        
        // List<String> allUsersEmail = Constants.permittedUserList;
		// for(String one:allUsersEmail){
		// 	PermittedUsers newPUser = new PermittedUsers();
		// 	newPUser.setEmail(one);
		// 	permittedUsersDao.save(newPUser);
		// }

        // return null;
        return permittedUsersDao.save(user);
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
