package core.process;

import com.tdstack.core.process.impl.CSDNNewsProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by yangangui on 2018/11/21.
 */
public class TestCSDNNewsProcessor {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spider-context.xml");

        CSDNNewsProcessor csdnNewsParser = ctx.getBean("csdnNewsParser", CSDNNewsProcessor.class);
        csdnNewsParser.process();
    }
}
