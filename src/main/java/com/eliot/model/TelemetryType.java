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
@Table(name = "telemetry_type")
@XmlRootElement
public class TelemetryType implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idTelemetryType")
    private Collection<CalculatedTelemetry> calculatedTelemetryCollection;

    public TelemetryType() {
    }

    public TelemetryType(Integer id) {
        this.id = id;
    }

    public TelemetryType(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "com.eliot.java.calculation.TelemetryType[ id=" + id + " ]";
    }
    
}
