package top.lxyi.share.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import top.lxyi.share.common.resp.CommonResp;
import top.lxyi.share.content.domain.dto.ExchangeDTO;
import top.lxyi.share.content.domain.entity.MidUserShare;

import top.lxyi.share.content.domain.entity.Share;
import top.lxyi.share.content.domain.resp.ShareResp;
import top.lxyi.share.content.feign.User;
import top.lxyi.share.content.feign.UserAddBonusMsgDTO;
import top.lxyi.share.content.feign.UserService;
import top.lxyi.share.content.mapper.MidUserShareMapper;
import top.lxyi.share.content.mapper.ShareMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareService {
    @Resource
    private ShareMapper shareMapper;

    @Resource
    private MidUserShareMapper midUserShareMapper;
    @Resource
    private UserService userService;

    /**
     * 查询某个用户首页可见的资源列表
     *
     * @param title  标题
     * @param userId 用户id
     */
    public List<Share> getList(String title, Integer pageNo, Integer pageSize, Long userId) {
        // 构造查询条件
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        // 按照 id 降序查询所有数据
        wrapper.orderByDesc(Share::getId);
        // 如标题关键字不空，则加上模糊查询条件，否则结果即所有数据
        if (title != null) {
            wrapper.like(Share::getTitle, title);
        }

        // 过滤出所有已经通过审核的数据并需要显示的数据
        wrapper.eq(Share::getAuditStatus, "PASS").eq(Share::getShowFlag, true);

        // 内置的分页对象
        Page<Share> page = Page.of(pageNo,pageSize);
        // 执行按条件查询
        List<Share> shares = shareMapper.selectList(page,wrapper);
//        List<Share> shares = shareMapper.selectList(wrapper);

        // 处理后的 Share 数据列表
        List<Share> sharesDeal;
        // 1. 如果用户未登录，那么 downloadUrl 全部设为 null
        if (userId == null) {
            sharesDeal = shares.stream().peek(share -> share.setDownloadUrl(null)).collect(Collectors.toList());
        }
        // 2. 如果用户登录了，那么查询 mid_user_share，如果没有数据，那么这条 share 的 downloadUrl 也设为 null
        // 只有自己分享的资源才能直接看到下载链接，否则显示"兑换"
        else {
            sharesDeal = shares.stream().peek(share -> {
                MidUserShare midUserShare = midUserShareMapper.selectOne(new QueryWrapper<MidUserShare>().lambda()
                        .eq(MidUserShare::getUserId, userId)
                        .eq(MidUserShare::getShareId, share.getId()));

                if (midUserShare == null) {
                    share.setDownloadUrl(null);
                }
            }).collect(Collectors.toList());
        }

        return sharesDeal;
    }

    public ShareResp findById(Long shareId){
        Share share = shareMapper.selectById(shareId);
        CommonResp<User> commonResp = userService.getUser(share.getUserId());
        return ShareResp.builder().share(share).nickname(commonResp.getData().getNickname()).avatarUrl(commonResp.getData().getAvatarUrl()).build();
    }

    public Share exchange(ExchangeDTO exchangeDTO){
        Long userId = exchangeDTO.getUserId();
        Long shareId = exchangeDTO.getShareId();
        //1.根据 id 查询 share,校验需要兑换的资源是否存在
        Share share = shareMapper.selectById(shareId);
        if (share == null){
            throw new IllegalArgumentException("该分享不存在！");
        }

        //2.如果当前用户已经兑换过该分享，则直接返回该分享（不需要扣积分）
        MidUserShare midUserShare = midUserShareMapper.selectOne(new QueryWrapper<MidUserShare>().lambda()
                .eq(MidUserShare::getUserId,userId)
                .eq(MidUserShare::getShareId,shareId));
        if (midUserShare != null){
            return share;
        }
        //3.看用户积分是否足够
        CommonResp<User> commonResp=userService.getUser(userId);
        User user = commonResp.getData();
        // 兑换这条资源需要的积分
        Integer price= share.getPrice();
        //看积分是否够
        if (price> user.getBonus()){
            throw new IllegalArgumentException("用户积分不够");
        }
        //4.修改积分（*-1 就是负值扣分）
        userService.updateBonus(UserAddBonusMsgDTO.builder().userId(userId).bonus(price*-1).build());
        //5.向mid_user_share 表插入一条数据，让这个用户对于这条资源拥有了下载权限
        midUserShareMapper.insert(MidUserShare.builder().userId(userId).shareId(shareId).build());
        return share;
    }
}

