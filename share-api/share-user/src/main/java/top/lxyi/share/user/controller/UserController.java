package top.lxyi.share.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lxyi.share.common.resp.CommonResp;
import top.lxyi.share.user.domain.dto.LoginDTO;
import top.lxyi.share.user.domain.dto.UserAddBonusMsgDTO;
import top.lxyi.share.user.domain.entity.BonusEventLog;
import top.lxyi.share.user.domain.entity.User;
import top.lxyi.share.user.domain.resp.UserLoginResp;
import top.lxyi.share.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/count")
    public CommonResp<Long> count() {
//        return userService.count();
        Long count = userService.count();
        CommonResp<Long> commonResp = new CommonResp<>();
        commonResp.setData(count);
        return commonResp;
    }

    @PostMapping("/login")
//    public CommonResp<User> login(@RequestBody LoginDTO LoginDTO) {
    public CommonResp<UserLoginResp> login(@Valid @RequestBody LoginDTO loginDTO) {
//        return userService.login(LoginDTO);
        UserLoginResp userLoginResp = userService.login(loginDTO);
        CommonResp<UserLoginResp> commonResp = new CommonResp<>();
        commonResp.setData(userLoginResp);
        return commonResp;
    }

    @PostMapping("/register")
    public CommonResp<Long> register(@Valid @RequestBody LoginDTO loginDTO) {
        Long id = userService.register(loginDTO);
        CommonResp<Long> commonResp = new CommonResp<>();
        commonResp.setData(id);
        return commonResp;

    }

    @GetMapping("/{id}")
    public CommonResp<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        CommonResp<User> commonResp = new CommonResp<>();
        commonResp.setData(user);
        return commonResp;
    }

    @PutMapping(value = "/update-bonus")
    public CommonResp<User> updateBonus(@RequestBody UserAddBonusMsgDTO userAddBonusMsgDTO) {
        Long userId = userAddBonusMsgDTO.getUserId();
        userService.updateBonus(
                userAddBonusMsgDTO.builder()
                        .userId(userId)
                        .bonus(userAddBonusMsgDTO.getBonus())
                        .description("兑换分享")
                        .event("BUY")
                        .build()
        );
        CommonResp<User> commonResp = new CommonResp<>();
        commonResp.setData(userService.findById(userId));
        return commonResp;
    }
//    积分明细接口
    @GetMapping(value = "/getBonus")
    public CommonResp<List<BonusEventLog>> getBonusEventLogs(@RequestParam Long id) {
        List<BonusEventLog> list = userService.getBonusEventLog(+id);
        CommonResp<List<BonusEventLog>> resp = new CommonResp<>();
        resp.setData(list);
        return resp;
    }



}





