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
import LacunaMatata.Lacuna.security.principal.PrincipalUser;
import LacunaMatata.Lacuna.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

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
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        User user = userManageMapper.findUserById(dto.getUserId());

        int originalRoleId = user.getRoleId();
        int modifyRoleId = dto.getRoleId();

        if(originalRoleId == modifyRoleId) {
            return;
        }

        try {
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

            // 폰번호 변경
            if(!user.getUserOptionalInfo().getPhoneNumber().equals(dto.getPhoneNumber()) && !dto.getPhoneNumber().isEmpty() && dto.getPhoneNumber() != null) {
                Map<String, Object> params = Map.of(
                        "userId", dto.getUserId(),
                        "phoneNumber", dto.getPhoneNumber()
                );
                userManageMapper.modifyManagePhoneInfo(params);
            }

            // 이메일 변경시
            if(!user.getEmail().equals(dto.getEmail()) && !dto.getEmail().isEmpty() && dto.getEmail() != null) {
                Map<String, Object> params = Map.of(
                        "userId", dto.getUserId(),
                        "email", dto.getEmail()
                );
                userManageMapper.modifyManageEmailInfo(params);
            }

            // 비밀번호 변경시
            if(dto.getPassword().isEmpty() || user.getPassword().equals(dto.getPassword()) || dto.getPassword() == null) {
                return;
            }

            if(!dto.getPassword().equals(dto.getPasswordCheck())) {
                throw new Exception("비밀번호 불일치");
            }

            // 비밀번호 변경
            Map<String, Object> params1 = Map.of(
                    "userId", dto.getUserId(),
                    "password", dto.getPassword()
            );
            userManageMapper.modifyPasswordInfo(params1);
        } catch (Exception e) {
            throw new Exception("회원 정보 수정중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 사용자 삭제
    public void deleteUser(int userId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        try {
            userManageMapper.deleteByUserId(userId);
        } catch (Exception e) {
            throw new Exception("회원을 삭제하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 사용자 복수개 삭제
    public void deleteUserList(ReqDeleteUserListDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        List<Integer> userIdList = dto.getUserIdList();

        try {
            userManageMapper.deleteByUserList(userIdList);
        } catch (Exception e) {
            throw new Exception("회원을 삭제하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }
}
