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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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
    
    // Store choosen variables by user
    private String clientId;
    private String deviceId;
    
    // Credentials for login
    private String email;
    private String password;
    
    /**
     * Creates a new instance of AdminBean
     */
    public AdminBean() {
    }
    
    public String login() {
        try {
            adminWCF.getBasicHttpBindingIAdminEndpoint().loginAdminUser(email, password);
            
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", email);
            
            return "admin";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage("Invalid credentials."));
            return "index";
        }
    }
    
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        
        FacesContext.getCurrentInstance().addMessage("logout", new FacesMessage("You are now logged out."));
        return "index";
    }
    
    public ArrayOfUser getUsers() {
        return adminWCF.getBasicHttpBindingIAdminEndpoint().getUsers();
    }
    
    public ArrayOfDevice getDevices() {
        return adminWCF.getBasicHttpBindingIAdminEndpoint().getDevices();
    }
    
    public void associateUserDevices() {
        try {
            adminWCF.getBasicHttpBindingIAdminEndpoint().associateUserDevices(getClientId(), getDeviceId());
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("associateForm:associateForm", new FacesMessage("The device" + getDeviceId() + " is already associated with the user " + getClientId() + "."));
        }
    }
    
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
