package LacunaMatata.Lacuna.dto.request.admin.Consulting;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReqRegistUpperConsultingCategoryDto {
    private String consultingUpperCategoryName;
    private String consultingUpperCategoryDescription;

    // 이미지 파일을 받는 곳
    private List<MultipartFile> insertImgs;
}
