package lanvander.framework.quartz.springboot.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lanvander.domain.specific.ServerResponse;
import lanvander.framework.quartz.springboot.request.QuartzJobRequest;
import lanvander.framework.quartz.springboot.service.QuartzSpringBootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quartz")
@Api(value = "/quartz", tags = "定时任务接口")
public class QuartzSpringBootController {

    @Autowired
    private QuartzSpringBootService quartzSpringBootService;

    @RequestMapping(value = "/addJob", method = RequestMethod.POST)
    @ApiOperation(value = "新建定时任务", httpMethod = "POST", notes = "/addJob")
    public ServerResponse<?> addJob(@RequestBody QuartzJobRequest request) {
        quartzSpringBootService.addJob(request);
        return ServerResponse.success();
    }

    @RequestMapping(value = "/updateJob", method = RequestMethod.POST)
    @ApiOperation(value = "更新定时任务", httpMethod = "POST", notes = "/updateJob")
    public ServerResponse<?> updateJob(@RequestBody QuartzJobRequest request) {
        quartzSpringBootService.updateJob(request);
        return ServerResponse.success();
    }

    @RequestMapping(value = "/updateTrigger", method = RequestMethod.POST)
    @ApiOperation(value = "更新任务的触发器", httpMethod = "POST", notes = "/updateTrigger")
    public ServerResponse<?> updateTrigger(@RequestBody QuartzJobRequest request) {
        quartzSpringBootService.updateTrigger(request);
        return ServerResponse.success();
    }

    @RequestMapping(value = "/deleteJob", method = RequestMethod.POST)
    @ApiOperation(value = "删除定时任务", httpMethod = "POST", notes = "/deleteJob")
    public ServerResponse<?> deleteJob(@RequestBody QuartzJobRequest request) {
        return ServerResponse.success().setData(quartzSpringBootService.deleteJob(request));
    }

    @RequestMapping(value = "/pauseJob", method = RequestMethod.POST)
    @ApiOperation(value = "暂停定时任务", httpMethod = "POST", notes = "/pauseJob")
    public ServerResponse<?> pauseJob(@RequestBody QuartzJobRequest request) {
        quartzSpringBootService.pauseJob(request);
        return ServerResponse.success();
    }

    @RequestMapping(value = "/resumeJob", method = RequestMethod.POST)
    @ApiOperation(value = "唤醒定时任务", httpMethod = "POST", notes = "/resumeJob")
    public ServerResponse<?> resumeJob(@RequestBody QuartzJobRequest request) {
        quartzSpringBootService.resumeJob(request);
        return ServerResponse.success();
    }

    @RequestMapping(value = "/runJob", method = RequestMethod.POST)
    @ApiOperation(value = "执行定时任务", httpMethod = "POST", notes = "/runJob")
    public ServerResponse<?> runJob(@RequestBody QuartzJobRequest request) {
        quartzSpringBootService.runJob(request);
        return ServerResponse.success();
    }

    @RequestMapping(value = "/getJob", method = RequestMethod.GET)
    @ApiOperation(value = "获取指定的定时任务", httpMethod = "GET", notes = "/getJob")
    public ServerResponse<?> getJob(
            @RequestParam("job") String jobName, @RequestParam("group") String jobGroupName) {
        return ServerResponse.success().setData(quartzSpringBootService.getJob(jobName, jobGroupName));
    }

    @RequestMapping(value = "/getAllJobs", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有定时任务", httpMethod = "GET", notes = "/getAllJobs")
    public ServerResponse<?> getAllJobs() {
        return ServerResponse.success().setData(quartzSpringBootService.getAllJobs());
    }

    @RequestMapping(value = "/getExecutingJobs", method = RequestMethod.GET)
    @ApiOperation(value = "获取执行中的定时任务", httpMethod = "GET", notes = "/getExecutingJobs")
    public ServerResponse<?> getExecutingJobs() {
        return ServerResponse.success().setData(quartzSpringBootService.getExecutingJobs());
    }
}
