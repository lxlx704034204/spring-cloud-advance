package com.sprainkle.ueh.licence.controllerTest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/valid")
public class TestController {

    @PostMapping("/new")
    public Object insertTicket(@RequestBody TestUser testUser) {
        return null;
    }

}
