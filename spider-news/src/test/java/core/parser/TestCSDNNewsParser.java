package core.parser;

import com.tdstack.core.parser.impl.CSDNNewsParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by yangangui on 2018/11/21.
 */
public class TestCSDNNewsParser {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spider-context.xml");

        CSDNNewsParser csdnNewsParser = ctx.getBean("csdnNewsParser", CSDNNewsParser.class);
        csdnNewsParser.process();
    }
}
