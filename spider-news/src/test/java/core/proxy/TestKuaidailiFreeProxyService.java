package core.proxy;

import com.tdstack.core.proxy.KuaidailiFreeProxyService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by yangangui on 2018/12/6.
 */
public class TestKuaidailiFreeProxyService {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spider-context.xml");

        KuaidailiFreeProxyService kuaidailiFreeProxyService = ctx.getBean("kuaidailiFreeProxyService", KuaidailiFreeProxyService.class);
        kuaidailiFreeProxyService.getProxy();
    }
}
