/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller;

/**
 *
 * @author user1
 */
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import org.fhi360.lamis.service.sms.Sms;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.sms.SmsConversationService;
import org.fhi360.lamis.service.sms.SmsMessageHandlerDelegator;
import org.fhi360.lamis.service.sms.SmsMessageSender;

public class SmsAction extends ActionSupport implements ModelDriven {
    private String text;
    private String from;
    private Sms sms;

    @Override
    public Object getModel() {
        sms = new Sms();
        return sms;        
    }    
    
    public String readSms() {
        sms.setOriginatorId(0L);
        SmsMessageHandlerDelegator smsMessageHandlerDelegator = SmsMessageHandlerDelegator.getInstance();
        smsMessageHandlerDelegator.delegate(sms);
                
        ServletActionContext.getResponse().setStatus(204);
        return SUCCESS;
    }
    
    public String sendSms() {
        long userId = (Long) ServletActionContext.getRequest().getSession().getAttribute("userId");
        sms.setOriginatorId(userId);
        new SmsConversationService().sendSms(sms);
        return SUCCESS;
    }
    
    public String unreadFlagUpdate() {
        new SmsConversationService().unreadFlagUpdate(ServletActionContext.getRequest().getParameter("phone"));
        return SUCCESS;
    }
    
    public String sendTestMessage() {
        String message = ServletActionContext.getRequest().getParameter("message");
        String phone = ServletActionContext.getRequest().getParameter("phone");
        new SmsMessageSender().send(message, phone);
        return SUCCESS;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * @return the sms
     */
    public Sms getSms() {
        return sms;
    }

    /**
     * @param sms the sms to set
     */
    public void setSms(Sms sms) {
        this.sms = sms;
    }
}
