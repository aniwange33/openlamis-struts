package org.fhi360.lamis.dao.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.h2.tools.TriggerAdapter;/**
 *
 * @author aalozie
 */
public class AfterStateUpdateTrigger extends TriggerAdapter  {
   private PreparedStatement statement;
   private String query;
   
    @Override
    public void fire(Connection conn, ResultSet oldRow, ResultSet newRow) throws SQLException {
        if(newRow != null && oldRow != null) { //This is an UPDATE
            query = "UPDATE patient SET state = ? WHERE state = ?";
            statement = conn.prepareStatement(query);         
            statement.setString(1, newRow.getString("name"));
            statement.setString(2, oldRow.getString("name"));
            statement.execute();            
        } 
    }
}
