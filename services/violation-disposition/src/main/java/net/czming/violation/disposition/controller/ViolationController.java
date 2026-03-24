package net.czming.violation.disposition.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import net.czming.common.annotation.Loggable;
import net.czming.common.result.R;
import net.czming.model.violation.disposition.dto.ViolationAddDto;
import net.czming.model.violation.disposition.vo.ViolationVo;
import net.czming.violation.disposition.service.ViolationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Loggable
@RestController
@RequestMapping("/violation")
public class ViolationController {

    private final ViolationService violationService;

    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    @GetMapping("/get")
    public R<List<ViolationVo>> getViolations(@RequestParam("licensePlate") @NotBlank(message = "licensePlate不能为空") String licensePlate) {
        List<ViolationVo> violationVoList = violationService.getViolations(licensePlate);
        return R.ok(violationVoList);
    }


    @PostMapping("/add")
    public R<Void> addViolation(@RequestBody  @Valid ViolationAddDto violationAddDto) {
        violationService.addViolation(violationAddDto);
        return R.ok();
    }

    @PostMapping("/add-batch")
    public R<Void> batchAddViolation(@RequestBody @NotEmpty List<@Valid ViolationAddDto> violationAddDtoList) {
        violationService.batchAddViolations(violationAddDtoList);
        return R.ok();
    }
}
