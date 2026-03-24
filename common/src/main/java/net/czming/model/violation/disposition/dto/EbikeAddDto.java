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
public class EbikeAddDto {
    @NotBlank(message = "licensePlate不能为空")
    private String licensePlate;

    @NotBlank(message = "featureDescription不能为空")
    private String featureDescription;

    @NotBlank(message = "ownerName不能为空")
    private String ownerName;


    @NotBlank(message = "OwnerIdCard不能为空")
    @Pattern(regexp = ValidationConstants.REGEX_ID_CARD, message = "ownerIdCard格式错误")
    private String ownerIdCard;


    @NotBlank(message = "ownerName不能为空")
    @Pattern(regexp = ValidationConstants.REGEX_PHONE_NUMBER, message = "ownerPhoneNumber格式错误")
    private String ownerPhoneNumber;
}
