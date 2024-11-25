package LacunaMatata.Lacuna.security.handler.auth;

import LacunaMatata.Lacuna.entity.user.User;
import LacunaMatata.Lacuna.repository.user.UserMapper;
import LacunaMatata.Lacuna.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = defaultOAuth2User.getAttributes();
        String socialId = attributes.get("socialId").toString();
        String provider = attributes.get("provider").toString();
        String email = attributes.get("email").toString();

        User user = userMapper.findUserBySocialId(socialId);
        if(user == null) {
            response.sendRedirect("http://localhost:3000/auth/signup/oauth2?socialid=" + socialId +
                    "&provider=" + provider + "&email=" + email);
            return;
        }

        String accessToken = jwtProvider.generateAccessToken(user.getUserId(), user.getRoleName());
        response.sendRedirect("http://localhost:3000/auth/signin/oauth2?accessToken=" + accessToken);
    }
}
