package top.lxyi.share.content.feign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Long id;

    private String nickname;

    private String avatarUrl;

    private Integer bonus;
    private String roles;

}
