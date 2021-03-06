package com.appdynamics.extensions.couchdb.config;

import com.appdynamics.extensions.couchdb.util.Constants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author: {Vishaka Sekar} on {7/17/19}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Stat {

    @XmlElement(name = "stat")
    public Stat[] stats;
    @XmlAttribute
    private String type;
    @XmlElement(name = Constants.METRIC)
    private Metric[] metric;

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
}

