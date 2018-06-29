/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eliot.model;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.eliot.dataendpoint.client.DeviceType;

/**
 *
 * @author DRobin
 */
@Stateless
public class CalculatedTelemetryDAO {

    @PersistenceContext(unitName = "lgPU")
    private EntityManager em;
    
    public void create(CalculatedTelemetry calculatedTelemetry) {
        em.persist(calculatedTelemetry);
    }
    
    public void edit(CalculatedTelemetry calculatedTelemetry) {
        em.merge(calculatedTelemetry);
    }

    public void remove(CalculatedTelemetry calculatedTelemetry) {
        em.remove(em.merge(calculatedTelemetry));
    }
    
    public Boolean contains(CalculatedTelemetry calculatedTelemetry) {
        return em.contains(calculatedTelemetry);
    }

    public CalculatedTelemetry find(Object id) {
        return em.find(CalculatedTelemetry.class, id);
    }

    public List<CalculatedTelemetry> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(CalculatedTelemetry.class));
        return em.createQuery(cq).getResultList();
    }
    
    public List<CalculatedTelemetry> findById(String deviceId, DeviceType deviceType) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<CalculatedTelemetry> query = builder.createQuery(CalculatedTelemetry.class);
        Root<CalculatedTelemetry> root = query.from(CalculatedTelemetry.class);
        query.select(root).where(builder.equal(root.get("deviceId"), deviceId));
        //query.select(root).where(builder.equal(root.get("idDeviceType"), (deviceType.ordinal() + 1)));
        return em.createQuery(query).getResultList();
    }
    
    public List<CalculatedTelemetry> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(CalculatedTelemetry.class));
        javax.persistence.Query q = em.createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }
    
}
