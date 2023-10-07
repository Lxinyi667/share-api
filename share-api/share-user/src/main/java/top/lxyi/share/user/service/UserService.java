package top.lxyi.share.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.lxyi.share.user.domain.dto.LoginDTO;
import top.lxyi.share.user.domain.entity.User;
import top.lxyi.share.user.mapper.UserMapper;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public Long count(){
        return userMapper.selectCount(null);
    }
    public User login(LoginDTO loginDTO){
        User userDB= userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getPhone,loginDTO.getPhone()));
        if (userDB == null){
            throw new RuntimeException("手机号不存在");
        }
        if (!userDB.getPassword().equals(loginDTO.getPassword())){
            throw new RuntimeException("密码错误");
        }
        return userDB;
    }
}
