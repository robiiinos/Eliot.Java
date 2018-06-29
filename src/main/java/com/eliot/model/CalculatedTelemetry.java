/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eliot.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author DRobin
 */
@Entity
@Table(name = "calculated_telemetry")
@XmlRootElement
public class CalculatedTelemetry implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "value")
    private String value;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "deviceId")
    private String deviceId;
    
    @JoinColumn(name = "id_device_type", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DeviceType idDeviceType;
    
    @JoinColumn(name = "id_telemetry_type", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TelemetryType idTelemetryType;

    public CalculatedTelemetry() {
    }

    public CalculatedTelemetry(Integer id) {
        this.id = id;
    }

    public CalculatedTelemetry(Integer id, String value, Date startDate, Date endDate, Date createdAt) {
        this.id = id;
        this.value = value;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public DeviceType getIdDeviceType() {
        return idDeviceType;
    }

    public void setIdDeviceType(DeviceType idDeviceType) {
        this.idDeviceType = idDeviceType;
    }

    public TelemetryType getIdTelemetryType() {
        return idTelemetryType;
    }

    public void setIdTelemetryType(TelemetryType idTelemetryType) {
        this.idTelemetryType = idTelemetryType;
    }

    @Override
    public String toString() {
        return "com.eliot.java.calculation.CalculatedTelemetry[ id=" + id + " ]";
    }
    
}
