package top.lxyi.share.common.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResp<T> {
    private Boolean success = true;

    private String message;
    private T data;
}
