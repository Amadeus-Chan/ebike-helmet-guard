package net.czming.model.violation.disposition.entity;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.czming.model.violation.disposition.dto.UserRegisterDto;
import net.czming.model.violation.disposition.context.UserContext;
import net.czming.model.violation.disposition.vo.UserVo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@AutoMappers({
        @AutoMapper(target = UserRegisterDto.class),
        @AutoMapper(target = UserVo.class),
        @AutoMapper(target = UserContext.class)
})
public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String idCard;
    private String phoneNumber;
    private Role role;
    private Status status;




    public enum Role {
        USER,
        AUDITOR,
        ADMIN,
    }

    public enum Status {
        DISABLED,
        ENABLED,
    }
}