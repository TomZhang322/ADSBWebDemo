package adsbWebCrawler.util;

import com.beamOps.utility.log.Log;

import java.io.InputStream;

/**
 * 字符串工具类
 */
public final class StringUtil {

    /**
     * 格式化流
     * @param is
     * @return
     */
    public static String toString(InputStream is){
        try{
            int length = -1;
            byte[] buffer = new byte[1024];
            StringBuilder sb = new StringBuilder();
            if(is != null){
                while ((length = is.read(buffer,0,buffer.length)) >=0){
                    String content = new String(buffer,0,length);
                    sb.append(content);
                }
            }
            return sb.toString();
        }catch (Exception ex){
            Log.error(StringUtil.class,"toString() error",ex);
            return null;
        }
    }
}
