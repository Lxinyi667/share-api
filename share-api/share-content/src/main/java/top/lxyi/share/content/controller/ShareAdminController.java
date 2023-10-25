package top.lxyi.share.content.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import top.lxyi.share.common.resp.CommonResp;
import top.lxyi.share.content.auth.CheckAuth;
import top.lxyi.share.content.domain.dto.ShareAuditDTO;
import top.lxyi.share.content.domain.entity.Share;
import top.lxyi.share.content.service.ShareService;

import java.util.List;

@RestController
@RequestMapping("/share/admin")
public class ShareAdminController {

    @Resource
    private ShareService shareService;

    @GetMapping(value = "/list")
    @CheckAuth("admin")
    public CommonResp<List<Share>> getSharesNotYet() {
        CommonResp<List<Share>> commonResp = new CommonResp<>();
        commonResp.setData(shareService.querySharesNotYet());
        return commonResp;
    }

    @PostMapping(value = "/audit/{id}")
    @CheckAuth(value = "admin")
    public CommonResp<Share> auditById(@PathVariable Long id, @RequestBody ShareAuditDTO auditDTO){
        CommonResp<Share> commonResp = new CommonResp<>();
        commonResp.setData(shareService.auditById(id,auditDTO));
        return commonResp;
    }

}
