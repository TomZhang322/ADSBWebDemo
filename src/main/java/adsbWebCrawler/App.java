package adsbWebCrawler;

import com.beamOps.utility.log.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ADS-B网络爬虫应用启动类
 */
@SpringBootApplication
public class App {

    public static void main(String[] args){
        SpringApplication.run(App.class, args);
        Log.info("ADS-B WebCrawler started");
    }
}
