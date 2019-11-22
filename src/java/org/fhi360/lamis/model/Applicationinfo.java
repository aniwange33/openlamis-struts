/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author user10
 */
public class Applicationinfo implements Serializable {

    private Long id;
    private String databaseVersion;
    private String applicationVersion;
    private Date dateApplicationUpdate;
    private Date dateDatabaseUpdate;

    public Applicationinfo() {
    }

    public Applicationinfo(Long id, String databaseVersion, String applicationVersion, Date dateApplicationUpdate, Date dateDatabaseUpdate) {
        this.id = id;
        this.databaseVersion = databaseVersion;
        this.applicationVersion = applicationVersion;
        this.dateApplicationUpdate = dateApplicationUpdate;
        this.dateDatabaseUpdate = dateDatabaseUpdate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(String databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public Date getDateApplicationUpdate() {
        return dateApplicationUpdate;
    }

    public void setDateApplicationUpdate(Date dateApplicationUpdate) {
        this.dateApplicationUpdate = dateApplicationUpdate;
    }

    public Date getDateDatabaseUpdate() {
        return dateDatabaseUpdate;
    }

    public void setDateDatabaseUpdate(Date dateDatabaseUpdate) {
        this.dateDatabaseUpdate = dateDatabaseUpdate;
    } 

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Applicationinfo)) {
            return false;
        }
        Applicationinfo other = (Applicationinfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhi360.lamis.model.ApplicationInfo[ id=" + id + " ]";
    }
    
}
