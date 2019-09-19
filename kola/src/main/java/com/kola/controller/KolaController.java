package com.kola.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//@Controller
//@RequestMapping("/kola")
@RestController
public class KolaController {

    @RequestMapping(value = "/dis", method = RequestMethod.GET)
    public String dis() {
        return "dis";
    }

}
