package net.czming.violation.disposition.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import net.czming.common.annotation.Loggable;
import net.czming.common.result.R;
import net.czming.model.violation.disposition.dto.EbikeAddDto;
import net.czming.model.violation.disposition.dto.EbikeUpdateDto;
import net.czming.model.violation.disposition.vo.EbikeVo;
import net.czming.violation.disposition.service.EbikeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Loggable
@RestController
@RequestMapping("/ebike")
public class EbikeController {
    private final EbikeService ebikeService;

    public EbikeController(EbikeService ebikeService) {
        this.ebikeService = ebikeService;
    }

    @GetMapping("/get")
    public R<List<EbikeVo>> getEbikes(@RequestParam("ownerUsername")
                                      @NotBlank(message = "ownerUsername不能为空") String ownerUsername) {
        List<EbikeVo> ebikeVoList = ebikeService.getEbikes(ownerUsername);
        return R.ok(ebikeVoList);
    }

    @PostMapping("/update")
    public R<Void> updateEbike(@RequestBody @Valid EbikeUpdateDto ebikeUpdateDto) {
        ebikeService.updateEbike(ebikeUpdateDto);
        return R.ok();
    }

    @PostMapping("/add")
    public R<Void> addEbike(@RequestBody @Valid EbikeAddDto ebikeAddDto) {
        ebikeService.addEbike(ebikeAddDto);
        return R.ok();
    }

    @PostMapping("/add-batch")
    public R<Void> batchAddEbike(@RequestBody @NotEmpty List<@Valid EbikeAddDto> ebikeAddDtoList) {
        ebikeService.batchAddEbike(ebikeAddDtoList);
        return R.ok();
    }

    @PostMapping("/delete")
    public R<Void> deleteEbike(@RequestParam("licensePlate")
                               @NotBlank(message = "licensePlate不能为空") String licensePlate) {
        ebikeService.deleteEbike(licensePlate);
        return R.ok();
    }
}
