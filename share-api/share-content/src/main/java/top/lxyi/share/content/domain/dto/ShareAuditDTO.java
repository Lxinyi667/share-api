package top.lxyi.share.content.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.lxyi.share.content.domain.enums.AuditStatusEnum;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareAuditDTO {
    private AuditStatusEnum auditStatusEnum;

    private String reason;

    private Boolean showFlag;
}
