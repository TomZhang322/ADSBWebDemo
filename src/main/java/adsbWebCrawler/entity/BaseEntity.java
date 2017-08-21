package adsbWebCrawler.entity;

import java.io.Serializable;

/**
 * 基类实体
 */
public abstract class BaseEntity implements Serializable{

    /**
     * 转换为JSON
     * @return
     */
    public abstract String toJSON();

    /**
     * 转换为XML
     * @return
     */
    public abstract String toXML();
}
