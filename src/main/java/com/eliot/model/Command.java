/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eliot.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author DRobin
 */
@XmlRootElement
public class Command implements Serializable {
    
    private String deviceId;
    
    private String message;
    
    public Command() {
    }

    public Command(String deviceId, String message) {
        this.deviceId = deviceId;
        this.message = message;
    }

    /**
     * @return the deviceId
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * @param deviceId the deviceId to set
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return the command
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the command to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "com.eliot.java.calculation.Command[ deviceId=" + getDeviceId() + " ]";
    }
    
}
