/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eliot.java.calculation;

import com.eliot.dataendpoint.client.DeviceType;
import com.eliot.model.CalculatedTelemetry;
import com.eliot.model.CalculatedTelemetryDAO;
import com.eliot.model.DeviceTypeDAO;
import com.eliot.model.TelemetryType;
import com.eliot.model.TelemetryTypeDAO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author DRobin
 */
@RequestScoped
@Path("telemetry")
public class CalculatedTelemetryRessource {

    @EJB
    private CalculatedTelemetryDAO calculated;
    @EJB
    private DeviceTypeDAO device;
    @EJB
    private TelemetryTypeDAO telemetry;

    private Response resp;

    @PersistenceContext(unitName = "lgPU")
    private EntityManager em;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getlastEntry(@QueryParam("deviceType") String deviceType) {
        try {
            CalculatedTelemetry calculatedT =  (CalculatedTelemetry) calculated.findByDeviceType(DeviceType.fromValue(deviceType));
            
            resp = Response.status(Response.Status.OK).entity(calculatedT.getCreatedAt()).build();
            return resp;
        } catch(EJBException e) {
            resp = Response.status(Response.Status.NO_CONTENT).build();
            return resp;
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String content) throws ParseException {

        // Get the data send with the POST request and parse it into a JSON
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(content).getAsJsonObject();
        
        // Bind a new DateFormat to parse Universal Date
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        // Check if the JSON has the deviceId specified, if not => null
        String deviceId;
        deviceId = object.has("deviceId") ? object.get("deviceId").getAsString() : null;
        
        // Find the related entities with the given ID's
        com.eliot.model.DeviceType deviceType = device.find(object.get("deviceType").getAsInt());
        TelemetryType calculatedTelemetryType = telemetry.find(object.get("calculatedTelemetryType").getAsInt());

        // Create new entity and bind variabless
        CalculatedTelemetry entity = new CalculatedTelemetry();
        entity.setValue(object.get("value").getAsDouble());
        entity.setStartDate(formatter.parse(object.get("startDate").getAsString()));
        entity.setEndDate(formatter.parse(object.get("endDate").getAsString()));
        entity.setCreatedAt(new Date());
        entity.setDeviceId(deviceId);
        entity.setIdDeviceType(deviceType);
        entity.setIdTelemetryType(calculatedTelemetryType);

        // Persist the entity in the DB
        calculated.create(entity);

        // Check if entity has been persisted; responding with 201 if yes
        if (em.contains(entity)) {
            resp = Response.status(Response.Status.CREATED).build();
            return resp;
        }
        
        // If no, inform the user that something went wrong / 409
        resp = Response.status(Response.Status.CREATED).build();
        return resp;
    }

    protected EntityManager getEntityManager() {
        return em;
    }

}
