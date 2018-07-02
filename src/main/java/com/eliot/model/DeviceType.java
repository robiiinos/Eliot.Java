/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eliot.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author DRobin
 */
@Entity
@Table(name = "device_type")
@XmlRootElement
public class DeviceType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "type")
    private String type;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idDeviceType")
    private Collection<CalculatedTelemetry> calculatedTelemetryCollection;

    public DeviceType() {
    }

    public DeviceType(Integer id) {
        this.id = id;
    }

    public DeviceType(Integer id, String type) {
        this.id = id;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlTransient
    public Collection<CalculatedTelemetry> getCalculatedTelemetryCollection() {
        return calculatedTelemetryCollection;
    }

    public void setCalculatedTelemetryCollection(Collection<CalculatedTelemetry> calculatedTelemetryCollection) {
        this.calculatedTelemetryCollection = calculatedTelemetryCollection;
    }

    @Override
    public String toString() {
        return "com.eliot.java.calculation.DeviceType[ id=" + id + " ]";
    }
    
}
