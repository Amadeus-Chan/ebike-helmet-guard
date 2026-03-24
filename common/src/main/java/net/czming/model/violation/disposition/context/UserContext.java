package net.czming.model.violation.disposition.context;

import io.github.linpeilie.annotations.AutoMapMapper;
import lombok.Data;

@Data
@AutoMapMapper
public class UserContext {
    private String username;
    private String role;
    private String status;
}
