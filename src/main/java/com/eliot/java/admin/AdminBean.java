/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eliot.java.admin;

import com.eliot.adminendpoint.client.AdminEndpoint;
import com.eliot.adminendpoint.client.ArrayOfDevice;
import com.eliot.adminendpoint.client.ArrayOfUser;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceRef;

/**
 *
 * @author DRobin
 */
@Named(value = "adminBean")
@ManagedBean
@SessionScoped
public class AdminBean implements Serializable {
    
    @WebServiceRef
    private AdminEndpoint adminWCF;
    
    private String chooseDevice;
    private String chooseUser;
    
    private String clientId;
    private String deviceId;
    
    private String email;
    
    private String password;
    
    /**
     * Creates a new instance of AdminBean
     */
    public AdminBean() {
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String addIdentity() {
        Boolean isLogged = adminWCF.getBasicHttpBindingIAdminEndpoint().loginAdminUser(email, password);

        if (isLogged) {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            Map<String, Object> sessionMap = externalContext.getSessionMap();
            sessionMap.put("user", email);
            
            return "admin";
        }
        
        return "index";
    }

    public String login() {
        Boolean isLogged = adminWCF.getBasicHttpBindingIAdminEndpoint().loginAdminUser(email, password);

        if (isLogged) {
            return "admin";
        }
        
        return "index";
    }
    
    public ArrayOfUser getUsers() {
        return adminWCF.getBasicHttpBindingIAdminEndpoint().getUsers();
    }
    
    public ArrayOfDevice getDevices() {
        return adminWCF.getBasicHttpBindingIAdminEndpoint().getDevices();
    }
    
    public void associateUserDevices() {
        adminWCF.getBasicHttpBindingIAdminEndpoint().associateUserDevices(chooseUser, chooseDevice);
    }

    /**
     * @return the chooseDevice
     */
    public String getChooseDevice() {
        return chooseDevice;
    }

    /**
     * @param chooseDevice the chooseDevice to set
     */
    public void setChooseDevice(String chooseDevice) {
        this.chooseDevice = chooseDevice;
    }

    /**
     * @return the chooseUser
     */
    public String getChooseUser() {
        return chooseUser;
    }

    /**
     * @param chooseUser the chooseUser to set
     */
    public void setChooseUser(String chooseUser) {
        this.chooseUser = chooseUser;
    }
    
}
