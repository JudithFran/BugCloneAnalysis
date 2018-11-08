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
        
        SingleChange[] changedBugFixCommits = new SingleChange[10000];
        
        try{
        
        String str = getBugFixCommits();
        String[] bugFixCommits = new String[10000];
        
        SingleChange[] changes = db.getChangedRevisions();
       
        /*
        for(int j=0; changes[j] != null; j++){
            System.out.println("Revision [" + j + "]= " + changes[j].revision);
        }
        */
        
        bugFixCommits = str.split("  ");
        
        
        String[] bugFixCommitsReverse = new String[10000];
        int i = 0;
        for(int j = bugFixCommits.length-1; j>=0; j--){
            bugFixCommitsReverse[i] = bugFixCommits[j];
            //System.out.println("Bug Fix Revision [" + i + "] in Reverse = " + bugFixCommitsReverse[i] + " Where j = " + j);
            i++;    
        }
        int len = i;
        
        int k = 0;
        for(i = 0; i < len; i++){
            for(int j = 0; changes[j] != null; j++){
                if (bugFixCommitsReverse[i].equals(changes[j].revision)){
                    changedBugFixCommits[k] = changes[j];
                    System.out.println("Revision [" + k + "] in changedBugFixCommits = " + changedBugFixCommits[k].revision);
                    k++;
                }
            }
        }
        
        /*-------------------------------------------------------------------------- small sample test ----------------------------------------------------------------------
        String[] bugFixCommitsTest = new String[100];
        for(i = 0; i < 5; i++){
            bugFixCommitsTest[i] = bugFixCommitsReverse[i];
            System.out.println("Bug Fix Revision [" + i + "] First Five Values = " + bugFixCommitsTest[i]);
        }
        int len2 = i;
        System.out.println("Length of bugFixCommitsTest = " + len2);
        
        System.out.println();
        SingleChange[] changesTest = new SingleChange[100];
        for(i = 0; i < 10; i++){
            changesTest[i] = changes[i];
            System.out.println("Revision [" + i + "] First Ten Values (revision)= " + changesTest[i].revision);
            //System.out.println("Revision [" + i + "] First Ten Values (filepath)= " + changesTest[i].filepath);
            //System.out.println("Revision [" + i + "] First Ten Values (startline)= " + changesTest[i].startline);
            //System.out.println("Revision [" + i + "] First Ten Values (endline)= " + changesTest[i].endline);
            //System.out.println("Revision [" + i + "] First Ten Values (changetype)= " + changesTest[i].changetype);
            //System.out.println("\n");
        }
        
        System.out.println();
        SingleChange[] changedBugFixCommitsTest = new SingleChange[100];
        int k = 0;
        
        int len = i;
        System.out.println("Length of changesTest = " + len);
        
        for(i = 0; i < len2; i++){
            for(int j = 0; j < len; j++){
                if (bugFixCommitsTest[i].equals(changesTest[j].revision)){
                    changedBugFixCommitsTest[k] = changesTest[j];
                    System.out.println("Revision [" + k + "] in changedBugFixCommitsTest = " + changedBugFixCommitsTest[k].revision);
                    k++;
                }
            }
        }
        -------------------------------------------------------------------------- small sample test ------------------------------------------------------------------------*/
              
        }catch(Exception e){
            System.out.println ("error.getChangedBugFixCommits." + e);
        }
    }
    
    
}
