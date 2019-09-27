package lanvander.quartz.springboot.service;

import lanvander.domain.specific.ServerResponse;
import lanvander.quartz.springboot.request.QuartzJobRequest;
import lanvander.quartz.springboot.response.QuartzJobResponse;

import java.util.List;

public interface QuartzSpringBootService {

  ServerResponse addJob(QuartzJobRequest request);

  ServerResponse updateJob(QuartzJobRequest request);

  ServerResponse updateTrigger(QuartzJobRequest request);

  ServerResponse deleteJob(QuartzJobRequest request);

  ServerResponse pauseJob(QuartzJobRequest request);

  ServerResponse resumeJob(QuartzJobRequest request);

  ServerResponse runJob(QuartzJobRequest request);

  ServerResponse<QuartzJobResponse> getJob(String jobName, String jobGroupName);

  ServerResponse<List<QuartzJobResponse>> getAllJobs();

  ServerResponse<List<QuartzJobResponse>> getExecutingJobs();
}
