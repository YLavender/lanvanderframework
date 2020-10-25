package lanvander.framework.elasticjob.java.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import java.time.LocalTime;

public class ElasticSimpleJob implements SimpleJob {

  public void execute(ShardingContext shardingContext) {
    System.out.println("当前时间: " + LocalTime.now());
    System.out.println("当前分片项: " + shardingContext.getShardingItem());
    System.out.println("分片总数: " + shardingContext.getShardingTotalCount());
  }
}
