/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcmt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Judith
 */

class SingleChange{
    String revision="-1", startline="-1", endline="-1", filepath="-1", changetype="-1";        
}

public class DBConnect {
    
    Connection conn;
    Statement stmt;
    public ResultSet result;
    String query = "SELECT * FROM `changes` ";
    
    // Need to make variable
    String connectionString = "jdbc:mysql://localhost:3306/" + InputParameters.systemName; // Brlcad
    //String connectionString = "jdbc:mysql://localhost:3306/ctags"; // Ctags
    String userID = "root";
    String password = "";   
    
    public void connect(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(connectionString, userID, password);
            
            
            
            
        }catch(Exception e){
            System.out.println ("error.method name = connect." + e);
        }
    }
    
    
    public void disconnect(){
        try{
            conn.close();
        }
        catch (Exception e){
            System.out.println ("error.method name = disconnect.");
        }
    }
    
    public SingleChange[] getChangedRevisions(){
        
        SingleChange[] changes = new SingleChange[50000];
        int i = 0;
        
        try{
            connect();
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
                                   
            while (result.next()){
                changes[i] = new SingleChange();
                changes[i].revision = result.getString("revision");
                changes[i].filepath = result.getString("filepath");
                changes[i].startline = result.getString("startline");
                changes[i].endline = result.getString("endline");
                changes[i].changetype = result.getString("changetype");
                i++;
            };
            /*
            for(int j=0; changes[j] != null; j++){
                System.out.println("Revision [" + j + "]= " + changes[j].revision);
            }
            */
            disconnect();

        }catch(Exception e){
            System.out.println ("error.getChangedRevisions." + e);
        }
        return changes;    
    }
}
