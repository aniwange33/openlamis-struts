/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.model;

public class Dhiscodeset implements java.io.Serializable {
    private long dhiscodesetId;
    private String codeSetNm;
    private long lamisId;
    private String dhisId;
    private String description;

    public Dhiscodeset() {
    }

    public Dhiscodeset(long dhiscodesetId, String codeSetNm, long lamisId, String dhisId, String description) {
        this.dhiscodesetId = dhiscodesetId;
        this.codeSetNm = codeSetNm;
        this.lamisId = lamisId;
        this.dhisId = dhisId;
        this.description = description;
    }

    public long getDhiscodesetId() {
        return dhiscodesetId;
    }

    public void setDhiscodesetId(long dhiscodesetId) {
        this.dhiscodesetId = dhiscodesetId;
    }

    public String getCodeSetNm() {
        return codeSetNm;
    }

    public void setCodeSetNm(String codeSetNm) {
        this.codeSetNm = codeSetNm;
    }

    public long getLamisId() {
        return lamisId;
    }

    public void setLamisId(long lamisId) {
        this.lamisId = lamisId;
    }

    public String getDhisId() {
        return dhisId;
    }

    public void setDhisId(String dhisId) {
        this.dhisId = dhisId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    
}
