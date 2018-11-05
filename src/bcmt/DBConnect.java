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
 * @author jfi872
 */
public class DBConnect {
    
    Connection conn;
    Statement stmt;
    public ResultSet result;
    
    String connectionString = "jdbc:mysql://localhost:3306/ctags";
    String userID = "root";
    String password = "";
    String query = "SELECT * FROM `changes` ";
    
    
    public void connect(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(connectionString, userID, password);
            
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
            
            while(result.next()){
                System.out.println(result.getString(1));
            }
            
        }catch(Exception e){
            System.out.println ("error. method name = connect." + e);
        }
    }
    
    
    public void disconnect(){
        try{
            conn.close();
        }
        catch (Exception e){
            System.out.println ("error. method name = disconnect.");
        }
    }
}
