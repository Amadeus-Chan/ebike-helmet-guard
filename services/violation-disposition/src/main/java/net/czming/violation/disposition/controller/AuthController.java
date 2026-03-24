package net.czming.violation.disposition.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import net.czming.common.annotation.Loggable;
import net.czming.common.result.R;
import net.czming.common.util.constants.ValidationConstants;
import net.czming.model.violation.disposition.dto.UserLoginDto;
import net.czming.model.violation.disposition.dto.UserRegisterDto;
import net.czming.violation.disposition.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Loggable
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public R<Void> register(@RequestBody @Valid UserRegisterDto userRegisterDto,

                            @RequestParam("registerCode")
                            @NotBlank(message = "registerCode不能为空")
                            @Pattern(regexp = ValidationConstants.REGEX_CODE, message = "registerCode格式错误")
                            String registerCode) {
        authService.register(userRegisterDto, registerCode);
        return R.ok();
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody @Valid UserLoginDto userLoginDto) {
        String token = authService.login(userLoginDto);
        return R.ok(token);
    }

    @PostMapping("/code-login")
    public R<String> verifyCodeLogin(@RequestParam("phoneNumber")
                                      @NotBlank(message = "手机号码不能为空")
                                      @Pattern(regexp = ValidationConstants.REGEX_PHONE_NUMBER, message = "手机号码格式错误")
                                      String phoneNumber,

                                      @RequestParam("loginCode")
                                      @NotBlank(message = "loginCode不能为空")
                                      @Pattern(regexp = ValidationConstants.REGEX_CODE, message = "loginCode格式错误")
                                      String loginCode) {

        String token = authService.verifyCodeLogin(phoneNumber, loginCode);
        return R.ok(token);
    }

    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader("Authorization")
                          @NotBlank(message = "token不能为空")
                          @Pattern(regexp = ValidationConstants.REGEX_LOGIN_TOKEN, message = "token格式错误")
                          String token) {

        authService.logout(token);
        return R.ok();
    }

}
