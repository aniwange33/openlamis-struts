/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.dao.trigger;

import java.sql.PreparedStatement;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class InitialTriggers {

    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    
    private PreparedStatement preparedStatement;
    
    public void init() {        
        String[] queries = {"CREATE TRIGGER IF NOT EXISTS after_lga_update AFTER UPDATE ON lga FOR EACH ROW CALL \"com.agama.lamis.dao.trigger.AfterLgaUpdateTrigger\"",
            "CREATE TRIGGER IF NOT EXISTS after_state_update AFTER UPDATE ON state FOR EACH ROW CALL \"com.agama.lamis.dao.trigger.AfterStateUpdateTrigger\"",
            "CREATE TRIGGER IF NOT EXISTS after_regimen_update AFTER UPDATE ON regimen FOR EACH ROW CALL \"com.agama.lamis.dao.trigger.AfterRegimenUpdateTrigger\"",
            "CREATE TRIGGER IF NOT EXISTS after_regimentype_update AFTER UPDATE ON regimentype FOR EACH ROW CALL \"com.agama.lamis.dao.trigger.AfterRegimentypeUpdateTrigger\"",
            "CREATE TRIGGER IF NOT EXISTS after_status_update AFTER UPDATE ON status FOR EACH ROW CALL \"com.agama.lamis.dao.trigger.AfterStatusUpdateTrigger\""};        
        try {
            
            for (String query : queries) {
                transactionTemplate.execute((ts) -> {
                    jdbcTemplate.execute(query);
                    return null; //To change body of generated lambdas, choose Tools | Templates.
                });
            }
        } catch (Exception exception) {
            
        }
        
    }    
}
