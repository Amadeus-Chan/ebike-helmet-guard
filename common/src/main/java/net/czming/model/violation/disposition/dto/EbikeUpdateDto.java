package net.czming.model.violation.disposition.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import net.czming.common.util.constants.ValidationConstants;

@Data
public class EbikeUpdateDto {

    @Pattern(regexp = ValidationConstants.REGEX_NOT_BLANK_AND_NULLABLE, message = "licensePlate格式错误")
    private String licensePlate;

    @Pattern(regexp = ValidationConstants.REGEX_NOT_BLANK_AND_NULLABLE, message = "featureDescription格式错误")
    private String featureDescription;

    @Pattern(regexp = ValidationConstants.REGEX_NOT_BLANK_AND_NULLABLE, message = "ownerName格式错误")
    private String ownerName;

    @Pattern(regexp = ValidationConstants.REGEX_ID_CARD, message = "ownerIdCard格式错误")
    private String ownerIdCard;

    @Pattern(regexp = ValidationConstants.REGEX_PHONE_NUMBER, message = "ownerPhoneNumber格式错误")
    private String ownerPhoneNumber;
}
