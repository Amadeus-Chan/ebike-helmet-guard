package net.czming.violation.disposition.mapper;

import net.czming.model.violation.disposition.entity.Ebike;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface EbikeMapper {

    Ebike selectEbikeByLicensePlate(String licensePlate);

    List<Ebike> selectEbikeByOwnerIdCard( String ownerIdCard);

    int insertEbike(Ebike ebike);

    int batchInsertEbike(List<Ebike> ebikes);

    int updateEbike(Ebike ebike);

    int deleteEbikeByLicensePlate(String licensePlate);
}
