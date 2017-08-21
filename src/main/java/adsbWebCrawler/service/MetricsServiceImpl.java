package adsbWebCrawler.service;

import com.beamOps.utility.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.stereotype.Service;

/**
 * 度量服务实现层
 */
@Service("MetricsService")
public class MetricsServiceImpl implements MetricsService{

    @Autowired
    private CounterService counterService;

    @Autowired
    private GaugeService gaugeService;

    @Override
    public void increaseRecord(String name) {
        try {
            counterService.increment(name);
        }catch (Exception ex){
            Log.error(CounterService.class,"increaseRecord() error",ex);
        }
    }

    @Override
    public void decreaseRecord(String name) {
        try {
            counterService.decrement(name);
        }catch (Exception ex){
            Log.error(CounterService.class,"decreaseRecord() error",ex);
        }
    }

    @Override
    public void resetRecord(String name) {
        try {
            counterService.reset(name);
        }catch (Exception ex){
            Log.error(CounterService.class,"resetRecord() error",ex);
        }
    }

    @Override
    public void recordCurValue(String key, double value) {
        try {
            gaugeService.submit(key,value);
        }catch (Exception ex){
            Log.error(CounterService.class,"recordCurValue() error",ex);
        }
    }
}
