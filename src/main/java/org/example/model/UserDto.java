package org.example.model;

import java.util.Arrays;

public class UserDto {

    private Long id;

    private String username;

    private String password;

    private String email;

    private String[] roles;

    public UserDto() {}

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = "";
        this.email = user.getEmail();
        Object[] objectArr = user.getRoles().stream().map(Role::getName).toArray();
        this.roles = Arrays.copyOf(objectArr, objectArr.length, String[].class);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String rolesToString() {
        if (roles == null)
            return "null";

        int iMax = roles.length - 1;
        if (iMax == -1)
            return "";

        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(roles[i]);
            if (i == iMax)
                return b.toString();
            b.append(",");
        }
    }
}
