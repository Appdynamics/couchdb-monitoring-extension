package com.appdynamics.extensions.couchdb.config;
import com.appdynamics.extensions.couchdb.Constants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author: {Vishaka Sekar} on {7/17/19}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MetricConverter {

    @XmlAttribute(name = Constants.STR)
    private String label;

    @XmlAttribute(name = Constants.VALUE)
    private String value;

    public String getLabel () {
        return label;
    }

    public void setLabel (String label) {
        this.label = label;
    }

    public String getValue () {
        return value;
    }

    public void setValue (String value) {
        this.value = value;
    }
}
