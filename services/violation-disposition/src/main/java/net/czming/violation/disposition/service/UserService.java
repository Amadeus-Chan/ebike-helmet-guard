package net.czming.violation.disposition.service;

import io.github.linpeilie.Converter;
import net.czming.common.util.BeanToMapUtil;
import net.czming.common.util.constants.RedisConstants;
import net.czming.model.violation.disposition.dto.UserLoginDto;
import net.czming.model.violation.disposition.dto.UserRegisterDto;
import net.czming.model.violation.disposition.entity.User;
import net.czming.model.violation.disposition.context.UserContext;
import net.czming.model.violation.disposition.vo.UserVo;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.violation.disposition.mapper.UserMapper;
import net.czming.model.violation.disposition.context.UserContextHolder;
import net.czming.violation.disposition.util.MobileThreeCheckUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private final UserMapper userMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final SmsService smsService;

    private final PermissionService permissionService;

    private final MobileThreeCheckUtil mobileThreeCheckUtil;

    private final Converter converter;


    public UserService(UserMapper userMapper,
                       StringRedisTemplate stringRedisTemplate,
                       SmsService smsService,
                       PermissionService permissionService,
                       MobileThreeCheckUtil mobileThreeCheckUtil,
                       Converter converter) {
        this.userMapper = userMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.smsService = smsService;
        this.permissionService = permissionService;
        this.mobileThreeCheckUtil = mobileThreeCheckUtil;
        this.converter = converter;
    }


    public UserVo getUserByUsername(String username) {

       permissionService.requireSelfOrAdmin(username);

        User user = userMapper.selectUserByUsername(username);
        if (user == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "用户不存在");

        return converter.convert(user, UserVo.class);
    }


    public void register(UserRegisterDto userRegisterDto, String registerCode) {

        User user = userMapper.selectUserByIdCard(userRegisterDto.getIdCard());
        if (user != null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "已经实名注册，用户名：" + user.getUsername());

        user = userMapper.selectUserByUsername(userRegisterDto.getUsername());
        if (user != null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "用户名已存在" + user.getUsername());

        boolean flag = smsService.verifyRegisterCode(userRegisterDto.getPhoneNumber(), registerCode);

        if (!flag)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "验证码复核失败");

        flag = mobileThreeCheckUtil.check(userRegisterDto.getRealName(), userRegisterDto.getIdCard(), userRegisterDto.getPhoneNumber());

        if (!flag)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "三要素核验失败");

        user = converter.convert(userRegisterDto, User.class);
        user.setRole(User.Role.USER);
        user.setStatus(User.Status.ENABLED);

        userMapper.insertUser(user);
    }

    public String login(UserLoginDto userLoginDto) {
        User user = userMapper.selectUserByUsername(userLoginDto.getUsername());

        if (user == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "用户不存在");

        if (!user.getPassword().equals(userLoginDto.getPassword()))
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "用户名或密码错误");

        return generateLoginToken(user);
    }

    public String verifyCodeLogin(String phoneNumber, String loginCode) {
        User user = userMapper.selectUserByPhoneNumber(phoneNumber);

        if (user == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "用户不存在");

        boolean flag = smsService.verifyLoginCode(phoneNumber, loginCode);

        if (!flag)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "验证码错误");

        return generateLoginToken(user);
    }



    public void logout(String token) {

        String username = UserContextHolder.getUserContext().getUsername();
        String userTokenKey = RedisConstants.USER_TOKEN_KEY + username;
        String loginKey = RedisConstants.LOGIN_USER_KEY + token;

        stringRedisTemplate.delete(userTokenKey);
        stringRedisTemplate.delete(loginKey);
    }


    public void changeRole(String username, String newRole) {

        permissionService.requireAdmin();

        User user = userMapper.selectUserByUsername(username);
        if (user == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标用户不存在");
        user.setRole(User.Role.valueOf(newRole));

        String userTokenKey = RedisConstants.USER_TOKEN_KEY + user.getUsername();
        String token = stringRedisTemplate.opsForValue().get(userTokenKey);
        String loginKey = RedisConstants.LOGIN_USER_KEY + token;

        userMapper.updateUser(user);

        stringRedisTemplate.delete(userTokenKey);
        stringRedisTemplate.delete(loginKey);
    }

    public void changeStatus(String username, String newStatus) {
        permissionService.requireAdmin();

        User user = userMapper.selectUserByUsername(username);
        if (user == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标用户不存在");
        user.setStatus(User.Status.valueOf(newStatus));

        String userTokenKey = RedisConstants.USER_TOKEN_KEY + user.getUsername();
        String token = stringRedisTemplate.opsForValue().get(userTokenKey);
        String loginKey = RedisConstants.LOGIN_USER_KEY + token;

        userMapper.updateUser(user);

        stringRedisTemplate.delete(userTokenKey);
        stringRedisTemplate.delete(loginKey);
    }

    public String getUsernameByIdCard(String idCard) {
        User user = userMapper.selectUserByIdCard(idCard);
        if (user == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "该身份证号未注册");
        return user.getUsername();
    }

    public void changePassword(String username, String newPassword, String pwdCode) {
        User user = userMapper.selectUserByUsername(username);
        if (user == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "用户不存在");

        boolean flag = smsService.verifyChangePwdCode(user.getPhoneNumber(), pwdCode);
        if (!flag)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "验证码错误");

        user.setPassword(newPassword);
        userMapper.updateUser(user);
    }

    public void changePhoneNumber(String username, String newPhoneNumber, String phoneCode) {
        User user = userMapper.selectUserByUsername(username);
        if (user == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "用户不存在");

        boolean flag = smsService.verifyChangePhoneCode(newPhoneNumber, phoneCode);
        if (!flag)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "验证码错误");

        flag = mobileThreeCheckUtil.check(user.getRealName(), user.getIdCard(), newPhoneNumber);
        if (!flag)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "三要素核验");

        user.setPhoneNumber(newPhoneNumber);
        userMapper.updateUser(user);
    }

    public String getUserIdCard(String username) {
        User user = userMapper.selectUserByUsername(username);

        if (user == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "用户不存在");

        return user.getIdCard();
    }


    public String getRandomAuditorName() {
        return userMapper.selectRandomAuditorUsername();
    }

    public UserVo getMe() {
        return getUserByUsername(UserContextHolder.getUserContext().getUsername());
    }

    private String generateLoginToken(User user) {

        String userTokenKey = RedisConstants.USER_TOKEN_KEY + user.getUsername();
        String token = stringRedisTemplate.opsForValue().get(userTokenKey);
        String loginKey = RedisConstants.LOGIN_USER_KEY + token;
        stringRedisTemplate.delete(loginKey);


        token = UUID.randomUUID().toString();

        UserContext userContext = converter.convert(user, UserContext.class);
        Map<String, Object> userContextMap = BeanToMapUtil.beanToMap(userContext);

        stringRedisTemplate.opsForValue().set(userTokenKey, token);
        stringRedisTemplate.expire(userTokenKey, RedisConstants.TOKEN_TTL, TimeUnit.SECONDS);

        loginKey = RedisConstants.LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(loginKey, userContextMap);
        stringRedisTemplate.expire(loginKey, RedisConstants.TOKEN_TTL, TimeUnit.SECONDS);

        return token;
    }
}


