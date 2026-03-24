package net.czming.violation.disposition.mapper;

import net.czming.model.violation.disposition.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectUserById(Long id);

    User selectUserByUsername(String username);

    User selectUserByPhoneNumber(String phoneNumber);

    User selectUserByIdCard(String idCard);

    String selectRandomAuditorUsername();

    int updateUser(User user);

    int insertUser(User user);
}
