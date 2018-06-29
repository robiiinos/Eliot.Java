/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eliot.java.calculation;

import com.eliot.model.CalculatedTelemetry;
import com.eliot.model.CalculatedTelemetryDAO;
import com.eliot.model.DeviceType;
import com.eliot.model.DeviceTypeDAO;
import com.eliot.model.TelemetryType;
import com.eliot.model.TelemetryTypeDAO;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String content) throws ParseException {

        // Get the data send with the POST request
        StringReader reader = new StringReader(content);

        // Create a Reader and read then send data 
        try (JsonReader jreader = Json.createReader(reader)) {
            JsonObject infos = jreader.readObject();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            // Find the related entities
            DeviceType deviceType = device.find(infos.getInt("deviceType"));
            TelemetryType calculatedTelemetryType = telemetry.find(infos.getInt("calculatedTelemetryType"));
            
            String deviceId = null;
            if(infos.containsKey("deviceId")) {
                deviceId = infos.getString("deviceId");
            }

            // Create new entity and bind variabless
            CalculatedTelemetry entity = new CalculatedTelemetry();
            entity.setValue(infos.getString("value"));
            entity.setStartDate(formatter.parse(infos.getString("startDate")));
            entity.setEndDate(formatter.parse(infos.getString("endDate")));
            entity.setCreatedAt(new Date());
            entity.setDeviceId(deviceId);
            entity.setIdDeviceType(deviceType);
            entity.setIdTelemetryType(calculatedTelemetryType);

            // Persist the entity in the DB
            calculated.create(entity);

            // Check if entity has been persisted; responding with 200 if yes
            if (calculated.contains(entity)) {
                resp = Response.status(Response.Status.CREATED).build();
                return resp;
            }

            // If no, inform the user that something went wrong / 409
            resp = Response.status(Response.Status.CREATED).build();
            return resp;
        } catch (ParseException e) {
            // Parse exception occured on Date; responding with INTERNAL_SERVER_ERROR / 500
            resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            return resp;
        }
    }

    protected EntityManager getEntityManager() {
        return em;
    }

}
