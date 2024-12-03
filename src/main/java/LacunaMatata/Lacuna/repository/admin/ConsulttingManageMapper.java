package LacunaMatata.Lacuna.repository.admin;

import LacunaMatata.Lacuna.entity.consulting.ConsultingLowerCategory;
import LacunaMatata.Lacuna.entity.consulting.ConsultingUpperCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConsulttingManageMapper {
    List<ConsultingUpperCategory> getConsultingCategoryList(Map<String, Object> params);
    List<ConsultingLowerCategory> getConsultingLowerCategoryList(Map<String, Object> params);
}