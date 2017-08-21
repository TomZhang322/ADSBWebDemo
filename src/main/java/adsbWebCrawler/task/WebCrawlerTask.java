package adsbWebCrawler.task;

import adsbWebCrawler.configuration.AppConfiguration;
import adsbWebCrawler.configuration.AreaProperties;
import adsbWebCrawler.entity.ADSB;
import adsbWebCrawler.service.MetricsService;
import adsbWebCrawler.service.WebCrawlerService;
import adsbWebCrawler.util.MQUtil;
import com.beamOps.utility.log.Log;
import com.beamOps.utility.xml.BeamOpsXML2;
import com.beamOps.utility.xml.BeamOpsXMLFactory;
import com.beamOps.utility.xml.Node;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zhaoke on 2017/6/16.
 */
@Component
@EnableScheduling
public class WebCrawlerTask {

    @Autowired
    private AppConfiguration configuration;

    @Autowired
    private AreaProperties properties;

    @Autowired
    private MQUtil mqUtil;

    @Autowired
    private WebCrawlerService webCrawlerService;

    @Autowired
    private MetricsService metricsService;

    /**
     * 生成XML
     * @param data
     * @return
     */
    private String toXML(String data){
        try{
            BeamOpsXML2 xml = BeamOpsXMLFactory.getInstance().createBeamOpsXML2();

            // 创建根节点
            xml.setRootName("beamOps");
            xml.getRoot().put("type", "ADS-B");
            xml.getRoot().put("source", "Fr24");
            xml.getRoot().put("rcvTime", DateTime.now(DateTimeZone.UTC).toString("yyyy-MM-dd HH:mm:ss"));
            Node node = new Node("data");
            node.setText(data);
            xml.addNode(node);
            return xml.createXMLString();
        }catch (Exception ex){
            Log.error(WebCrawlerTask.class,"toXML() error",ex);
            return null;
        }
    }

    @Scheduled(cron = "30 */1 * * * *")
    private void doTask(){
        try{
            Log.info("execute task begin");
            metricsService.increaseRecord("webCrawlerTask");
            String url = configuration.getUrl();
            List<String> coordinates = properties.getCoordinates();
            if(!Strings.isNullOrEmpty(url) && (coordinates != null && coordinates.size() > 0)){
                for(String coordinate : coordinates){
                    String strUrl = String.format(url,coordinate, DateTime.now().getMillis());
                    Optional<List<ADSB>> optional = webCrawlerService.getADSB(strUrl);
                    if(optional.isPresent()){
                        Log.info("get ADS-B count:" + optional.get().size());
                        metricsService.recordCurValue(coordinate,optional.get().size());
                        String strQueue = configuration.getSendQueue();
                        long lExpired = configuration.getSendExpiredTime();
                        String strData = new ObjectMapper().writeValueAsString(optional.get());
                        String strMsg = toXML(strData);
                        if(!Strings.isNullOrEmpty(strMsg)){
                            mqUtil.send(strQueue,strMsg,lExpired);
                            Log.debug(WebCrawlerTask.class,"send ADS-B message:\r\n" + strMsg);
                        }
                    }
                    Thread.sleep(2000);
                }
            }
            Log.info("execute task end");
        }catch (Exception ex){
            Log.error(WebCrawlerTask.class,"doTask() error",ex);
        }
    }
}
