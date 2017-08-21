package adsbWebCrawler.service;

import adsbWebCrawler.entity.ADSB;
import com.google.common.base.Optional;

import java.util.List;

/**
 * 网络爬虫服务层
 */
public interface WebCrawlerService {

    /**
     * 取得ADS-B数据
     * @param url
     * @return
     */
    public Optional<List<ADSB>> getADSB(String url);
}
