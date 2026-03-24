package net.czming.detection.review.service;

import net.czming.detection.review.mapper.ReviewRecordMapper;
import net.czming.model.detection.review.entity.ReviewRecord;
import org.springframework.stereotype.Service;

@Service
public class ReviewRecordService {

    private final ReviewRecordMapper reviewRecordMapper;

    public ReviewRecordService(ReviewRecordMapper reviewRecordMapper) {
        this.reviewRecordMapper = reviewRecordMapper;
    }

    public void addReviewRecord(ReviewRecord reviewRecord) {
        reviewRecordMapper.insertReviewRecord(reviewRecord);
    }

    public void updateReviewRecordViolate(Long id, Boolean violated) {
        reviewRecordMapper.updateViolated(id, violated);
    }
}
