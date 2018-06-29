/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eliot.java.mobile;

import com.eliot.dataendpoint.client.ArrayOfDevice;
import com.eliot.dataendpoint.client.ArrayOfTelemetry;
import com.eliot.dataendpoint.client.DeviceType;
import com.eliot.dataendpoint.client.DataEndpoint;
import com.eliot.model.CalculatedTelemetry;
import com.eliot.model.CalculatedTelemetryDAO;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceRef;

/**
 * REST Web Service
 *
 * @author DRobin
 */
@RequestScoped
@Path("mobile")
public class MobileRessource {
    
    @EJB
    private CalculatedTelemetryDAO calculated;

    private Response resp;

    @WebServiceRef
    private DataEndpoint dataWCF;

    @PersistenceContext(unitName = "lgPU")
    private EntityManager em;

    @POST
    @Path("addUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(String content) {
        
        // Get the data send with the POST request
        StringReader reader = new StringReader(content);

        // Create a Reader and read then send data 
        try (JsonReader jreader = Json.createReader(reader)) {
            JsonObject infos = jreader.readObject();

            String clientId = infos.getString("clientId");

            // Persist the entity in the DB
            Boolean isCreated = dataWCF.getBasicHttpBindingIDataEndpoint().addUser(clientId);

            // Check if entity has been persisted; responding with 201 if yes
            if (isCreated) {
                resp = Response.status(Response.Status.CREATED).build();
                return resp;
            }
        }
        
        // If no, inform the user that something went wrong / 409
        resp = Response.status(Response.Status.CONFLICT).build();
        return resp;
    }

    @GET
    @Path("userDevices")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayOfDevice userDeviceType(
            @QueryParam("clientId") String clientId,
            @QueryParam("deviceType") DeviceType deviceType) {

        return dataWCF.getBasicHttpBindingIDataEndpoint().getUserDevices(clientId, deviceType);
    }

    @GET
    @Path("deviceSimpleMetrics")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayOfTelemetry deviceSimpleMetrics(
            @QueryParam("deviceId") String deviceId,
            @QueryParam("deviceType") String deviceType,
            @QueryParam("startDate") String minDate,
            @QueryParam("endDate") String maxDate) throws ParseException, DatatypeConfigurationException {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = format.parse(minDate);
        Date date2 = format.parse(maxDate);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        XMLGregorianCalendar xmlGregCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        
        cal.setTime(date2);
        XMLGregorianCalendar xmlGregCal2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        System.out.println(xmlGregCal);
        System.out.println(xmlGregCal2);

        return dataWCF.getBasicHttpBindingIDataEndpoint().getTelemetries(deviceId, DeviceType.fromValue(deviceType), xmlGregCal, xmlGregCal2);
    }

    @POST
    @Path("sendCommand")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendCommand(String content) {

        StringReader reader = new StringReader(content);

        try (JsonReader jreader = Json.createReader(reader)) {
            JsonObject infos = jreader.readObject();

            String deviceId = infos.getString("deviceId");
            String command = infos.getString("command");

            // Créer la queue JMS
        }

    }

    @GET
    @Path("calculatedMetrics")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CalculatedTelemetry> calculatedMetrics(
            @QueryParam("deviceId") String deviceId) {
        
        return calculated.findById(deviceId);
    }

    protected EntityManager getEntityManager() {
        return em;
    }

}
