package top.lxyi.share.user.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.lxyi.share.user.mapper.UserMapper;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public Long count(){
        return userMapper.selectCount(null);
    }
}
