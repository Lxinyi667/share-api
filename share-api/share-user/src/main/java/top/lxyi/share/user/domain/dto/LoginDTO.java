package top.lxyi.share.user.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {
    //    校验字段

    @NotBlank(message = "[手机号] 不能为空")
    private String phone;

    private String password;



}
