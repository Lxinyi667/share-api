package top.lxyi.share.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.lxyi.share.common.exception.BusinessException;
import top.lxyi.share.common.exception.BusinessExceptionEnum;
import top.lxyi.share.common.util.JwtUtil;
import top.lxyi.share.common.util.SnowUtil;
import top.lxyi.share.user.domain.dto.LoginDTO;
import top.lxyi.share.user.domain.dto.UserAddBonusMsgDTO;
import top.lxyi.share.user.domain.entity.BonusEventLog;
import top.lxyi.share.user.domain.entity.User;
import top.lxyi.share.user.domain.resp.UserLoginResp;
import top.lxyi.share.user.mapper.BonusEventLogMapper;
import top.lxyi.share.user.mapper.UserMapper;

import java.util.Date;
import java.util.List;
import java.util.Map;




@Service
@Slf4j
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private BonusEventLogMapper bonusEventLogMapper;
//增加修改用户积分方法
    @Transactional(rollbackFor = Exception.class)
    public void updateBonus(UserAddBonusMsgDTO userAddBonusMsgDTO){
        System.out.println(userAddBonusMsgDTO);
        //1.为用户修改积分
        Long userId = userAddBonusMsgDTO.getUserId();
        Integer bonus = userAddBonusMsgDTO.getBonus();
        User user = userMapper.selectById(userId);
        user.setBonus(user.getBonus() + bonus);
        userMapper.update(user,new QueryWrapper<User>().lambda().eq(User::getId,userId));
        //2.记录日志到bonus_event_log 表里
        bonusEventLogMapper.insert(
                BonusEventLog.builder()
                        .userId(userId)
                        .value(bonus)
                        .description(userAddBonusMsgDTO.getDescription())
                        .event(userAddBonusMsgDTO.getEvent())
                        .createTime(new Date())
                        .build()
        );
        log.info("积分添加完毕...");
    }
    public Long count(){
        return userMapper.selectCount(null);
    }

    public User findById(Long userId){
        return userMapper.selectById(userId);
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
    //    积分明细接口
    public List<BonusEventLog> getBonusEventLog(Long userId) {
        LambdaQueryWrapper<BonusEventLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BonusEventLog::getUserId, userId);
        List<BonusEventLog> bonusEventLogList = bonusEventLogMapper.selectList(wrapper);
        return bonusEventLogList;
    }
}
