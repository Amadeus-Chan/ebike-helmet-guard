package net.czming.violation.disposition.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import net.czming.common.annotation.Loggable;
import net.czming.common.result.R;
import net.czming.common.util.constants.ValidationConstants;
import net.czming.model.violation.disposition.vo.UserVo;
import net.czming.violation.disposition.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Loggable
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("change-role")
    public R<Void> changeRole(@RequestParam("username")
                              @NotBlank(message = "username不能为空")
                              String username,

                              @RequestParam("newRole")
                              @NotBlank(message = "nweRole不能为空")
                              @Pattern(regexp = ValidationConstants.REGEX_USER_ROLE, message = "newRole 只能是 USER、AUDITOR、ADMIN 其中的一个") String newRole) {
        userService.changeRole(username, newRole);
        return R.ok();
    }

    @PostMapping("change-status")
    public R<Void> changeStatus(@RequestParam("username")
                                @NotBlank(message = "username不能为空")
                                String username,

                                @RequestParam("newStatus")
                                @NotBlank(message = "nweRole不能为空")
                                @Pattern(regexp = ValidationConstants.REGEX_USER_STATUS, message = "newStatus 只能是 ENABLED 或 DISABLED")String newStatus) {
        userService.changeStatus(username, newStatus);
        return R.ok();
    }

    @GetMapping("/get-username")
    public R<String> getUsernameByIdCard(@RequestParam("idCard")
                                         @NotBlank(message = "idCard不能为空")
                                         @Pattern(regexp = ValidationConstants.REGEX_ID_CARD, message = "idCard格式错误")
                                         String idCard) {
        String username = userService.getUsernameByIdCard(idCard);
        return R.ok(username);
    }

    @GetMapping("/get-me")
    public R<UserVo> getMe() {
        UserVo userVo = userService.getMe();
        return R.ok(userVo);
    }

    @GetMapping("/get")
    public R<UserVo> getUser(@RequestParam("username")
                             @NotBlank(message = "username不能为空") String username) {
        UserVo userVo = userService.getUserByUsername(username);
        return R.ok(userVo);
    }

    @PostMapping("change-password")
    public R<Void> changePassword(@RequestParam("username")
                                  @NotBlank(message = "username不能为空")
                                  String username,

                                  @RequestParam("newPassword")
                                  @NotBlank(message = "newPassword不能为空")
                                  String newPassword,

                                  @RequestParam("pwdCode")
                                  @NotBlank(message = "pwdCode不能为空")
                                  @Pattern(regexp = ValidationConstants.REGEX_CODE, message = "pwdCode格式错误")
                                  String pwdCode) {
        userService.changePassword(username, newPassword, pwdCode);
        return R.ok();
    }

    @PostMapping("/change-phone")
    public R<Void> changePhoneNumber(@RequestParam("username")
                                     @NotBlank(message = "username不能为空")
                                     String username,

                                     @RequestParam("newPhoneNumber")
                                     @NotBlank(message = "newPhoneNumber不能为空")
                                     @Pattern(regexp = ValidationConstants.REGEX_PHONE_NUMBER, message = "newPhoneNumber格式错误")
                                     String newPhoneNumber,

                                     @RequestParam("phoneCode")
                                     @NotBlank(message = "phoneCode不能为空")
                                     @Pattern(regexp = ValidationConstants.REGEX_CODE, message = "phoneCode格式错误")
                                     String phoneCode) {
        userService.changePhoneNumber(username, newPhoneNumber, phoneCode);
        return R.ok();
    }
}
