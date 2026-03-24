package net.czming.violation.disposition.mapper;

import net.czming.model.violation.disposition.entity.Appeal;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AppealMapper {

    Appeal selectAppealById(Long id);

    Appeal selectAppealByViolationId(Long ViolationId);

    List<Appeal> selectAppealByAuditorUsername(String AuditorUsername);

    List<Appeal> selectProcessingAppealByAuditorUsername(String AuditorUsername);

    int updateAppeal(Appeal appeal);

    int insertAppeal(Appeal appeal);
}
