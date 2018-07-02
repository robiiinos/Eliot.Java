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

/**
 *
 * @author DRobin
 */
@Stateless
public class TelemetryTypeDAO {

    @PersistenceContext(unitName = "lgPU")
    private EntityManager em;
    
    public void create(TelemetryType telemetryType) {
        em.persist(telemetryType);
    }
    
    public void edit(TelemetryType telemetryType) {
        em.merge(telemetryType);
    }

    public void remove(TelemetryType telemetryType) {
        em.remove(em.merge(telemetryType));
    }
    
    public Boolean contains(TelemetryType telemetryType) {
        return em.contains(telemetryType);
    }

    public TelemetryType find(Object id) {
        return em.find(TelemetryType.class, id);
    }

    public List<TelemetryType> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(TelemetryType.class));
        return em.createQuery(cq).getResultList();
    }

    public List<TelemetryType> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(TelemetryType.class));
        javax.persistence.Query q = em.createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }
    
}
