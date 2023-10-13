package top.lxyi.share.content.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareRequestDTO {
    private Long userId;
    private String author;
    private String title;
    private Boolean isOriginal;
    private Integer price;
    private String downloadUrl;
    private String cover;
    private String summary;
}
