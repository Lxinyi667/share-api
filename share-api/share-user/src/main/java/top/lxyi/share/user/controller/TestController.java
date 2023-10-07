package top.lxyi.share.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/hello")
//    public String hello(){
//        return "Hello Wrold!";
//    }
//    制造异常
        public String hello(){
            int a = 1 / 0;
            return "Hello Wrold!";
        }
}
