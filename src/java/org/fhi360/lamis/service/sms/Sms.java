/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.sms;

/**
 *
 * @author user1
 */
public class Sms {
    private String text;
    private String from;
    private Long originatorId;
    private String shortcode;
    private String phoneNo;
    private String message;

    public Sms() {
        
    }

    public Sms(String text, String from, long originatorId) {
       this.text = text;
       this.from = from;
       this.originatorId = originatorId;
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
    
    public Long getOriginatorId() {
        return this.originatorId;
    }
    
    public void setOriginatorId(Long originatorId) {
        this.originatorId = originatorId;
    }

    /**
     * @return the shortcode
     */
    public String getShortcode() {
        return shortcode;
    }

    /**
     * @param shortcode the shortcode to set
     */
    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    /**
     * @return the phoneNo
     */
    public String getPhoneNo() {
        return phoneNo;
    }

    /**
     * @param phoneNo the phoneNo to set
     */
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
