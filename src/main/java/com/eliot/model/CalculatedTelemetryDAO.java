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
import java.util.Date;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

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
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(CalculatedTelemetry.class));
        return em.createQuery(cq).getResultList();
    }
    
    public List<CalculatedTelemetry> findByTelemetryType(TelemetryType telemetryType, Date startDate) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        Root<CalculatedTelemetry> root = cq.from(CalculatedTelemetry.class);
        
        ParameterExpression<Date> parameterDate = builder.parameter(Date.class);
        Predicate startDatePredicate = builder.greaterThanOrEqualTo(root.get("startDate").as(Date.class), parameterDate);
        cq.where(startDatePredicate);
        
        ParameterExpression<Integer> parameterInt = builder.parameter(Integer.class);
        Predicate temetryTypePredicate = builder.equal(root.join("idTelemetryType").get("id"), parameterInt);
        cq.where(temetryTypePredicate);
        
        javax.persistence.Query q = em.createQuery(cq);
        q.setParameter(parameterInt, telemetryType.getId());
        q.setParameter(parameterDate, startDate);
        return q.getResultList();
    }
    
    public Object findByDeviceType(DeviceType deviceType) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        Root<CalculatedTelemetry> root = cq.from(CalculatedTelemetry.class);
        cq.where(builder.equal(root.join("idDeviceType").get("id"), deviceType.ordinal() + 1));
        cq.orderBy(builder.desc(root.get("createdAt")));
        javax.persistence.Query q = em.createQuery(cq);
        q.setMaxResults(1);
        return q.getSingleResult();
    }
    
    public List<CalculatedTelemetry> findRange(int[] range) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(CalculatedTelemetry.class));
        javax.persistence.Query q = em.createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }
    
}
