package io.jnyou.springsecurity.controller;

import io.jnyou.springsecurity.common.HttpResult;
import io.jnyou.springsecurity.util.TokenUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代码全万行，注释第一行
 * 注释不规范，同事泪两行
 *
 * @author JnYou
 * @version 1.0.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/register")
    public HttpResult<String> register() {

        return HttpResult.success();
    }


    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String newPWD = passwordEncoder.encode("123456");
        System.out.println(newPWD);
        // $2a$10$4Qx1LLVlt0A20xnnwaZMkeqmK12NAdSjHx./yz4fgFEytPvb2I3Ja
        // $2a$10$BEyj.rADPFkKtrfnSmr.aetFL27oi9EzdIyYS6M3eFAaMCN1d/nZS

        String token = TokenUtil.generator("1");
        System.out.println(token);
        // f17c333eff62d37a38a71b40b396b10e
    }
}
