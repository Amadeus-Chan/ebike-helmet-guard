package net.czming.violation.disposition.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.violation.disposition.feign.MobileThreeCheckFeignClient;
import net.czming.violation.disposition.properties.AppCodeProperties;
import org.springframework.stereotype.Component;


@Component
public class MobileThreeCheckUtil {

    private final ObjectMapper objectMapper;

    private final MobileThreeCheckFeignClient mobileThreeCheckFeignClient;

    private final AppCodeProperties appCodeProperties;

    public MobileThreeCheckUtil(ObjectMapper objectMapper, MobileThreeCheckFeignClient mobileThreeCheckFeignClient, AppCodeProperties appCodeProperties) {
        this.objectMapper = objectMapper;
        this.mobileThreeCheckFeignClient = mobileThreeCheckFeignClient;
        this.appCodeProperties = appCodeProperties;
    }

    public boolean check(String name, String idCard, String phoneNumber) {
        String resultJson = mobileThreeCheckFeignClient.check("APPCODE " + appCodeProperties.getThreeCheck(), name, idCard, phoneNumber);

        try {
            ApiResponse response = objectMapper.readValue(resultJson, ApiResponse.class);
            if (response.getCode() == 200) {
                return response.getData().getResult().equals("0");
            }

            if (response.getCode() == 400)
                throw new BusinessException(ErrorEnum.BIZ_FAILED, response.getMsg());


        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "json解析错误");
        }

        return false;
    }

    @Data
    static
    class ApiResponse {
        private String msg;
        private boolean success;
        private int code;
        private Data data;

        @lombok.Data
        static
        class Data {
            private String result;
            private String orderNo;
            private String desc;
        }
    }
}
