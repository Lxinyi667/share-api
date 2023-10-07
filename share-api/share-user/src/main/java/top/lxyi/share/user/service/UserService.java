package top.lxyi.share.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.lxyi.share.common.exception.BusinessException;
import top.lxyi.share.common.exception.BusinessExceptionEnum;
import top.lxyi.share.common.util.JwtUtil;
import top.lxyi.share.common.util.SnowUtil;
import top.lxyi.share.user.domain.dto.LoginDTO;
import top.lxyi.share.user.domain.entity.User;
import top.lxyi.share.user.domain.resp.UserLoginResp;
import top.lxyi.share.user.mapper.UserMapper;

import java.util.Date;
import java.util.Map;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public Long count(){
        return userMapper.selectCount(null);
    }
    public UserLoginResp login(LoginDTO loginDTO){
        User userDB= userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getPhone,loginDTO.getPhone()));
        if (userDB == null){
//            throw new RuntimeException("手机号不存在");
        throw new BusinessException(BusinessExceptionEnum.PHONE_NOT_EXIST);
        }
        if (!userDB.getPassword().equals(loginDTO.getPassword())){
//            throw new RuntimeException("密码错误");
        throw new BusinessException(BusinessExceptionEnum.PASSWORD_ERROR);
        }
//        都正确，返回
        UserLoginResp userLoginResp = UserLoginResp.builder()
                .user(userDB)
                .build();
//        String key ="InfinityX7";
//        Map<String,Object> map =BeanUtil.beanToMap(userLoginResp);
//        String token =JWTUtil.createToken(map,key.getBytes());
        String token = JwtUtil.createToken(userLoginResp.getUser().getId(),userLoginResp.getUser().getPhone());
        userLoginResp.setToken(token);
        return userLoginResp;
    }

    public Long register(LoginDTO loginDTO){
//        根据手机号查询用户
        User userDB= userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getPhone,loginDTO.getPhone()));
        //        找到了手机号已经被注册
        if (userDB != null){
            throw new BusinessException(BusinessExceptionEnum.PHONE_EXIST);
        }
        User savedUser = User.builder()
                //        使用雪花算法生成id
                .id(SnowUtil.getSnowflakeNextId())
                .phone(loginDTO.getPhone())
                .password(loginDTO.getPassword())
                .nickname("新用户")
                .roles("user")
                .avatarUrl("https://i2.100024.xyz/2023/01/26/3exzjl.webp")
                .bonus(100)
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        userMapper.insert(savedUser);
        return savedUser.getId();


    }
}
