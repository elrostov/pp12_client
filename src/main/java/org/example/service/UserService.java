package org.example.service;

import org.example.model.UserDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDetails loadUserByUsername(String username);
    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto);
    void deleteUser(long id);
}
