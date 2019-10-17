package lanvander.framework.elasticjob.java;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import lanvander.framework.elasticjob.java.job.ElasticSimpleJob;

public class ElasticJobDemo {

  public static void main(String[] args) {
    System.out.println("Demo Start.");
    new JobScheduler(zooKeeperCenter(), configuration()).init();
  }

  private static CoordinatorRegistryCenter zooKeeperCenter() {
    ZookeeperConfiguration zookeeperConfiguration =
        new ZookeeperConfiguration("localhost:2181", "lanvander-elastic-job");
    CoordinatorRegistryCenter coordinatorRegistryCenter =
        new ZookeeperRegistryCenter(zookeeperConfiguration);
    coordinatorRegistryCenter.init();
    return coordinatorRegistryCenter;
  }

  private static LiteJobConfiguration configuration() {
    JobCoreConfiguration jobCoreConfiguration =
        JobCoreConfiguration.newBuilder("simpleJob", "0/10 * * * * ?", 2).build();

    // job.class 需要全包名
    JobTypeConfiguration jobTypeConfiguration =
        new SimpleJobConfiguration(jobCoreConfiguration, ElasticSimpleJob.class.getCanonicalName());

    return LiteJobConfiguration.newBuilder(jobTypeConfiguration).build();
  }
}
