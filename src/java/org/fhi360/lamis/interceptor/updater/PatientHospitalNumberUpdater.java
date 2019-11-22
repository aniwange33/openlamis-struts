/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor.updater;

import java.util.Date;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class PatientHospitalNumberUpdater {

    private String query;

    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public void changeHospitalNum(String hospitalNum, String newHospitalNum, long facilityId) {
        try {
            
            query = "UPDATE patient SET hospital_num = ?, time_stamp = ? WHERE facility_id = ? AND hospital_num = ?";
            transactionTemplate.execute((ts) -> {
                jdbcTemplate.update(query, newHospitalNum,
                        new Date(), facilityId, hospitalNum);
                return null; 
            });

        } catch (Exception exception) {

        }
    }

}
