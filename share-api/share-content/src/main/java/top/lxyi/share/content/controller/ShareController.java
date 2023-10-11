package top.lxyi.share.content.controller;

import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.lxyi.share.common.resp.CommonResp;
import top.lxyi.share.common.util.JwtUtil;
import top.lxyi.share.content.domain.entity.Notice;
import top.lxyi.share.content.domain.entity.Share;
import top.lxyi.share.content.service.NoticeService;
import top.lxyi.share.content.service.ShareService;

import java.util.List;




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
}
