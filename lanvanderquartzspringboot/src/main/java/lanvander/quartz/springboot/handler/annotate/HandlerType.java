package lanvander.quartz.springboot.handler.annotate;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerType {
  String value();
}
