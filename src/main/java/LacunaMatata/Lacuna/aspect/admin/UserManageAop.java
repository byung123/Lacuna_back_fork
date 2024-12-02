package LacunaMatata.Lacuna.aspect.admin;

import LacunaMatata.Lacuna.dto.request.admin.usermanage.ReqRegistUserDto;
import LacunaMatata.Lacuna.dto.request.user.auth.ReqGeneralSigninDto;
import LacunaMatata.Lacuna.dto.request.user.auth.ReqGeneralSignupDto;
import LacunaMatata.Lacuna.dto.request.user.auth.ReqOauthSignupDto;
import LacunaMatata.Lacuna.service.AuthService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

@Aspect
@Component
public class UserManageAop {

    @Autowired
    private AuthService authService;

    @Pointcut("@annotation(LacunaMatata.Lacuna.aspect.annotation.admin.UserManageAop)")
    private void pointCut() {};

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object[] args = proceedingJoinPoint.getArgs();
        BeanPropertyBindingResult bindingResult = null;

        for(Object arg : args) {
            if(arg.getClass() == BeanPropertyBindingResult.class) {
                bindingResult = (BeanPropertyBindingResult) arg;
                break;
            }
        }

        switch (proceedingJoinPoint.getSignature().getName()) {
            case "registerUser":
                for(Object arg : args) {
                    if(arg.getClass() == ReqRegistUserDto.class) {
                        ReqRegistUserDto dto = (ReqRegistUserDto) arg;
                        if(authService.isDuplicateUsername(dto.getUsername())) {
                            FieldError fieldError
                                    = new FieldError("username", "username", "이미 존재하는 계정입니다.");
                            bindingResult.addError(fieldError);
                        }
                        if(authService.isDuplicateEmail(dto.getEmail())) {
                            FieldError fieldError
                                    = new FieldError("email", "email", "이미 존재하는 이메일 주소입니다.");
                            bindingResult.addError(fieldError);
                        }
                        if(!dto.getPassword().equals(dto.getPasswordCheck())) {
                            FieldError fieldError
                                    = new FieldError("checkPassword", "checkPassword", "비밀번호를 일치시켜주세요.");
                            bindingResult.addError(fieldError);
                        }
                        break;
                    }
                }
                break;
        }

        if(bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldErrors());
        }

        return proceedingJoinPoint.proceed();
    }
}
