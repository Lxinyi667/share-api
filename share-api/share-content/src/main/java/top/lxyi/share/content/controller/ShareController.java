package top.lxyi.share.content.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.lxyi.share.common.resp.CommonResp;
import top.lxyi.share.common.util.JwtUtil;
import top.lxyi.share.content.domain.dto.ExchangeDTO;
import top.lxyi.share.content.domain.dto.ShareRequestDTO;
import top.lxyi.share.content.domain.entity.MidUserShare;
import top.lxyi.share.content.domain.entity.Notice;
import top.lxyi.share.content.domain.entity.Share;
import top.lxyi.share.content.domain.resp.ShareResp;
import top.lxyi.share.content.service.NoticeService;
import top.lxyi.share.content.service.ShareService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/share")
@Slf4j
public class ShareController {
    @Resource
    private NoticeService noticeService;
    @Resource
    private ShareService shareService;

    private final int MAX = 100;
    @GetMapping(value = "/notice")
    public CommonResp<Notice> getLatestNotice(){
        CommonResp<Notice> commonResp = new CommonResp<>();
        commonResp.setData(noticeService.getLatest());
        return commonResp;
    }

    @GetMapping(value = "/list")
    public CommonResp<List<Share>> getShareList(@RequestParam(required = false )String title,
                                                @RequestParam(required = false,defaultValue = "1")Integer pageNo,
                                                @RequestParam(required = false,defaultValue = "3")Integer pageSize,
                                                @RequestHeader(value = "token",required = false )String token){
        log.info(token.toString()+"23232");
        if (pageSize >MAX){
            pageSize = MAX;
        }
        Long userId = getUserIdFromToken(token);
//        long userId=1;
        CommonResp<List<Share>> commonResp = new CommonResp<>();
        commonResp.setMessage("1111");
        commonResp.setData(shareService.getList(title,pageNo,pageSize,userId));
        return commonResp;
    }

    private Long  getUserIdFromToken(String token) {
        log.info(">>>>>>>>>>>> token" + token);
        long userId =0;
        String noToken = "no-token";
       if(token.equals("")){
           return null;
       }
        if (!noToken.equals(token)){
            JSONObject jsonObject = JwtUtil.getJSONObject(token);
            log.info("解析到token的json数据为：{}",jsonObject);
            userId = Long.parseLong(jsonObject.get("id").toString());

        }else {
            log.info("没有 token");
        }
        return userId;
    }
    @GetMapping("/{id}")
    public CommonResp<ShareResp> getShareById(@PathVariable Long id){
        ShareResp shareResp = shareService.findById(id);
        CommonResp<ShareResp> commonResp = new CommonResp<>();
        commonResp.setData(shareResp);
        return commonResp;
    }
    @PostMapping("/exchange")
    public CommonResp<Share> exchange(@RequestBody ExchangeDTO exchangeDTO){
        System.out.println(exchangeDTO);
        CommonResp<Share> commonResp = new CommonResp<>();
        commonResp.setData(shareService.exchange(exchangeDTO));
        return commonResp;
    }
//    投稿接口
    @PostMapping("/contribute")
    public int contributeShare(@RequestBody ShareRequestDTO shareRequestDTO,
                               @RequestHeader(value = "token", required = false) String token){
        long userId = getUserIdFromToken(token);
        shareRequestDTO.setUserId(userId);
        System.out.println(shareRequestDTO);
        return shareService.contribute(shareRequestDTO);
    }
    @GetMapping("/my-contribute")
    public CommonResp<List<Share>> myContribute(
            @RequestParam(required = false,defaultValue = "1") Integer pageNo,
            @RequestParam(required = false,defaultValue = "3") Integer pageSize,
            @RequestHeader(value = "token",required = false) String token) {
     if (pageSize > MAX){
         pageSize = MAX;
     }
     long userId = getUserIdFromToken(token);
     CommonResp<List<Share>> commonResp = new CommonResp<>();
     commonResp.setData(shareService.myContribute(pageNo,pageSize,userId));
     return commonResp;
    }
//    我的兑换接口

    @GetMapping("/myExchange")
    public CommonResp<List<Share>> myExchange(
            @RequestHeader(value = "token", required = false) String token
    ) {
        long id = getUserIdFromToken(token);
        List<Share> shares = shareService.myExchange(id);
        CommonResp<List<Share>> resp = new CommonResp<>();
        resp.setData(shares);
        return resp;
    }






}
