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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
@PropertySource("classpath:google.properties")
public class AuthServiceGoogle {

    @Value("${google_auth_url}")
    private String url = null;

    private OAuth20Service service;

    private AuthServiceGoogle(@Value("${clientId}") String clientId,
                              @Value("${clientSecret}") String clientSecret,
                              @Value("${defaultScope}") String defaultScope,
                              @Value("${callback}") String callback) {
        this.service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .defaultScope(defaultScope)
                .callback(callback)
                .build(GoogleApi20.instance());
    }

    public void putGoogleUserInSecurityContext(String code)
            throws InterruptedException, ExecutionException, IOException {
        OAuth2AccessToken accessToken = service.getAccessToken(code);
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        service.signRequest(accessToken, request);
        Response response = service.execute(request);
        String body = response.getBody();
        JSONObject jsonObject = new JSONObject(body);
        User user = createGoogleUser(jsonObject);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user, user.getPassword());
        token.setDetails("GOOGLE");
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(token);
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

    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl();
    }
}