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
import com.eliot.model.Command;
import com.eliot.model.TelemetryType;
import com.eliot.model.TelemetryTypeDAO;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
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
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceRef;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javax.ws.rs.DefaultValue;

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
    
    @EJB
    private TelemetryTypeDAO telemetry;

    private Response resp;

    @WebServiceRef
    private DataEndpoint dataWCF;
    
    // Queue credentials / informations for RabbitMQ
    private final static String QUEUE_NAME = "commandQueue";
    private final static String QUEUE_HOST = "40.89.135.29";
    private final static Integer QUEUE_PORT = 5672;
    private final static String QUEUE_USERNAME = "admin";
    private final static String QUEUE_PASSWORD = "atlantis";

    @PersistenceContext(unitName = "lgPU")
    private EntityManager em;

    @POST
    @Path("addUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(String content) {

        // Get the data send with the POST request and parse it into a JSON
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(content).getAsJsonObject();

        String clientId = object.get("clientId").getAsString();

        // Persist the entity in the DB
        try {
            dataWCF.getBasicHttpBindingIDataEndpoint().addUser(clientId);
            
            resp = Response.status(Response.Status.CREATED).build();
            return resp;
        } catch (Exception e) {
            resp = Response.status(Response.Status.CONFLICT).build();
            return resp;
        }
    }

    @GET
    @Path("userDevices")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayOfDevice userDeviceType(
            @QueryParam("clientId") String clientId,
            @DefaultValue("none") @QueryParam("deviceType") String deviceType) {
        
        DeviceType device;
        device = "none".equals(deviceType) ? null : DeviceType.fromValue(deviceType);
        
        return dataWCF.getBasicHttpBindingIDataEndpoint().getUserDevices(clientId, device);
    }

    @GET
    @Path("deviceSimpleMetrics")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayOfTelemetry deviceSimpleMetrics(
            @QueryParam("deviceId") String deviceId,
            @DefaultValue("none") @QueryParam("deviceType") String deviceType,
            @QueryParam("startDate") String minDate,
            @QueryParam("endDate") String maxDate) throws ParseException, DatatypeConfigurationException {

        DeviceType device;
        device = "none".equals(deviceType) ? null : DeviceType.fromValue(deviceType);
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = format.parse(minDate);
        Date date2 = format.parse(maxDate);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        XMLGregorianCalendar xmlGregCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        
        cal.setTime(date2);
        XMLGregorianCalendar xmlGregCal2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        return dataWCF.getBasicHttpBindingIDataEndpoint().getTelemetries(deviceId, device, xmlGregCal, xmlGregCal2);
    }

    @POST
    @Path("sendCommand")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendCommand(String content) throws IOException, TimeoutException {
        
        // Get the data send with the POST request and parse it into a JSON
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(content).getAsJsonObject();
        
        // create new command;
        Command command = new Command();
        command.setDeviceId(object.get("deviceId").getAsString());
        command.setMessage(object.get("message").getAsString());
        
        Gson gson = new Gson();
        String message = gson.toJson(command);

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(QUEUE_HOST);
            factory.setPort(QUEUE_PORT);
            factory.setUsername(QUEUE_USERNAME);
            factory.setPassword(QUEUE_PASSWORD);
            
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

            channel.close();
            connection.close();

            resp = Response.status(Response.Status.OK).build();
            return resp;
        } catch (IOException | TimeoutException e) {
            resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
            return resp;
        }
    }
    
    @GET
    @Path("calculatedTypeMetrics")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TelemetryType> calculatedMetrics() {
        return telemetry.findAll();
    }

    @GET
    @Path("calculatedMetrics")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CalculatedTelemetry> calculatedMetrics(
            @QueryParam("calculatedType") Integer telemetryType,
            @QueryParam("startDate") String startDate) throws ParseException {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date dateS = format.parse(startDate);
        
        return calculated.findByTelemetryType(telemetry.find(telemetryType), dateS);
    }

    protected EntityManager getEntityManager() {
        return em;
    }

}
