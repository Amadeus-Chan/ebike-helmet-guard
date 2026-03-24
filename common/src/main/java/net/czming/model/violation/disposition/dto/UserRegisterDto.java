package net.czming.model.violation.disposition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.czming.common.util.constants.ValidationConstants;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserRegisterDto {

    @NotBlank(message = "username不能为空")
    private String username;

    @NotBlank(message = "password不能为空")
    private String password;

    @NotBlank(message = "realName不能为空")
    private String realName;

    @NotBlank(message = "idCard不能为空")
    @Pattern(regexp = ValidationConstants.REGEX_ID_CARD, message = "身份证号码格式不正确")
    private String idCard;

    @NotBlank(message = "phoneNumber不能为空")
    @Pattern(regexp = ValidationConstants.REGEX_PHONE_NUMBER, message = "电话号码格式不正确")
    private String phoneNumber;
}
