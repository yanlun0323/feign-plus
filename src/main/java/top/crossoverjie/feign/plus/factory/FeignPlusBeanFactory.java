package top.crossoverjie.feign.plus.factory;

import feign.*;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import top.crossoverjie.feign.plus.springboot.FeignPlusConfigurationProperties;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020/7/25 02:20
 * @since JDK 11
 */
public class FeignPlusBeanFactory<T> implements FactoryBean<T>, ApplicationContextAware {


    private ApplicationContext context;

    private Class<T> proxyInterface;

    private String url;


    @Override
    public T getObject() throws Exception {
        FeignPlusConfigurationProperties conf = this.context.getBean(FeignPlusConfigurationProperties.class);
        Client client;
        try {
            client = this.context.getBean("client", Client.class);
        } catch (NoSuchBeanDefinitionException e) {
            throw new NullPointerException("Without one of [okhttp3, Http2Client] client.");
        }
        T target = Feign.builder()
                .client(client)
                // .addCapability(new SpringMvcContract(this.parameterProcessors, feignConversionService))
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .contract(this.get("feignContract", Contract.class))
                .retryer(new Retryer.Default(100, SECONDS.toMillis(1), 0))
                .options(new Request.Options(conf.getConnectTimeout(), conf.getReadTimeout(), true))
                .target(proxyInterface, url);

        return target;
    }


    protected <T> T get(String name, Class<T> type) {
        return context.getBean(name, type);
    }

    @Override
    public Class<?> getObjectType() {
        return proxyInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public Class<T> getProxyInterface() {
        return proxyInterface;
    }

    public void setProxyInterface(Class<T> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
