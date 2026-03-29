package net.czming.detection.review.service;

import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.detection.review.mapper.ViolationResultMapper;
import net.czming.model.detection.review.entity.ViolationResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViolationResultService {

    private final ViolationResultMapper violationResultMapper;

    public ViolationResultService(final ViolationResultMapper violationResultMapper) {
        this.violationResultMapper = violationResultMapper;
    }

    public void batchAdd(final List<ViolationResult> violationResultList) {
        if (violationResultList == null || violationResultList.isEmpty()) {
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "violationResultList is null or empty");
        }
        violationResultMapper.batchInsertViolationResult(violationResultList);
    }
}
