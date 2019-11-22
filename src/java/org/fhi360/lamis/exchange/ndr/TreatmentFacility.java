/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.ndr;

/**
 * @author user1
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.fhi360.lamis.exchange.ndr.schema.FacilityType;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.JDBCUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public class TreatmentFacility {

    public TreatmentFacility() {
    }

    public static FacilityType getFacility(long facilityId) {
        FacilityType facility = new FacilityType();
        String query = "SELECT * FROM facility WHERE facility_id = " + facilityId;
        ContextProvider.getBean(JdbcTemplate.class).query(query, rs -> {
            facility.setFacilityName(rs.getString("name"));
            //facility.setFacilityID(Long.toString(rs.getLong("facility_id")));
            facility.setFacilityID(rs.getString("datim_id").replace("\r", "").replace("\n", "").trim());
            facility.setFacilityTypeCode("FAC");
        });
        return facility;
    }

}
