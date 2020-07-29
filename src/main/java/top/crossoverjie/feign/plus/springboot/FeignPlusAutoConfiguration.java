package top.crossoverjie.feign.plus.springboot;

import feign.Client;
import feign.Contract;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import top.crossoverjie.feign.plus.annotation.AnnotatedParameterProcessor;
import top.crossoverjie.feign.plus.support.SpringMvcContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020/7/25 01:16
 * @since JDK 11
 */
@Configuration
@EnableConfigurationProperties(FeignPlusConfigurationProperties.class)
public class FeignPlusAutoConfiguration {

    @Autowired(required = false)
    private List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();


    private FeignPlusConfigurationProperties feignPlusConfigurationProperties;

    public FeignPlusAutoConfiguration(FeignPlusConfigurationProperties feignPlusConfigurationProperties) {
        this.feignPlusConfigurationProperties = feignPlusConfigurationProperties;
    }

    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(feignPlusConfigurationProperties.getMaxIdleConnections(),
                feignPlusConfigurationProperties.getKeepAliveDuration(), TimeUnit.MINUTES);
    }

    @Bean(value = "feignContract")
    @ConditionalOnExpression("'mvc'.equals('${feign.contract:mvc}')")
    public Contract feignContract(ConversionService feignConversionService) {
        return new SpringMvcContract(this.parameterProcessors, feignConversionService);
    }

    @Bean(value = "feignContract")
    @ConditionalOnExpression("'feign'.equals('${feign.contract:mvc}')")
    public Contract defaultContract() {
        return new Contract.Default();
    }

    @Bean(value = "client")
    @ConditionalOnExpression("'okhttp3'.equals('${feign.httpclient:okhttp3}')")
    public Client okHttpClient(ConnectionPool connectionPool) {
        OkHttpClient delegate = new OkHttpClient().newBuilder()
                .connectionPool(connectionPool)
                .connectTimeout(feignPlusConfigurationProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(feignPlusConfigurationProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(feignPlusConfigurationProperties.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .build();
        return new feign.okhttp.OkHttpClient(delegate);
    }

    /*@Bean(value = "client")
    @ConditionalOnExpression("'http2Client'.equals('${feign.httpclient:okhttp3}')")
    public Client client(){
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofMillis(feignPlusConfigurationProperties.getConnectTimeout()))
                .build();
        return new Http2Client(httpClient) ;
    }*/
}
