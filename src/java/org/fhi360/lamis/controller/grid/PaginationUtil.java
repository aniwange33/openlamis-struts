/**
 *
 * @author aalozie
 */

package org.fhi360.lamis.controller.grid;

import java.util.HashMap;
import java.util.Map;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class PaginationUtil {
    private String query;
   private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    
    public Map<String, Object> paginateGrid(int page, int rows, String table) {
        long facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        
        //calculate the number of rows for the query. We need this for paging the result
        query = "SELECT COUNT(*) AS count FROM [table] WHERE facility_id = " + facilityId;
        int totalrecords = getCount(query.replace("[table]", table));
        
        // calculate the total pages for the query
        int totalpages = 0;
        System.out.println("Pages is: "+Math.ceil(totalrecords / rows));
        if (totalrecords > 0) {
            if(totalrecords > rows){
                if(totalrecords % rows == 0)
                    totalpages = (int) Math.ceil(totalrecords / rows);
                else if(totalrecords % rows > 0)
                    totalpages = (int) Math.ceil(totalrecords / rows) + 1;
            }
            else{
                totalpages = 1;
            }
        }
        //totalpages = (int) Math.ceil(totalrecords / rows);

        // if for some reasons the requested page is greater than the total set the requested page to total page 
        if (page > totalpages) page = totalpages;

        // calculate the starting position of the rows to be retrieved
        int start = rows * page - rows;

        // if for some reasons start position is negative set it to 0. Typical case is that the user type 0 for the requested page 
        if (start < 0) start = 0;

        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("start", start);
        map.put("totalpages", totalpages);
        map.put("totalrecords", totalrecords);
        return map;
    }
	
    private int getCount(String query) {
       int count[]  = {0};
       try {
           jdbcTemplate.query(query, resultSet -> {
            if(resultSet.next()) {
                count[0] = resultSet.getInt("count");
            }
            return null;
           });
        }
        catch (Exception exception) {
           
        }
        return count[0];
    }        
}
