package com.appdynamics.extensions.couchdb.config;
import com.appdynamics.extensions.couchdb.Constants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author: {Vishaka Sekar} on {7/17/19}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Stat {

    @XmlAttribute
    private String type;

    @XmlElement(name = Constants.METRIC)
    private Metric[] metric;

    @XmlAttribute(name = Constants.URL)
    private String url;

    @XmlElement(name = "stat")
    public Stat[] stats;

    public Stat[] getStats() {
        return stats;
    }

    public void setStats(Stat[] stats) {
        this.stats = stats;
    }

    public Metric[] getMetric() {
        return metric;
    }

    public void setMetric(Metric[] metric) {
        this.metric = metric;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

