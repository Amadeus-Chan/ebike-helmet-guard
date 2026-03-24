package net.czming.violation.disposition.service;

import io.github.linpeilie.Converter;
import net.czming.model.violation.disposition.dto.ViolationAddDto;
import net.czming.model.violation.disposition.entity.User;
import net.czming.model.violation.disposition.entity.Violation;
import net.czming.model.violation.disposition.vo.ViolationVo;
import net.czming.common.exception.AccessDeniedException;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.violation.disposition.mapper.ViolationMapper;
import net.czming.model.violation.disposition.context.UserContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViolationService {

    private final ViolationMapper violationMapper;

    private final EbikeService ebikeService;

    private final PermissionService permissionService;

    private final Converter converter;

    public ViolationService(ViolationMapper violationMapper,PermissionService permissionService, EbikeService ebikeService, Converter converter) {
        this.violationMapper = violationMapper;
        this.ebikeService = ebikeService;
        this.permissionService = permissionService;
        this.converter = converter;
    }

    public List<ViolationVo> getViolations(String licensePlate) {

        String ebikeOwnerUsername = ebikeService.getEbikeOwnerUsername(licensePlate);
        permissionService.requireSelfOrAdmin(ebikeOwnerUsername);

        List<Violation> violationList = violationMapper.selectViolationsByLicensePlate(licensePlate);

        return violationList.stream().map(
                violation -> converter.convert(violation, ViolationVo.class)
        ).toList();
    }

    public void addViolation(ViolationAddDto violationAddDto) {
        Violation violation = converter.convert(violationAddDto, Violation.class);
        violation.setStatus(Violation.Status.UNAPPEALED);

        violationMapper.insertViolation(violation);
    }

    public void batchAddViolations(List<ViolationAddDto> violationAddDtoList) {
        List<Violation> violationList = violationAddDtoList.stream().map(
                violationAddDto -> {
                    Violation violation = converter.convert(violationAddDto, Violation.class);
                    violation.setStatus(Violation.Status.UNAPPEALED);
                    return violation;
                }
        ).toList();

        violationMapper.batchInsertViolation(violationList);
    }

    public void updateViolationStatus(Long violationId, Violation.Status status) {

        if (violationId == null || status == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "violationId或status不能为null");


        Violation violation = violationMapper.selectViolationById(violationId);

        if (violation == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标违规不存在");

        violation.setStatus(status);
        violationMapper.updateViolation(violation);
    }

    public String getViolatorUsername(Long violationId) {

        if (violationId == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "violationId不能为null");

        Violation violation = violationMapper.selectViolationById(violationId);
        if (violation == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标违规不存在");

        String licensePlate = violation.getLicensePlate();
        return ebikeService.getEbikeOwnerUsername(licensePlate);
    }

}
