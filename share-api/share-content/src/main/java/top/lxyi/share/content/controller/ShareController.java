package top.lxyi.share.content.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.lxyi.share.common.resp.CommonResp;
import top.lxyi.share.content.domain.entity.Notice;
import top.lxyi.share.content.domain.entity.Share;
import top.lxyi.share.content.service.NoticeService;
import top.lxyi.share.content.service.ShareService;

import java.util.List;

@RestController
@RequestMapping("/share")
public class ShareController {
    @Resource
    private NoticeService noticeService;
    @Resource
    private ShareService shareService;
    @GetMapping(value = "/notice")
    public CommonResp<Notice> getLatestNotice(){
        CommonResp<Notice> commonResp = new CommonResp<>();
        commonResp.setData(noticeService.getLatest());
        return commonResp;
    }
    @GetMapping(value = "/list")
    public CommonResp<List<Share>> getShareList(@RequestParam(required = false )String title){
        CommonResp<List<Share>> commonResp = new CommonResp<>();
        Long userId = 1L;
        commonResp.setData(shareService.getList(title,userId));
        return commonResp;
    }
}
