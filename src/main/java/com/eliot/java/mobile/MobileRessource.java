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
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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
    
    //@Inject
    //private JMSContext context;

    //@Resource(lookup = "jms/commandQueue")
    //private Queue commandQueue;

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
        Boolean isCreated = dataWCF.getBasicHttpBindingIDataEndpoint().addUser(clientId);

        // Check if entity has been persisted; responding with 201 if yes
        if (isCreated) {
            resp = Response.status(Response.Status.CREATED).build();
            return resp;
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

        return dataWCF.getBasicHttpBindingIDataEndpoint().getTelemetries(deviceId, DeviceType.fromValue(deviceType), xmlGregCal, xmlGregCal2);
    }

    @POST
    @Path("sendCommand")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendCommand(String content) {
        
        // Get the data send with the POST request and parse it into a JSON
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(content).getAsJsonObject();
        
        // create new command;
        Command command = new Command();
        command.setDeviceId(object.get("deviceId").getAsString());
        command.setMessage(object.get("message").getAsString());
        /*
        try {
        
            //obtention d'une instance JAXBContext associée au type Payment annoté avec JAX-B
            JAXBContext jaxbContext = JAXBContext.newInstance(Command.class);
            
            //création d'un Marshaller pour transfomer l'objet Java en flux XML
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            StringWriter writer = new StringWriter();
        
            //transformation de l'objet en flux XML stocké dans un Writer
            jaxbMarshaller.marshal(command, writer);
            String xmlMessage = writer.toString();
        
            //affichage du XML dans la console de sortie
            System.out.println(xmlMessage);

            //encapsulation du paiement au format XML dans un objet javax.jms.TextMessage
            TextMessage msg = context.createTextMessage(xmlMessage);
        
            //envoi du message dans la queue paymentQueue
            context.createProducer().send(commandQueue, msg);
        
        } catch (JAXBException ex) {
            System.err.println(ex);
        }
        */
    }

    @GET
    @Path("calculatedMetrics")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CalculatedTelemetry> calculatedMetrics(
            @QueryParam("deviceId") String deviceId,
            @QueryParam("deviceType") String deviceType) {
        
        return calculated.findById(deviceId, DeviceType.fromValue(deviceType));
    }

    protected EntityManager getEntityManager() {
        return em;
    }

}
