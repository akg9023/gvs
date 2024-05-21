//package com.voice.attendance.restController;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import com.voice.attendance.dao.JapaParticipantsDao;
//import com.voice.attendance.models.JapaParticipants;
//
//@RestController
//@RequestMapping("/v1/participants/japa")
//@CrossOrigin(origins = "*")
//public class JapaParticipanatsRestController {
//
//    @Autowired
//    JapaParticipantsDao devDao;
//
//    private void getAllFacilites(Long userId,List<Map<String,Object>> facilitesList){
//
//        List<Map<String,Object>> directFacilities = devDao.findAllFacilitiesId(userId);
//
//        if(directFacilities == null){
//           return ;
//        }
//
//        facilitesList.addAll(directFacilities);
//        for(Map<String,Object> oneMem:directFacilities){
//            getAllFacilites((Long)oneMem.get("id"),facilitesList);
//        }
//
//    }
//
//    @PostMapping("/facilitesId")
//    public List<Map<String,Object>> getFaciliteesId(@RequestParam("loginEmail") String loginEmail){
//        JapaParticipants loginPerson = devDao.findOneByEmail(loginEmail);
//        List<Map<String,Object>> facilitiesList = new ArrayList<>();
//
//        if(loginPerson == null){
//            return null;
//        }
//
//        getAllFacilites(loginPerson.getId(),facilitiesList);
//
//        // adding the login user in the list
//        Map<String,Object> self = new HashMap<>();
//        self.put("id", loginPerson.getId());
//        self.put("name", loginPerson.getName());
//        facilitiesList.add(self);
//
//        return facilitiesList;
//    }
//
//    @PostMapping("/saveDevotee")
//    public JapaParticipants saveDevotee(@RequestBody JapaParticipants attendanceDevotee) {
//        return devDao.save(attendanceDevotee);
//    }
//
//    @PostMapping("/fetchAll")
//    public List<JapaParticipants> fetchAll() {
//        return devDao.findAll();
//    }
//
//    @PostMapping("/saveListDevotee")
//    public List<JapaParticipants> saveListDevotee(@RequestBody List<JapaParticipants> listParticipants){
//
//        List<JapaParticipants> result = new ArrayList<>();
//        for(JapaParticipants jp:listParticipants){
//            result.add(devDao.save(jp));
//        }
//
//        return result;
//
//    }
//}