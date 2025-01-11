package LacunaMatata.Lacuna.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public class OAuth2Service implements OAuth2UserService {

    @Autowired
    private DefaultOAuth2UserService defaultOAuth2UserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> oAuth2Attributes = new HashMap<>();
        oAuth2Attributes.put("provider", userRequest.getClientRegistration().getClientName());

        switch (userRequest.getClientRegistration().getClientName()) {
            case "Google":
                oAuth2Attributes.put("socialId", attributes.get("sub").toString());
                oAuth2Attributes.put("email", attributes.get("email").toString());
                break;
            case "Naver":
                // 네이버는 Attribue에서 response꺼내서 다시 attribute로 설정
                attributes = (Map<String, Object>) attributes.get("response");
                oAuth2Attributes.put("socialId", attributes.get("id").toString());
                oAuth2Attributes.put("email", attributes.get("email").toString());
                break;
            case "Kakao":
                attributes = (Map<String, Object>) attributes.get("kakao_account");
                oAuth2Attributes.put("socialId", attributes.get("id").toString());
                oAuth2Attributes.put("email", attributes.get("email").toString());
                break;
        }

        return new DefaultOAuth2User(new HashSet<>(), oAuth2Attributes, "socialId");
    }
}
