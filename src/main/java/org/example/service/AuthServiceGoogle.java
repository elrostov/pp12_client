package org.example.service;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.example.model.Role;
import org.example.model.User;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class AuthServiceGoogle {

    private final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private final String clientId = "257298502369-16krvug04jsf5mo5nhecav1i0k89mo9u.apps.googleusercontent.com";
    private final String clientSecret = "SGyjJhXN7f6L1n3_Xmppal3P";
    private OAuth20Service service;

    private AuthServiceGoogle() {
        this.service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .defaultScope("openid profile email") // replace with desired scope
                .callback("http://localhost:8080/googleLogin/process")
                .build(GoogleApi20.instance());
    }

    public OAuth20Service getService() {
        return service;
    }

    public User getGoogleUser(String code) throws InterruptedException, ExecutionException, IOException {
        OAuth2AccessToken accessToken = service.getAccessToken(code);
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = service.execute(request);
        String body = response.getBody();
        JSONObject jsonObject = new JSONObject(body);
        return createGoogleUser(jsonObject);
    }

    private User createGoogleUser(JSONObject jsonObject) {
        User user = new User();
        user.setId(jsonObject.getLong("sub"));
        user.setUsername(jsonObject.getString("given_name"));
        user.setPassword("Google user");
        user.setEmail(jsonObject.getString("email"));
        Set<Role> roles = new LinkedHashSet<>();
        roles.add(new Role(2L, "USER"));
        user.setRoles(roles);
        return user;
    }
}