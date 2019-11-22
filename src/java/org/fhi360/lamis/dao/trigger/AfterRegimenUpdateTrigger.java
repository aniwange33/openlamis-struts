package org.fhi360.lamis.dao.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.h2.tools.TriggerAdapter;/**
 *
 * @author aalozie
 */
public class AfterRegimenUpdateTrigger extends TriggerAdapter  {
   private PreparedStatement statement;
   private String query;
    
    @Override
    public void fire(Connection conn, ResultSet oldRow, ResultSet newRow) throws SQLException {
        if(newRow != null && oldRow != null) { //This is an UPDATE
            String[] tables = {"patient", "clinic"};
            for(String table : tables) {
                query = "UPDATE [table] SET regimen = ? WHERE regimen = ?";
                query.replace("[table]", table);
                statement = conn.prepareStatement(query);         
                statement.setString(1, newRow.getString("description"));
                statement.setString(2, oldRow.getString("description"));
                statement.execute();            
            }
        } 
    }
}
