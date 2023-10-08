package top.lxyi.share.content.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Notice {
    public Long id;
    private String content;
    private Boolean showFlag;
    @JsonFormat(locale = "zh", timezone = "GMT+8" , pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
