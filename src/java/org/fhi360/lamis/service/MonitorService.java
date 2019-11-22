/**
 *
 * @author user1
 */
package org.fhi360.lamis.service;

import java.util.Date;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class MonitorService {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public static void logEntity(String entityId, String tableName, int operationId) {
        String query = "INSERT INTO monitor (facility_id, entity_id, table_name, "
                + "operation_id, user_id, time_stamp) VALUES(?, ?, ?, ?, ?, ?)";
        transactionTemplate.execute(ts -> {
            jdbcTemplate.update(query, ServletActionContext.getRequest().getSession().getAttribute("facilityId"),
                    entityId, tableName, operationId,
                    ServletActionContext.getRequest().getSession().getAttribute("userId"),
                    new Date());
            return null;
        });

    }

}
