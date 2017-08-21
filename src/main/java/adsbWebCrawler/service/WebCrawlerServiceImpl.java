package adsbWebCrawler.service;

import adsbWebCrawler.configuration.AirlineProperties;
import adsbWebCrawler.entity.ADSB;
import adsbWebCrawler.util.StringUtil;
import com.beamOps.utility.log.Log;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;


/**
 * 网络爬虫服务实现层
 */
@Service("WebCrawlerService")
public class WebCrawlerServiceImpl implements WebCrawlerService{

    @Autowired
    private AirlineProperties properties;

    /**
     * 校验航班号
     * @param callSign
     * @return
     */
    private boolean checkCallSign(String callSign){
        try{
            if(properties.getCode2() != null && properties.getCode2().size() > 0){
                for(String code2 : properties.getCode2()){
                    if(callSign.contains(code2)){
                        return true;
                    }
                }
            }
            return false;
        }catch (Exception ex){
            Log.error(WebCrawlerService.class,"checkCallSign() error",ex);
            return false;
        }
    }

    private double formatAltitude(double value){
        return value*0.3048;
    }

    /**
     * 格式化经纬度
     * @param value
     * @param flag(0:纬度,1:经度)
     * @return
     */
    private String formatLatLon(double value,int flag) {
        if (flag == 0) {
            if (value > 0) {
                return "N" + value;
            } else {
                return "E" + Math.abs(value);
            }
        } else {
            if (value > 0) {
                return "E" + value;
            }else {
                return "W" + Math.abs(value);
            }
        }
    }

    /**
     * 拆解互联网资源
     * @param resource
     * @return
     */
    private List<ADSB> parseResource(String resource){
        try{
            List<ADSB> list = null;
            int intStart = resource.indexOf("(");
            int intEnd = resource.indexOf(")");
            if(intStart > 0 && intEnd > intStart){
                list = Lists.newArrayListWithExpectedSize(10000);
                String strJSON = resource.substring(intStart + 1,intEnd);
                JsonNode jn = new ObjectMapper().readTree(strJSON);
                for (Iterator<String> iterator = jn.fieldNames();iterator.hasNext();){
                    String fieldName = iterator.next();
                    if(jn.get(fieldName).isArray()){
                        String strICAO24 = jn.get(fieldName).get(0).asText();
                        String strAN = jn.get(fieldName).get(9).asText();
                        String strCallSign = jn.get(fieldName).get(13).asText();
                        if((!Strings.isNullOrEmpty(strICAO24) || !Strings.isNullOrEmpty(strAN)) && checkCallSign(strCallSign)){
                            ADSB adsb = new ADSB();
                            adsb.setIcao24(strICAO24.toLowerCase().trim());
                            adsb.setAn(Strings.isNullOrEmpty(strAN) ? adsb.getIcao24() : strAN);
                            adsb.setCallSign(Strings.nullToEmpty(strCallSign).trim());
                            double dLatitude = jn.get(fieldName).get(1).asDouble(1);
                            adsb.setLatitude(formatLatLon(dLatitude,0));
                            double dLontitude = jn.get(fieldName).get(2).asDouble(2);
                            adsb.setLongitude(formatLatLon(dLontitude,1));
                            double dAltitude = jn.get(fieldName).get(4).asDouble();
                            adsb.setAltitude(formatAltitude(dAltitude));
                            double dHeading = jn.get(fieldName).get(3).asDouble();
                            adsb.setHeading(dHeading);
                            double dVelocity = jn.get(fieldName).get(5).asDouble();
                            adsb.setVelocity(dVelocity);
                            list.add(adsb);
                        }
                    }
                }
            }
            return list;
        }catch (Exception ex){
            Log.error(WebCrawlerService.class,"parseResource() error",ex);
            return null;
        }
    }

    /**
     * 获取互联网资源
     * @param url
     * @return
     */
    private String getInternetResource(String url){
        InputStream is = null;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(6000 * 60 * 60)
                    .setConnectTimeout(6000 * 60 * 60)
                    .setConnectionRequestTimeout(6000 * 60 * 60).build();
            httpGet.setConfig(requestConfig);
            client = HttpClients.createDefault();
            response = client.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == 200){
                is = response.getEntity().getContent();
                return StringUtil.toString(is);
            }else{
                return null;
            }
        }catch (Exception ex){
            Log.error(WebCrawlerService.class,"getInternetResource() error",ex);
            return null;
        }finally {
            if(is != null){
                try{
                    if(is != null){
                        is.close();
                    }
                }catch (IOException ex){
                    Log.error(WebCrawlerService.class,"close IO error",ex);
                }
            }
            if(response != null){
                try {
                    response.close();
                } catch (IOException ex) {
                    Log.error(WebCrawlerService.class,"close IO error",ex);
                }
            }
            if(client != null){
                try {
                    client.close();
                } catch (IOException ex) {
                    Log.error(WebCrawlerService.class, "close IO error", ex);
                }
            }
        }
    }

    @Override
    public Optional<List<ADSB>> getADSB(String url) {
        try{
            String strResource = getInternetResource(url);
            if(!Strings.isNullOrEmpty(strResource)){
                List<ADSB> list = parseResource(strResource);
                return Optional.fromNullable(list);
            }else{
                return Optional.absent();
            }
        }catch (Exception ex){
            Log.error(WebCrawlerService.class,"getADSB() error",ex);
            return Optional.absent();
        }
    }
}
