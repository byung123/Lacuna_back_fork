package LacunaMatata.Lacuna.service.admin;

import LacunaMatata.Lacuna.dto.request.admin.usermanage.ReqDeleteUserListDto;
import LacunaMatata.Lacuna.dto.request.admin.usermanage.ReqGetUserListDto;
import LacunaMatata.Lacuna.dto.request.admin.usermanage.ReqModifyUserDto;
import LacunaMatata.Lacuna.dto.request.admin.usermanage.ReqRegistUserDto;
import LacunaMatata.Lacuna.dto.response.admin.usermanage.*;
import LacunaMatata.Lacuna.entity.user.User;
import LacunaMatata.Lacuna.entity.user.UserOptionalInfo;
import LacunaMatata.Lacuna.entity.user.UserRole;
import LacunaMatata.Lacuna.entity.user.UserRoleMet;
import LacunaMatata.Lacuna.repository.admin.UserManageMapper;
import LacunaMatata.Lacuna.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserManageService {

    @Autowired
    private UserManageMapper userManageMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 사용자 정보 리스트 출력
    public RespCountAndUserListDto getUserInfoList(ReqGetUserListDto dto) {
        int startIndex = (dto.getPage() - 1) * dto.getLimit();
        Map<String, Object> params = Map.of(
                "filter", dto.getFilter(),
                "option", dto.getOption(),
                "searchValue", dto.getSearchValue() == null ? "" : dto.getSearchValue(),
                "startIndex", startIndex,
                "limit", dto.getLimit()
        );
        List<User> userList = userManageMapper.getUserList(params);
        List<RespGetUserListDto> respGetUserListDtos = new ArrayList<>();
        for(User user : userList) {
            Map<Integer, String> roleMap = user.getUserRoleMets().stream().collect(Collectors.toMap
                            (rolemet -> rolemet.getUserRole().getRoleId(), rolemet -> rolemet.getUserRole().getRoleName()
            ));
            Integer maxRoleId = roleMap.keySet().stream().max(Integer::compare).orElse(1);
            String roleName = (maxRoleId != null) ? roleMap.get(maxRoleId) : null;

            RespGetUserListDto respGetUserListDto = RespGetUserListDto.builder()
                    .userId(user.getUserId())
                    .roleName(user.getRoleName())
                    .username(user.getUsername())
                    .name(user.getName())
                    .gender(user.getUserOptionalInfo().getGender())
                    .birthDate(user.getUserOptionalInfo().getBirthDate())
                    .createdDate(user.getCreateDate())
                    .inactive(user.getInactive())
                    .build();
            respGetUserListDtos.add(respGetUserListDto);
        }
        int totalCount = userList.isEmpty() ? 0 : userList.get(0).getTotalCount();

        RespCountAndUserListDto respCountAndUserListDto = RespCountAndUserListDto.builder()
                .totalCount(totalCount)
                .userList(respGetUserListDtos)
                .build();

        return respCountAndUserListDto;
    }

    // 사용자 등록
    @Transactional(rollbackFor = Exception.class)
    public void registUser(ReqRegistUserDto dto) throws Exception {

        try {
            User user = User.builder()
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .name(dto.getName())
                    .build();
            userManageMapper.saveUser(user);

            UserOptionalInfo userOptionalInfo = UserOptionalInfo.builder()
                    .userId(user.getUserId())
                    .birthDate(dto.getBirthDate())
                    .gender(dto.getGender())
                    .phoneNumber(dto.getPhoneNumber())
                    .build();
            userManageMapper.saveUserOptionalInfo(userOptionalInfo);

            List<Integer> roleIdList = new ArrayList<>();
            for(int i = 1; i < dto.getRoleId() + 1; i++) {
                roleIdList.add(i);
            }
            Map<String, Object> params = Map.of(
                    "userId", user.getUserId(),
                    "roleIdList", roleIdList
            );
            userManageMapper.saveUserRoleMet(params);
        } catch (Exception e) {
            throw new Exception("사용자 등록 도중 오류 발생");
        }
    }

    // 사용자 권한 목록 출력(필터)
    public List<RespUserRoleFilterDto> getUserRoleList() {
        List<UserRole> userRole = userManageMapper.getUserRole();
        List<RespUserRoleFilterDto> userRoleList = new ArrayList<>();
        for(UserRole ur : userRole) {
            RespUserRoleFilterDto respUserRoleFilterDto = RespUserRoleFilterDto.builder()
                    .roleId(ur.getRoleId())
                    .roleName(ur.getRoleName())
                    .build();
            userRoleList.add(respUserRoleFilterDto);
        }
        return userRoleList;
    }

    // 사용자 관리 수정 모달창 출력
    public RespGetModifyUserModalDto getUserModifyModal(int userId) {
        User user = userManageMapper.findUserById(userId);
        List<UserRole> userRole = userManageMapper.getUserRole();
        List<RespUserRoleFilterDto> userRoleList = new ArrayList<>();
        for(UserRole ur : userRole) {
            RespUserRoleFilterDto respUserRoleFilterDto = RespUserRoleFilterDto.builder()
                    .roleId(ur.getRoleId())
                    .roleName(ur.getRoleName())
                    .build();
            userRoleList.add(respUserRoleFilterDto);
        }
        RespGetModifyUserModalDto modifyUserModal = RespGetModifyUserModalDto.builder()
                .userId(user.getUserId())
                .roleId(user.getRoleId())
                .roleName(user.getRoleName())
                .username(user.getUsername())
                .gender(user.getUserOptionalInfo().getGender())
                .birthDate(user.getUserOptionalInfo().getBirthDate())
                .email(user.getEmail())
                .inactive(user.getInactive())
                .name(user.getName())
                .phoneNumber(user.getUserOptionalInfo().getPhoneNumber())
                .profileImg(user.getUserOptionalInfo().getProfileImg())
                .loginIp(user.getLoginIp())
                .userRoleList(userRoleList)
                .build();
        return modifyUserModal;
    }

    // 사용자 수정(권한)
    @Transactional(rollbackFor = Exception.class)
    public void modifyUser(ReqModifyUserDto dto) throws Exception {
        User user = userManageMapper.findUserById(dto.getUserId());

        int originalRoleId = user.getRoleId();
        int modifyRoleId = dto.getRoleId();

        if(originalRoleId == modifyRoleId) {
            return;
        }

        if(originalRoleId > modifyRoleId) {
            List<Integer> roleIdList = new ArrayList<>();
            for(int i = originalRoleId; i > modifyRoleId; i--) {
                roleIdList.add(i);
            }
            Map<String, Object> params = Map.of(
                    "userId", user.getUserId(),
                    "roleIdList", roleIdList
            );
            userManageMapper.deleteUserRoleMet(params);
        }

        if(originalRoleId < modifyRoleId) {
            List<Integer> roleIdList = new ArrayList<>();

            for(int i = modifyRoleId; i > originalRoleId; i--) {
                roleIdList.add(i);
            }

            Map<String, Object> params = Map.of(
                    "userId", user.getUserId(),
                    "roleIdList", roleIdList
            );
            userManageMapper.saveUserRoleMet(params);
        }

        List<Integer> roleIdList = new ArrayList<>();
        for(int i = 1; i < modifyRoleId + 1; i++) {
            roleIdList.add(i);
        }
        Map<String, Object> modifyParams = Map.of(
                "userId", dto.getUserId(),
                "roleIdList", roleIdList
        );
        userManageMapper.modifyUserRoleMetDate(modifyParams);

        if(modifyRoleId == 5) {
            if(!dto.getPassword().equals(dto.getPasswordCheck())) {
                throw new Exception("비밀번호 불일치");
            }
            Map<String, Object> params1 = Map.of(
                    "userId", dto.getUserId(),
                    "email", dto.getEmail(),
                    "password", dto.getPassword()
            );
            userManageMapper.modifyManagerInfo(params1);
            Map<String, Object> params2 = Map.of(
                    "userId", dto.getUserId(),
                    "phoneNumber", dto.getPhoneNumber(),
                    "profileImg", dto.getProfileImg()
            );
            userManageMapper.modifyManageOptionalInfo(params2);
        }
    }

    // 사용자 삭제
    public void deleteUser(int userId) {
        userManageMapper.deleteByUserId(userId);
    }

    // 사용자 복수개 삭제
    public void deleteUserList(ReqDeleteUserListDto dto) {
        List<Integer> userIdList = dto.getUserIdList();
        userManageMapper.deleteByUserList(userIdList);
    }
}
