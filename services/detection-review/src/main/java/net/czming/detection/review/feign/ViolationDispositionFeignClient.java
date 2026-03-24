package net.czming.detection.review.feign;

import jakarta.validation.Valid;
import net.czming.common.result.R;
import net.czming.detection.review.feign.config.ViolationDispositionFeignConfig;
import net.czming.model.violation.disposition.dto.ViolationAddDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "violation-disposition", configuration = ViolationDispositionFeignConfig.class)
public interface ViolationDispositionFeignClient {

    @PostMapping("/violation/add")
    R<Void> addViolation(@RequestBody @Valid ViolationAddDto violationAddDto);

    @PostMapping("/violation/add-batch")
    R<Void> batchAddViolation(@RequestBody List<@Valid ViolationAddDto> violationAddDtoList);
}
