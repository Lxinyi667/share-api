package top.lxyi.share.content.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.lxyi.share.common.resp.CommonResp;

@FeignClient(value = "user-service",path = "/user")
public interface UserService {
    /**
     * 调用用户中心查询用户信息接口
     */
    @GetMapping("/{id}")
    CommonResp<User> getUser(@PathVariable Long id);
    /**
     * 调用用户中心修改用户积分接口
     */
    @PutMapping("/update-bonus")
    CommonResp<User> updateBonus(@RequestBody UserAddBonusMsgDTO userAddBonusMsgDTO);
}
