package com.sprainkle.ueh.licence.controllerTest;

import org.springframework.web.bind.annotation.*;

// AOP注解的方式进行接口参数统一校验 ValidationBean.class   ResponseInterceptor
@RestController
@RequestMapping(value = "/valid")
public class TestController {

    @PostMapping("/new")
    public Object insertTicket(@RequestBody TestUser testUser) {
        return null;
    }

}
