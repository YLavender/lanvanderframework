package lanvander.framework.elasticjob.java.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Order {
  private String orderId;
  private String status;
}
