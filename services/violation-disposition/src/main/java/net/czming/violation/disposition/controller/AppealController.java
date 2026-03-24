package net.czming.violation.disposition.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.czming.common.annotation.Loggable;
import net.czming.common.result.R;
import net.czming.model.violation.disposition.vo.AppealVo;
import net.czming.violation.disposition.service.AppealService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Loggable
@RequestMapping("/appeal")
@RestController
public class AppealController {
    private final AppealService appealService;

    public AppealController(final AppealService appealService) {
        this.appealService = appealService;
    }

    @GetMapping("/get")
    public R<List<AppealVo>> getAppeals(@RequestParam("auditorUsername") @NotBlank(message = "auditorUsername不能为空") String auditorUsername) {
        List<AppealVo> appeals = appealService.getAppeals(auditorUsername);
        return R.ok(appeals);
    }

    @GetMapping("/get-processing")
    public R<List<AppealVo>> getProcessingAppeals(@RequestParam("auditorUsername") @NotBlank(message = "auditorUsername不能为空") String auditorUsername) {
        List<AppealVo> processingAppeals = appealService.getProcessingAppeals(auditorUsername);
        return R.ok(processingAppeals);
    }


    @PostMapping("/approve")
    public R<Void> appealApprove(@RequestParam("appealId") @NotNull Long appealId) {
        appealService.appealApprove(appealId);
        return R.ok();
    }

    @PostMapping("/reject")
    public R<Void> appealReject(@RequestParam("appealId") @NotNull Long appealId) {
        appealService.appealReject(appealId);
        return R.ok();
    }

    @PostMapping("create")
    public R<Void> appeal(@RequestParam("violationId") @NotNull Long violationId) {
        appealService.appeal(violationId);
        return R.ok();
    }
}
