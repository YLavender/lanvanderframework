package lanvander.framework.quartz.springboot.mapper;

import java.util.List;
import lanvander.framework.quartz.springboot.domain.QuartzJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuartzJobMapper {

  int deleteByPrimaryKey(Integer id);

  int insert(QuartzJob record);

  int insertSelective(QuartzJob record);

  QuartzJob selectByPrimaryKey(Integer id);

  QuartzJob selectByJob(@Param("name") String jobName, @Param("group") String jobGroupName);

  List<QuartzJob> getAllJobs();

  int updateByPrimaryKeySelective(QuartzJob record);

  int updateByPrimaryKeyWithBLOBs(QuartzJob record);

  int updateByPrimaryKey(QuartzJob record);
}
