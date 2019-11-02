package lanvander.framework.elasticjob.java;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import lanvander.framework.elasticjob.java.job.ElasticDataFlowJob;
import lanvander.framework.elasticjob.java.job.ElasticSimpleJob;

public class ElasticJobDemo {

  public static void main(String[] args) {
    System.out.println("Demo Start.");
    //    new JobScheduler(zooKeeperCenter(), configuration()).init();
    //    new JobScheduler(zooKeeperCenter(), dataFlowConfiguration()).init();
    new JobScheduler(zooKeeperCenter(), scriptConfiguration()).init();
  }

  private static CoordinatorRegistryCenter zooKeeperCenter() {
    // ZooKeeper集群设置模板 localhost:2181,localhost:2182
    var zookeeperConfiguration =
        new ZookeeperConfiguration("localhost:2181", "lanvander-elastic-job");
    var coordinatorRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
    coordinatorRegistryCenter.init();
    System.out.println("ZooKeeper init success.");
    return coordinatorRegistryCenter;
  }

  private static LiteJobConfiguration configuration() {
    var jobCoreConfiguration =
        JobCoreConfiguration.newBuilder("simpleJob", "0/10 * * * * ?", 2).build();

    // job.class 需要全包名
    var jobTypeConfiguration =
        new SimpleJobConfiguration(jobCoreConfiguration, ElasticSimpleJob.class.getCanonicalName());

    System.out.println("SimpleJobConfiguration init success.");
    // overwrite() true - 启动的时候覆盖原有配置
    return LiteJobConfiguration.newBuilder(jobTypeConfiguration).overwrite(true).build();
  }

  private static LiteJobConfiguration dataFlowConfiguration() {
    var jobCoreConfiguration =
        JobCoreConfiguration.newBuilder("dataFlowJob", "0/10 * * * * ?", 2).build();

    /*
     * job.class 需要全包名
     * streamingProcess 是否使用流式处理
     */
    var jobTypeConfiguration =
        new DataflowJobConfiguration(
            jobCoreConfiguration, ElasticDataFlowJob.class.getCanonicalName(), true);

    System.out.println("DataFlowJobConfiguration init success.");
    // overwrite() true - 启动的时候覆盖原有配置
    return LiteJobConfiguration.newBuilder(jobTypeConfiguration).overwrite(true).build();
  }

  private static LiteJobConfiguration scriptConfiguration() {
    var jobCoreConfiguration =
        JobCoreConfiguration.newBuilder("scriptJob", "0/10 * * * * ?", 2).misfire(false).build();

    /* 脚本作业
     * 1. 直接写命令
     * 2. 传脚本文件路径
     *    (Windows系统可执行, Mac系统执行不了, 暂时不清楚原因)
     *    (另外Windows系统中通过ClassLoader获取路径也执行不了)
     */
    var jobTypeConfiguration =
        new ScriptJobConfiguration(
            jobCoreConfiguration,
            "/Users/yanlei/Workspace/IdeaProjects/CommonProjects/yanlei/study/lanvanderframework/lanvanderelasticjob/src/main/resources/scriptJob.sh");

    System.out.println("ScriptJobConfiguration init success.");
    // overwrite() true - 启动的时候覆盖原有配置
    return LiteJobConfiguration.newBuilder(jobTypeConfiguration).overwrite(true).build();
  }
}
