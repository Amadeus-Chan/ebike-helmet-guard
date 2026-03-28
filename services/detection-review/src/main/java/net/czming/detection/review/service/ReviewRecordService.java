package net.czming.detection.review.service;

import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.detection.review.mapper.ReviewTaskMapper;
import net.czming.model.detection.review.entity.ReviewTask;
import org.springframework.stereotype.Service;

@Service
public class ReviewRecordService {

    private final ReviewTaskMapper reviewTaskMapper;

    public ReviewRecordService(ReviewTaskMapper reviewTaskMapper) {
        this.reviewTaskMapper = reviewTaskMapper;
    }

    public void addReviewRecord(ReviewTask reviewTask) {
        reviewTaskMapper.insertReviewTask(reviewTask);
    }

    public ReviewTask getReviewRecordById(Long id) {
        ReviewTask reviewTask = reviewTaskMapper.selectReviewTaskById(id);
        if (reviewTask == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标复核记录不存在");
        return reviewTask;
    }

    public void updateReviewRecord(ReviewTask reviewTask) {
        reviewTaskMapper.updateReviewTask(reviewTask);
    }


}
