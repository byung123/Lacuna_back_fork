package LacunaMatata.Lacuna.dto.request.admin.mbti;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReqModifyUpperConsulingCategoryDto {
    private int consultingUpperCategoryId;
    private String consultingUpperCategoryName;
    private String consultingUpperCategoryDescription;

    // 이미지 파일을 받는 곳 (신규 업로드, 삭제)
    private List<MultipartFile> insertImgs;
    private String deleteImgPath;
    private String prevImgPath;
}
