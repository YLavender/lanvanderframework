package lanvander.quartz.springboot;

import lanvander.utils.PropertiesUtils;
import lanvander.utils.exception.UtilsInitialException;
import org.junit.Test;

public class Tests {

  @Test
  public void properties() throws UtilsInitialException {
    PropertiesUtils.loadProperties("message.properties");
    System.out.println(PropertiesUtils.getProperty("sms.message.url"));
  }
}
