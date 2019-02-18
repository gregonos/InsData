package net.windia.insdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/analytics")
    public String analytics() {
        return "analytics";
    }

    @RequestMapping("/BXFR")
    public String bxfr() {
        return "bxfr";
    }
}
