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
public class DeviceTypeDAO {

    @PersistenceContext(unitName = "lgPU")
    private EntityManager em;
    
    public void create(DeviceType deviceType) {
        em.persist(deviceType);
    }
    
    public void edit(DeviceType deviceType) {
        em.merge(deviceType);
    }

    public void remove(DeviceType deviceType) {
        em.remove(em.merge(deviceType));
    }
    
    public Boolean contains(DeviceType deviceType) {
        return em.contains(deviceType);
    }

    public DeviceType find(Object id) {
        return em.find(DeviceType.class, id);
    }

    public List<DeviceType> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(DeviceType.class));
        return em.createQuery(cq).getResultList();
    }

    public List<DeviceType> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(DeviceType.class));
        javax.persistence.Query q = em.createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }
    
}
