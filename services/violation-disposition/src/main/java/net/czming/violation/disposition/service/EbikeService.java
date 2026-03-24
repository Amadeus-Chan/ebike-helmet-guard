package net.czming.violation.disposition.service;

import io.github.linpeilie.Converter;
import net.czming.model.violation.disposition.dto.EbikeAddDto;
import net.czming.model.violation.disposition.dto.EbikeUpdateDto;
import net.czming.model.violation.disposition.entity.Ebike;
import net.czming.model.violation.disposition.vo.EbikeVo;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.violation.disposition.mapper.EbikeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EbikeService {

    private final EbikeMapper ebikeMapper;

    private final UserService userService;

    private final PermissionService permissionService;

    private final Converter converter;

    public EbikeService(EbikeMapper ebikeMapper, UserService userService, PermissionService permissionService, Converter converter) {
        this.ebikeMapper = ebikeMapper;
        this.userService = userService;
        this.permissionService = permissionService;
        this.converter = converter;
    }

    public List<EbikeVo> getEbikes(String ownerUsername) {

        permissionService.requireSelfOrAdmin(ownerUsername);

        String idCard = userService.getUserIdCard(ownerUsername);
        List<Ebike> ebikeList = ebikeMapper.selectEbikeByOwnerIdCard(idCard);
        return ebikeList.stream().map(
                ebike -> converter.convert(ebike, EbikeVo.class)
        ).toList();
    }

    public void updateEbike(EbikeUpdateDto ebikeUpdateDto) {
        permissionService.requireAdmin();

        Ebike ebike = ebikeMapper.selectEbikeByLicensePlate(ebikeUpdateDto.getLicensePlate());
        if (ebike == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标车辆不存在");

        converter.convert(ebikeUpdateDto, ebike);
        ebikeMapper.updateEbike(ebike);
    }

    public void addEbike(EbikeAddDto ebikeAddDto) {
        permissionService.requireAdmin();

        Ebike convert = converter.convert(ebikeAddDto, Ebike.class);
        ebikeMapper.insertEbike(convert);
    }

    public void batchAddEbike(List<EbikeAddDto> ebikeAddDtoList) {
        permissionService.requireAdmin();

        List<Ebike> ebikeList = ebikeAddDtoList.stream().map(
                ebikeAddDto -> converter.convert(ebikeAddDto, Ebike.class)
        ).toList();

        ebikeMapper.batchInsertEbike(ebikeList);
    }

    public void deleteEbike(String licensePlate) {
        permissionService.requireAdmin();

        int num = ebikeMapper.deleteEbikeByLicensePlate(licensePlate);
        if (num == 0)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标车辆不存在");
    }

     String getEbikeOwnerUsername(String licensePlate) {

        if (licensePlate == null || licensePlate.isBlank())
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "非法的车牌号");

        Ebike ebike = ebikeMapper.selectEbikeByLicensePlate(licensePlate);

        if (ebike == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标车辆不存在");

        return userService.getUsernameByIdCard(ebike.getOwnerIdCard());
    }

}
