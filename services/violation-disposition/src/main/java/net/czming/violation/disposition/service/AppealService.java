package net.czming.violation.disposition.service;

import io.github.linpeilie.Converter;
import net.czming.model.violation.disposition.entity.Appeal;
import net.czming.model.violation.disposition.entity.Violation;
import net.czming.model.violation.disposition.vo.AppealVo;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.violation.disposition.mapper.AppealMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppealService {

    private final AppealMapper appealMapper;

    private final ViolationService violationService;

    private final UserService userService;

    private final PermissionService permissionService;

    private final Converter converter;

    public AppealService(AppealMapper appealMapper, ViolationService violationService, UserService userService, PermissionService permissionService, Converter converter) {
        this.appealMapper = appealMapper;
        this.violationService = violationService;
        this.userService = userService;
        this.permissionService = permissionService;
        this.converter = converter;
    }

    public List<AppealVo> getAppeals(String auditorUsername) {

        permissionService.requireSelfOrAdmin(auditorUsername);

        return appealMapper.selectAppealByAuditorUsername(auditorUsername).stream().map(
                appeal -> converter.convert(appeal, AppealVo.class)
        ).toList();
    }

    public List<AppealVo> getProcessingAppeals(String auditorUsername) {

        permissionService.requireSelfOrAdmin(auditorUsername);

        return appealMapper.selectProcessingAppealByAuditorUsername(auditorUsername).stream().map(
                appeal -> converter.convert(appeal, AppealVo.class)
        ).toList();
    }


    @Transactional
    public void appealApprove(Long appealId) {

        permissionService.requireAuditor();

        Appeal appeal = getAppeal(appealId);
        if (appeal.getStatus() != Appeal.Status.PROCESSING)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "该申诉已审核");

        appeal.setStatus(Appeal.Status.APPROVED);
        appeal.setProcessTime(LocalDateTime.now());

        appealMapper.updateAppeal(appeal);
        violationService.updateViolationStatus(appeal.getViolationId(), Violation.Status.APPEAL_APPROVED);
    }


    @Transactional
    public void appealReject(Long appealId) {

        permissionService.requireAuditor();

        Appeal appeal = getAppeal(appealId);
        if (appeal.getStatus() != Appeal.Status.PROCESSING)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "该申诉已审核");

        appeal.setStatus(Appeal.Status.REJECTED);
        appeal.setProcessTime(LocalDateTime.now());


        appealMapper.updateAppeal(appeal);
        violationService.updateViolationStatus(appeal.getViolationId(), Violation.Status.APPEAL_REJECTED);
    }


    @Transactional
    public void appeal(Long violationId) {

        String violatorUsername = violationService.getViolatorUsername(violationId);
        permissionService.requireSelfOrAdmin(violatorUsername);

        String auditorUsername = userService.getRandomAuditorName();

        Appeal appeal = appealMapper.selectAppealByViolationId(violationId);
        if (appeal != null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "该违规已申诉");

        appeal = Appeal.builder()
                .violationId(violationId)
                .createTime(LocalDateTime.now())
                .auditorUsername(auditorUsername)
                .status(Appeal.Status.PROCESSING)
                .processTime(null)
                .build();

        appealMapper.insertAppeal(appeal);
        violationService.updateViolationStatus(violationId, Violation.Status.APPEALING);
    }

    private Appeal getAppeal(Long appealId) {

        Appeal appeal = appealMapper.selectAppealById(appealId);
        if (appeal == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标申诉不存在");

        String auditorUsername = appeal.getAuditorUsername();

        permissionService.requireSelfOrAdmin(auditorUsername);

        return appeal;
    }
}
