package net.czming.detection.review.service;

import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.detection.review.mapper.ReviewTaskMapper;
import net.czming.model.detection.review.entity.ReviewTask;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewTaskService {

    private final ReviewTaskMapper reviewTaskMapper;

    public ReviewTaskService(ReviewTaskMapper reviewTaskMapper) {
        this.reviewTaskMapper = reviewTaskMapper;
    }

    public void addReviewTask(ReviewTask reviewTask) {
        reviewTaskMapper.insertReviewTask(reviewTask);
    }

    public ReviewTask getReviewTaskById(Long id) {
        ReviewTask reviewTask = reviewTaskMapper.selectReviewTaskById(id);
        if (reviewTask == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标复核记录不存在");
        return reviewTask;
    }

    public List<ReviewTask> getPendingReviewTask() {
        return reviewTaskMapper.selectPendingReviewTask();
    }

    public void updateReviewTask(ReviewTask reviewTask) {
        reviewTaskMapper.updateReviewTask(reviewTask);
    }

    public int markProcessingIfPending(Long id) {
        return reviewTaskMapper.markProcessingIfPending(id);
    }

    public void markReviewTaskInvalid(Long id) {
        reviewTaskMapper.markReviewTaskInvalid(id);
    }



}
