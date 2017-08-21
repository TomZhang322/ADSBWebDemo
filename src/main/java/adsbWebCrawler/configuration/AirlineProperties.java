package adsbWebCrawler.configuration;

import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 航空公司配置
 */
@ConfigurationProperties(prefix = "airline")
public class AirlineProperties {

    // 航空公司2码
    private List<String> code2 = Lists.newArrayListWithExpectedSize(100);

    public List<String> getCode2() {
        return code2;
    }
}
