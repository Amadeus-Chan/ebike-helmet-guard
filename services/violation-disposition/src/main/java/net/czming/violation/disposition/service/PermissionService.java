package net.czming.violation.disposition.service;

import net.czming.common.exception.AccessDeniedException;
import net.czming.model.violation.disposition.context.UserContext;
import net.czming.model.violation.disposition.context.UserContextHolder;
import net.czming.model.violation.disposition.entity.User;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    public String currentUsername() {
        return getRequiredUserContext().getUsername();
    }

    public String currentRole() {
        return getRequiredUserContext().getRole();
    }

    public boolean isSelf(String targetUsername){
        if (targetUsername == null) {
            return false;
        }

        return currentUsername().equals(targetUsername);
    }

    public boolean isAuditor(){
        return User.Role.AUDITOR.name().equals(currentRole());
    }

    public boolean isAdmin() {
        return User.Role.ADMIN.name().equals(currentRole());
    }

    public boolean isSelfOrAuditor(String targetUsername){
        return isSelf(targetUsername) || isAuditor();
    }

    public boolean isSelfOrAdmin(String targetUsername){
        return isSelf(targetUsername) || isAdmin();
    }

    public void requireSelf(String targetUsername) {
        if (!isSelf(targetUsername)) {
            throw new AccessDeniedException();
        }
    }

    public void requireAuditor() {
        if (!isAuditor()) {
            throw new AccessDeniedException();
        }
    }

    public void requireAdmin() {
        if (!isAdmin()) {
            throw new AccessDeniedException();
        }
    }

    public void requireSelfOrAuditor(String targetUsername) {
        if (!isSelfOrAuditor(targetUsername)) {
            throw new AccessDeniedException();
        }
    }

    public void requireSelfOrAdmin(String targetUsername) {
        if (!isSelfOrAdmin(targetUsername)) {
            throw new AccessDeniedException();
        }
    }


    private UserContext getRequiredUserContext() {
        UserContext userContext = UserContextHolder.getUserContext();
        if (userContext == null)
            throw new AccessDeniedException();

        return userContext;
    }
}
