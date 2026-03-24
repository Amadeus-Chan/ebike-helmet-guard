package net.czming.violation.disposition.service;

import net.czming.model.violation.disposition.dto.UserLoginDto;
import net.czming.model.violation.disposition.dto.UserRegisterDto;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;


    public AuthService(UserService userService, StringRedisTemplate stringRedisTemplate) {
        this.userService = userService;
    }

    public void register(UserRegisterDto userRegisterDto, String registerCode) {
        userService.register(userRegisterDto, registerCode);
    }

    public String login(UserLoginDto userLoginDto) {
        return userService.login(userLoginDto);
    }


    public String verifyCodeLogin(String phoneNumber, String loginCode) {
        return userService.verifyCodeLogin(phoneNumber, loginCode);
    }

    public void logout(String token) {
        userService.logout(token);
    }

}

