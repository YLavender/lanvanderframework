package lanvander.framework.quartz.springboot.service;

import java.util.List;
import lanvander.framework.quartz.springboot.domain.QuartzJob;
import lanvander.framework.quartz.springboot.request.QuartzJobRequest;
import lanvander.framework.quartz.springboot.response.QuartzJobResponse;

public interface QuartzSpringBootService {

  void addJob(QuartzJobRequest request);

  void updateJob(QuartzJobRequest request);

  void updateTrigger(QuartzJobRequest request);

  QuartzJob deleteJob(QuartzJobRequest request);

  void pauseJob(QuartzJobRequest request);

  void resumeJob(QuartzJobRequest request);

  void runJob(QuartzJobRequest request);

  QuartzJobResponse getJob(String jobName, String jobGroupName);

  List<QuartzJobResponse> getAllJobs();

  List<QuartzJobResponse> getExecutingJobs();
}
