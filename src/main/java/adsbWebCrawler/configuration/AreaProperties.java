package adsbWebCrawler.configuration;

import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 地区配置
 */
@ConfigurationProperties(prefix = "area")
public class AreaProperties {

    // 地区坐标
    private List<String> coordinates = Lists.newArrayListWithExpectedSize(7);

    public List<String> getCoordinates() {
        return coordinates;
    }
}
