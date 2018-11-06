/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcmt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author jfi872
 */
public class BugReplicationMicro {

    public String getBugFixCommits(){
        String bugFixCommits = "";
        try{
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream ("commitlog.txt")));
            
            String str = "";            
            String prevString = "";
            
            int commit = 0;
            while ((str = br.readLine())!= null)
            {
                if (str.trim().length() == 0) {continue;}
                
                if (prevString.contains ("--------------------------------"))
                {
                    //this is the starting of a commit report.
                    //we need to know the commit number.
                    String str1 = str.trim().split("[ ]+")[0].trim();
                    str1 = str1.substring(1);
                    commit = Integer.parseInt (str1);
                    //System.out.println (commit);
                }
                else
                {
                    //according to the study of Mockus
                    if (str.toLowerCase().contains ("bug") || str.toLowerCase().contains("fix") || str.toLowerCase().contains ("fixup") || str.toLowerCase().contains ("error") || str.toLowerCase().contains ("fail"))
                    //if (str.contains ("bug") || str.contains("fix") || str.contains ("fixup") || str.contains ("error") || str.contains ("fail"))
                    {
                        if (!bugFixCommits.contains (" "+commit+" "))
                        {
                            bugFixCommits += " " + commit + " ";
                        }
                    }
                }
                prevString = str;
            }
            br.close();
            System.out.println ("Revisions that were created because of a bug fix = " + bugFixCommits);
        
        }catch(Exception e){
            System.out.println ("error.getBugFixCommits." + e);
        }
        
        
        return bugFixCommits;
    }
    
    DBConnect db = new DBConnect();
    
    
    public void getChangedBugFixCommits(){
        try{
            
        String bugFixCommits = getBugFixCommits();
        String [] str = new String [10000];
        
        str = bugFixCommits.split("  ");
        
        for(int i=0; i<str.length; i++){
            System.out.println("Revision = " + str[i]);    
        }
        
        db.getChangedRevisions();
        
        }catch(Exception e){
            System.out.println ("error.getChangedBugFixCommits." + e);
        }
    }
    
    
}
