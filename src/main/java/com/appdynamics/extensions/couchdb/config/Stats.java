package com.appdynamics.extensions.couchdb.config;


import com.appdynamics.extensions.couchdb.Constants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author: {Vishaka Sekar} on {7/17/19}
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Stats {

    @XmlElement(name = Constants.STAT)
    private Stat[] stat;

    public Stat[] getStat () {
        return stat;
    }

    public void setStat (Stat[] stat) {
        this.stat = stat;
    }
}
