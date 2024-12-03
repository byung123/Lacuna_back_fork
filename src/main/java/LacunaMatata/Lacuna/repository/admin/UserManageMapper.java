package LacunaMatata.Lacuna.repository.admin;

import LacunaMatata.Lacuna.entity.user.User;
import LacunaMatata.Lacuna.entity.user.UserOptionalInfo;
import LacunaMatata.Lacuna.entity.user.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserManageMapper {

    /** 사용자 관리용 mapper */
    // 1. 사용자 목록 리스트 출력_2024_10-30
    List<User> getUserList(Map<String, Object> params);
    // 2-1. 사용자 등록_2024.10.31
    int saveUser(User user);
    // 2-2. 사용자 optional info 등록_2024.10.31
    int saveUserOptionalInfo(UserOptionalInfo userOptionalInfo);
    // 2-3. 사용자 권한 등록_2024.10.31
    int saveUserRoleMet(Map<String, Object> params);
    // 3-1. 사용자 권한 수정(권한을 낮출때)
    int deleteUserRoleMet(Map<String, Object> params);
    // 3-2. 사용자 권한 수정(권한을 높일때) -> 2-3번 이용_2024.10.31
    // 3-3. 권한 수정 날짜 업데이트_2024.10.31
    int modifyUserRoleMetDate(Map<String, Object> params);
    // 3-4. 사용자 정보 수정 User_2024.11.10
    int modifyPasswordInfo(Map<String, Object> params);
    // 3-5. 사용자 정보 수정2(관리자 경우만) UserOptionalInfo_2024.11.10
    int modifyManageEmailInfo(Map<String, Object> params);
    // 3-5. 사용자 정보 수정2(관리자 경우만) UserOptionalInfo_2024.11.10
    int modifyManagePhoneInfo(Map<String, Object> params);
    // 사용자 삭제
    int deleteByUserId(int userId);
    // 사용자 복수개 삭제
    int deleteByUserList(List<Integer> userIdList);

    /** 공통으로 사용할 Mapper */
    // 1. 사용자 ID로 사용자 찾기
    User findUserById(int userId);
    // 2. 사용자 권한목록 출력(필터)
    List<UserRole> getUserRole();
}
