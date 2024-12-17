package LacunaMatata.Lacuna.dto.request.admin.Consulting;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReqModifyConsultingSurveyInfoDto {
    private int consultingId;
    private String consultingCode;
    private String consultingTitle;
    private String consultingSubtitle;
    private String consultingOptionType;

    private List<ReqModifyConsultingSurveyOptionDto> consultingOption;

    // 이미지 파일을 받는 곳 (신규 업로드, 삭제)
    private List<MultipartFile> insertImgs;
    private String deleteImgPath;
    private String prevImgPath;
}
