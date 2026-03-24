package net.czming.violation.disposition.controller;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import net.czming.common.annotation.Loggable;
import net.czming.common.result.R;
import net.czming.common.util.constants.ValidationConstants;
import net.czming.violation.disposition.service.SmsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Loggable
@RestController
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    public SmsController(final SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/register-code")
    public R<Void> sendRegisterCode(@RequestParam("phoneNumber") @NotBlank(message = "手机号码不能为空")
                                    @Pattern(regexp = ValidationConstants.REGEX_PHONE_NUMBER, message = "手机号码格式错误")
                                    String phoneNumber) {
        smsService.sendRegisterCode(phoneNumber);
        return R.ok();
    }

    @PostMapping("/login-code")
    public R<Void> sendLoginCode(@RequestParam("phoneNumber") @NotBlank(message = "手机号码不能为空")
                                 @Pattern(regexp = ValidationConstants.REGEX_PHONE_NUMBER, message = "手机号码格式错误")
                                 String phoneNumber) {
        smsService.sendLoginCode(phoneNumber);
        return R.ok();
    }

    @PostMapping("/change-pwd-code")
    public R<Void> sendChangePwdCode(@RequestParam("phoneNumber") @NotBlank(message = "手机号码不能为空")
                                     @Pattern(regexp = ValidationConstants.REGEX_PHONE_NUMBER, message = "手机号码格式错误")
                                     String phoneNumber) {
        smsService.sendChangePwdCode(phoneNumber);
        return R.ok();
    }

    @PostMapping("/change-phone-code")
    public R<Void> sendChangePhoneCode(@RequestParam("phoneNumber") @NotBlank(message = "手机号码不能为空")
                                       @Pattern(regexp = ValidationConstants.REGEX_PHONE_NUMBER, message = "手机号码格式错误")
                                       String phoneNumber) {
        smsService.sendChangePhoneCode(phoneNumber);
        return R.ok();
    }
}
