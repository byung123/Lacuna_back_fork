package LacunaMatata.Lacuna.dto.request.admin.Consulting;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReqRegistConsultingSurveyDto {
    private int consultingUpperCategoryId;
    private int consultingLowerCategoryId;
    private String consultingCode;
    private String consultingTitle;
    private String consultingSubtitle;
    private String consultingOptionType;

    private List<ReqConsultingSurveyOptionDto> consultingOption;

    // 이미지 파일 저장
    private List<MultipartFile> insertImgs;
}
