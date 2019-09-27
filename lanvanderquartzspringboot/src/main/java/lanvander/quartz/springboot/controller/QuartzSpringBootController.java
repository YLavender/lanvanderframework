package lanvander.quartz.springboot.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lanvander.domain.specific.ServerResponse;
import lanvander.quartz.springboot.request.QuartzJobRequest;
import lanvander.quartz.springboot.response.QuartzJobResponse;
import lanvander.quartz.springboot.service.QuartzSpringBootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quartz")
@Api(value = "/quartz", description = "动态定时任务控制器")
@ResponseBody
public class QuartzSpringBootController {

  @Autowired private QuartzSpringBootService quartzSpringBootService;

  @RequestMapping(value = "/addJob", method = RequestMethod.POST)
  @ApiOperation(value = "新建定时任务", httpMethod = "POST", notes = "/addJob")
  public ServerResponse addJob(@RequestBody QuartzJobRequest request) {
    return quartzSpringBootService.addJob(request);
  }

  @RequestMapping(value = "/updateJob", method = RequestMethod.POST)
  @ApiOperation(value = "更新定时任务", httpMethod = "POST", notes = "/updateJob")
  public ServerResponse updateJob(@RequestBody QuartzJobRequest request) {
    return quartzSpringBootService.updateJob(request);
  }

  @RequestMapping(value = "/updateTrigger", method = RequestMethod.POST)
  @ApiOperation(value = "更新任务的触发器", httpMethod = "POST", notes = "/updateTrigger")
  public ServerResponse updateTrigger(@RequestBody QuartzJobRequest request) {
    return quartzSpringBootService.updateTrigger(request);
  }

  @RequestMapping(value = "/deleteJob", method = RequestMethod.POST)
  @ApiOperation(value = "删除定时任务", httpMethod = "POST", notes = "/deleteJob")
  public ServerResponse deleteJob(@RequestBody QuartzJobRequest request) {
    return quartzSpringBootService.deleteJob(request);
  }

  @RequestMapping(value = "/pauseJob", method = RequestMethod.POST)
  @ApiOperation(value = "暂停定时任务", httpMethod = "POST", notes = "/pauseJob")
  public ServerResponse pauseJob(@RequestBody QuartzJobRequest request) {
    return quartzSpringBootService.pauseJob(request);
  }

  @RequestMapping(value = "/resumeJob", method = RequestMethod.POST)
  @ApiOperation(value = "唤醒定时任务", httpMethod = "POST", notes = "/resumeJob")
  public ServerResponse resumeJob(@RequestBody QuartzJobRequest request) {
    return quartzSpringBootService.resumeJob(request);
  }

  @RequestMapping(value = "/runJob", method = RequestMethod.POST)
  @ApiOperation(value = "执行定时任务", httpMethod = "POST", notes = "/runJob")
  public ServerResponse runJob(@RequestBody QuartzJobRequest request) {
    return quartzSpringBootService.runJob(request);
  }

  @RequestMapping(value = "/getJob", method = RequestMethod.GET)
  @ApiOperation(value = "获取指定的定时任务", httpMethod = "GET", notes = "/getJob")
  public ServerResponse<QuartzJobResponse> getJob(
      @RequestParam("job") String jobName, @RequestParam("group") String jobGroupName) {
    return quartzSpringBootService.getJob(jobName, jobGroupName);
  }

  @RequestMapping(value = "/getAllJobs", method = RequestMethod.GET)
  @ApiOperation(value = "获取所有定时任务", httpMethod = "GET", notes = "/getAllJobs")
  public ServerResponse<List<QuartzJobResponse>> getAllJobs() {
    return quartzSpringBootService.getAllJobs();
  }

  @RequestMapping(value = "/getExecutingJobs", method = RequestMethod.GET)
  @ApiOperation(value = "获取执行中的定时任务", httpMethod = "GET", notes = "/getExecutingJobs")
  public ServerResponse<List<QuartzJobResponse>> getExecutingJobs() {
    return quartzSpringBootService.getExecutingJobs();
  }
}
