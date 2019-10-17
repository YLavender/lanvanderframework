package lanvander.framework.quartz.springboot.mapper;

import lanvander.framework.quartz.springboot.request.Job;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobMapper {
  int deleteByPrimaryKey(Integer id);

  int insert(Job record);

  int insertSelective(Job record);

  Job selectByPrimaryKey(Integer id);

  Job selectByJob(@Param("name") String jobName, @Param("group") String jobGroupName);

  List<Job> getAllJobs();

  int updateByPrimaryKeySelective(Job record);

  int updateByPrimaryKeyWithBLOBs(Job record);

  int updateByPrimaryKey(Job record);
}
