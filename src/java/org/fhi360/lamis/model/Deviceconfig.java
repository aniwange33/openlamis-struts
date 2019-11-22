/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.model;

/**
 *
 * @author user10
 */
public class Deviceconfig  implements java.io.Serializable {
    private Long deviceconfigId;
    private Long facilityId;
    private String deviceId;
   private String username;
   private String password;

    public Deviceconfig() {
    }

    public Deviceconfig(Long deviceconfigId, Long facilityId, String deviceId) {
        this.deviceconfigId = deviceconfigId;
        this.facilityId = facilityId;
        this.deviceId = deviceId;
    }

    public Deviceconfig(Long deviceconfigId, Long facilityId, String deviceId, String username, String password) {
        this.deviceconfigId = deviceconfigId;
        this.facilityId = facilityId;
        this.deviceId = deviceId;
        this.username = username;
        this.password = password;
    }

    public Long getDeviceconfigId() {
        return deviceconfigId;
    }

    public void setDeviceconfigId(Long deviceconfigId) {
        this.deviceconfigId = deviceconfigId;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
}
