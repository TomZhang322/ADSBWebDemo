package adsbWebCrawler.service;

import org.springframework.stereotype.Service;

/**
 * 度量服务层
 */
@Service
public interface MetricsService {

    /**
     * 新增记录
     */
    public void increaseRecord(String name);

    /**
     * 减少记录
     * @param name
     */
    public void decreaseRecord(String name);

    /**
     * 重置记录
     * @param name
     */
    public void resetRecord(String name);

    /**
     * 记录当前数值
     */
    public void recordCurValue(String key, double value);
}
