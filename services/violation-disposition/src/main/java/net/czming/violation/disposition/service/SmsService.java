package net.czming.violation.disposition.service;

import net.czming.common.util.constants.RedisConstants;
import net.czming.violation.disposition.util.SendVerifyCodeUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class SmsService {

    private final SendVerifyCodeUtil sendVerifyCodeUtil;

    private final StringRedisTemplate stringRedisTemplate;

    public SmsService(SendVerifyCodeUtil sendVerifyCodeUtil, StringRedisTemplate stringRedisTemplate) {
        this.sendVerifyCodeUtil = sendVerifyCodeUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void sendRegisterCode(String phoneNumber) {
        //生成随机六位数字字符串
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        sendVerifyCodeUtil.send(phoneNumber, code);
        stringRedisTemplate.opsForValue().set(RedisConstants.REGISTER_CODE_KEY + phoneNumber, code, RedisConstants.CODE_TTL, TimeUnit.SECONDS);
    }

    public boolean verifyRegisterCode(String phoneNumber, String registerCode) {

        String cacheCodeKey = RedisConstants.REGISTER_CODE_KEY + phoneNumber;
        String cacheCode = stringRedisTemplate.opsForValue().get(cacheCodeKey);
        if (cacheCode != null && cacheCode.equals(registerCode)) {
            stringRedisTemplate.delete(cacheCodeKey);
            return true;
        }

        return false;
    }

    public void sendLoginCode(String phoneNumber) {
        //生成随机六位数字字符串
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        sendVerifyCodeUtil.send(phoneNumber, code);
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phoneNumber, code, RedisConstants.CODE_TTL, TimeUnit.SECONDS);
    }

    public boolean verifyLoginCode(String phoneNumber, String loginCode) {

        String cacheCodeKey = RedisConstants.LOGIN_CODE_KEY + phoneNumber;
        String cacheCode = stringRedisTemplate.opsForValue().get(cacheCodeKey);
        if (cacheCode != null && cacheCode.equals(loginCode)) {
            stringRedisTemplate.delete(cacheCodeKey);
            return true;
        }

        return false;
    }

    public void sendChangePwdCode(String phoneNumber) {
        //生成随机六位数字字符串
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        sendVerifyCodeUtil.send(phoneNumber, code);
        stringRedisTemplate.opsForValue().set(RedisConstants.CHANGE_PASSWORD_CODE_KEY + phoneNumber, code, RedisConstants.CODE_TTL, TimeUnit.SECONDS);
    }

    public boolean verifyChangePwdCode(String phoneNumber, String pwdCode) {
        String cacheCodeKey = RedisConstants.CHANGE_PASSWORD_CODE_KEY + phoneNumber;
        String cacheCode = stringRedisTemplate.opsForValue().get(cacheCodeKey);
        if (cacheCode != null && cacheCode.equals(pwdCode)) {
            stringRedisTemplate.delete(cacheCodeKey);
            return true;
        }

        return false;
    }

    public void sendChangePhoneCode(String phoneNumber) {
        //生成随机六位数字字符串
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        sendVerifyCodeUtil.send(phoneNumber, code);
        stringRedisTemplate.opsForValue().set(RedisConstants.CHANGE_PHONE_KEY + phoneNumber, code, RedisConstants.CODE_TTL, TimeUnit.SECONDS);
    }

    public boolean verifyChangePhoneCode(String phoneNumber, String changePhoneCode) {
        String cacheCodeKey = RedisConstants.CHANGE_PHONE_KEY + phoneNumber;
        String cacheCode = stringRedisTemplate.opsForValue().get(cacheCodeKey);
        if (cacheCode != null && cacheCode.equals(changePhoneCode)) {
            stringRedisTemplate.delete(cacheCodeKey);
            return true;
        }

        return false;
    }
}
