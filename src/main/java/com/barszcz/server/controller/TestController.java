package com.barszcz.server.controller;

import com.barszcz.server.dao.BilionaireDao;
import com.barszcz.server.entity.Bilionare;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class TestController {


    private BilionaireDao bilionaireDao;



//    @GetMapping(path = "/test")
//    public String test() throws DataAccessException {
////        return testString;
////        return new ResponseEntity<>(dsl.select(USER.USERNAME).from(USER).fetchAny().into(String.class), HttpStatus.OK);
//    }

    @GetMapping(path = "/find")
    public String getTestString(){
        return bilionaireDao.findAll().toString();
    }

    @GetMapping(path = "/add")
    public String postTestString(@RequestParam String message){
        Bilionare bilionare = new Bilionare();
        bilionare.setName(message);
        bilionaireDao.save(bilionare);
        return  bilionaireDao.findAll().toString();

    }

}
