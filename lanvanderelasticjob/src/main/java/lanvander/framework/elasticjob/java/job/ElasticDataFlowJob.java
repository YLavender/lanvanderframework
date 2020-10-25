package lanvander.framework.elasticjob.java.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lanvander.framework.elasticjob.java.model.Order;

// 适用场景: 不间歇的数据处理
public class ElasticDataFlowJob implements DataflowJob<Order> {

  private List<Order> orderList;

  {
    orderList = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      Order order = new Order();
      order.setOrderId(i + "");
      order.setStatus("0");
      orderList.add(order);
    }
  }

  @Override
  public List fetchData(ShardingContext shardingContext) {
    var temp =
        orderList.stream()
            .filter(order -> "0".equals(order.getStatus()))
            .filter(
                order ->
                    Integer.valueOf(order.getOrderId()) % shardingContext.getShardingTotalCount()
                        == shardingContext.getShardingItem())
            .collect(Collectors.toList());
    List<Order> subList = null;
    if (temp.size() > 0) {
      subList = temp.subList(0, 10);
    }

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println(
        "time: "
            + LocalTime.now()
            + "\nFragment: "
            + shardingContext.getShardingItem()
            + "\nresult: "
            + subList);

    return subList;
  }

  @Override
  public void processData(ShardingContext shardingContext, List<Order> data) {
    data.forEach(o -> o.setStatus("1"));
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println(
        "time: "
            + LocalTime.now()
            + "\nFragment: "
            + shardingContext.getShardingItem()
            + "\nresult: "
            + data);
  }
}
