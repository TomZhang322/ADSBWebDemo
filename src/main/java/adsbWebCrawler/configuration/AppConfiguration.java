package adsbWebCrawler.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 应用配置类
 */
@Configuration
@EnableConfigurationProperties({AreaProperties.class,AirlineProperties.class})
public class AppConfiguration {

    // URL
    @Value("${url}")
    private String url;
    
    // sendQueue
    @Value("${sendQueue}")
    private String sendQueue;

    // sendExpiredTime
    @Value("${sendExpiredTime}")
    private long sendExpiredTime;

    public String getUrl(){
        return url;
    }

    public String getSendQueue() {
        return sendQueue;
    }

    public long getSendExpiredTime(){
        return sendExpiredTime;
    }
}
