package top.lxyi.share.user.rocketmq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import top.lxyi.share.user.domain.dto.UserAddBonusMQDTO;
import top.lxyi.share.user.domain.dto.UserAddBonusMsgDTO;
import top.lxyi.share.user.domain.entity.BonusEventLog;
import top.lxyi.share.user.domain.entity.User;
import top.lxyi.share.user.mapper.BonusEventLogMapper;
import top.lxyi.share.user.mapper.UserMapper;

import java.util.Date;

@Service
@RocketMQMessageListener(consumerGroup = "test-group",topic = "add-bonus")
@Slf4j
public class AddBonusListener  implements RocketMQListener<UserAddBonusMQDTO> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private BonusEventLogMapper bonusEventLogMapper;

    @Override
    public void onMessage(UserAddBonusMQDTO userAddBonusMQDTO){
        log.info(String.valueOf(userAddBonusMQDTO));
        //1.为用户加积分
        Long userId = userAddBonusMQDTO.getUserId();
        User user =userMapper.selectById(userId);
        user.setBonus(user.getBonus() + userAddBonusMQDTO.getBonus());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId,userId);
        userMapper.update(user,wrapper);
        //2.新增积分日志
        bonusEventLogMapper.insert(BonusEventLog.builder()
                        .userId(userId)
                        .value(userAddBonusMQDTO.getBonus())
                        .event("CONTRIBUTE")
                        .createTime(new Date())
                        .description("投稿加积分")
                        .build()
        );
    }
}
