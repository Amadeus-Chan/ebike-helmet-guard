package net.czming.violation.disposition.util;

import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.violation.disposition.properties.AccessKeyProperties;
import org.springframework.stereotype.Component;

@Component
public class SendVerifyCodeUtil {

    com.aliyun.dypnsapi20170525.Client client;


    public SendVerifyCodeUtil(AccessKeyProperties accessKeyProperties){

        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();

        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyProperties.getId())
                .setAccessKeySecret(accessKeyProperties.getSecret())
                .setCredential(credential);

        config.endpoint = "dypnsapi.aliyuncs.com";

        try {
            client = new com.aliyun.dypnsapi20170525.Client(config);
        } catch (Exception e) {
            throw new BusinessException(ErrorEnum.EXTERNAL_CALL_FAILED);
        }
    }


    public void send(String phoneNumber, String verifyCode){

        com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest sendSmsVerifyCodeRequest = new com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest()
                .setPhoneNumber(phoneNumber)
                .setSignName("速通互联验证码")
                .setTemplateCode("100001")
                .setTemplateParam("{\"code\":" + verifyCode + ",\"min\":\"5\"}");

        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        try {
            client.sendSmsVerifyCodeWithOptions(sendSmsVerifyCodeRequest, runtime);
        } catch (Exception e) {
            throw new BusinessException(ErrorEnum.EXTERNAL_CALL_FAILED);
        }

    }

}
