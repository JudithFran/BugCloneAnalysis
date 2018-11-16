/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author jfi872
 */



class CodeFragment{
    int revision = -1;
    String filepath = "";
    int startline = -1, endline = -1;
    String changetype = "-1";

    String [] lines = new String[1000];
    
    
    public void getFragment(){
        
        String abs_filepath = "D:/ManiBhaiBackup/systems/ctags/repository/version-" + revision + "/"+ filepath;
        try{
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (abs_filepath)));
            String str = "";
            
            int line = 0;
            int i =0;
            while ((str = br.readLine ()) != null)
            {
                line++;
                if (line > endline) {break;}
                if (line >= startline && line <= endline)
                {
                    lines[i] = str.trim();
                    //System.out.println(lines[i]);
                    i++;
                }
            }
        }catch(Exception e){
            System.out.println("error.getFragment." + e);
        }
    
    }
    
    public void showFragment (){
        String abs_filepath = "D:/ManiBhaiBackup/systems/ctags/repository/version-" + revision + "/"+ filepath;
        try{
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (abs_filepath)));
            String str = "";
            
            System.out.println ("\n" + revision + ": " + filepath + ", " + startline + " - " + endline + ", " + changetype);
            System.out.println ("---------------------------------------------------");
            int line = 0;
            int i =0;
            while ((str = br.readLine ()) != null)
            {
                line++;
                if (line > endline) {break;}
                if (line >= startline && line <= endline)
                {
                    lines[i] = str.trim();
                    i++;
                    System.out.println (str);
                }
            }
            System.out.println ("---------------------------------------------------");
        }catch (Exception e){
            System.out.println ("error.showFragment." + e);
        }
    }
}

public class BugReplicationMicro {

    DBConnect db = new DBConnect();
    CompareChanges cc = new CompareChanges();
    
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
    
    
    public SingleChange[] getChangedBugFixCommits(){
        
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
        
        /*---------------------------------------- Preprocessing bugFixCommits Start ---------------------------------------------------------------*/
        String[] bugFixCommitsReverse = new String[10000];
        int i = 0;
        for(int j = bugFixCommits.length-1; j>=0; j--){
            bugFixCommitsReverse[i] = bugFixCommits[j];
            //System.out.println("Bug Fix Revision [" + i + "] in Reverse = " + bugFixCommitsReverse[i] + " Where j = " + j);
            i++;    
        }
        int len = i;
        
        //Changing x to x-1 for revision numbers
        for(i = 0; i < len; i++){
            bugFixCommitsReverse[i] = Integer.toString(Integer.parseInt(bugFixCommitsReverse[i].trim())-1);
            //System.out.println("Bug Fix Revision [" + i + "] decreased value by 1 = " + bugFixCommitsReverse[i]);
        }
        /*---------------------------------------- Preprocessing bugFixCommits End ---------------------------------------------------------------*/
        
        
        //Matching bug-fix commits with changed revisions
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
       
        /*-------------------------------------------------------------------------- small sample test start -------------------------------------------------------------------
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
        -------------------------------------------------------------------------- small sample test end ----------------------------------------------------------------------*/
              
        }catch(Exception e){
            System.out.println ("error.getChangedBugFixCommits." + e);
        }
        return changedBugFixCommits;
    }
    
    public void isClonePair() throws ParserConfigurationException, SAXException, IOException{
        try{
        CodeFragment cf1 = new CodeFragment();
        CodeFragment cf2 = new CodeFragment();
        
        SingleChange[] changedBugFixCommits = new SingleChange[10000];
        
        changedBugFixCommits = getChangedBugFixCommits();
        /*
        for(int i = 0; changedBugFixCommits[i] != null; i++)
            System.out.println("Revision [" + i + "] in changedBugFixCommits in isClonePair()= " + changedBugFixCommits[i].revision);
        */
        
        cf1.revision = Integer.parseInt(changedBugFixCommits[0].revision);
        cf1.startline = Integer.parseInt(changedBugFixCommits[0].startline);
        cf1.endline = Integer.parseInt(changedBugFixCommits[0].endline);
        cf1.filepath = changedBugFixCommits[0].filepath;
        cf1.changetype = changedBugFixCommits[0].changetype;
        cf1.getFragment();
        cf1.showFragment();
        
        
        cf2 = getInstanceInNextRevision(cf1);
        
        cf2.getFragment();
        cf2.showFragment();
        
        /*---------------------------------------XML file parsing started here-------------------------------------------------*/
        
        //File microfXmlFile = new File("carolversion-1700_blocks-blind-clones-0.11.xml"); //All Type
        File regularfXmlFile = new File("D:/ManiBhaiBackup/systems/ctags/repository/version-15_blocks-blind-clones/version-15_blocks-blind-clones-0.3.xml"); //All Type
        
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        //Document docMicro = dBuilder.parse(microfXmlFile); 
        
        Document docRegular = dBuilder.parse(regularfXmlFile);
        
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        
        //docMicro.getDocumentElement().normalize();
        
        docRegular.getDocumentElement().normalize();
        
        //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        
        //NodeList nListMicro = docMicro.getElementsByTagName("clone");
        
        NodeList nListRegular = docRegular.getElementsByTagName("class");
        
        
        int count = 0;
        /*
        for (int i = 0; i < nListMicro.getLength(); i++) {

            Node nNodeMicro = nListMicro.item(i);
            
            ///System.out.println("\nCurrent Element in Micro : " + nNodeMicro.getNodeName());
            
            if (nNodeMicro.getNodeType() == Node.ELEMENT_NODE) {

			Element eElementMicro = (Element) nNodeMicro;

			//System.out.println("File Name : " + eElement.getAttribute("file"));
                        
                        Element cElementMicro =  (Element) eElementMicro.getElementsByTagName("source").item(0);
                        String microCloneFragment1 = cElementMicro.getAttribute("file");
                        //System.out.println("\nFirst File in Micro : " + microCloneFragment1);
                        
                        Element ccElementMicro =  (Element) eElementMicro.getElementsByTagName("source").item(1);
                        String microCloneFragment2 = ccElementMicro.getAttribute("file");
                        //System.out.println("Second File in Micro : " + microCloneFragment2);
                        
                        //Compare each micro-clone pair with the regular clone pair
        */
                        
                        for (int i = 0; i < nListRegular.getLength(); i++) {
                        
                            Node nNodeRegular = nListRegular.item(i);
                            
                            ///System.out.println("\nCurrent Element in Regular : " + nNodeRegular.getNodeName());
                            
                            if (nNodeRegular.getNodeType() == Node.ELEMENT_NODE) {

                                Element eElementRegular = (Element) nNodeRegular;
                                
                                int j = 0;
                                //while(eElementRegular.getElementsByTagName("source")){
                                Element cElementRegular =  (Element) eElementRegular.getElementsByTagName("source");
                                
                                //Element cElementRegular =  (Element) eElementRegular.getElementsByTagName("source").item(0);
                                
                                
                                String str1 = cElementRegular.getAttribute("file");
                                if(str1.contains("version-")){
                                    str1 = str1.replaceAll(".ifdefed", "");
                                    
                                    String[] filePath = str1.split("version-\\d*\\/");
                                    str1 = filePath[1];
                                    
                                    System.out.println("Working str1 = " + str1);
                                }
                                
                                String regularCloneFragment1 = str1;
                                
                                //System.out.println("\nFirst File in Regular : " + regularCloneFragment1);
                        
                                Element ccElementRegular =  (Element) eElementRegular.getElementsByTagName("source").item(1);
                                String str2 = ccElementRegular.getAttribute("file");
                                if(str2.contains("version-")){
                                    str2 = str2.replaceAll(".ifdefed", "");
                                    
                                    String[] filePath = str2.split("version-\\d*\\/");
                                    str2 = filePath[1];
                                    
                                    System.out.println("Working str2 = " + str2);
                                }
                                
                                String regularCloneFragment2 = str2;
                                //System.out.println("Second File in Regular : " + regularCloneFragment2);
                                                              
                                //if (microCloneFragment1.equals(regularCloneFragment1) && microCloneFragment2.equals(regularCloneFragment2)){
                                if (cf1.filepath.equals(regularCloneFragment1) && (cf2.filepath.equals(regularCloneFragment2))){
                                    System.out.println("\n\n*************************************************************Flag ********************** Found***************************************************************** " + count + "\n");
                                    count++;
                                }
                                
                                //}
                            }   
                        }
  
        System.out.println("Count = " + count);
        }catch(Exception e){
            System.out.println("error in method isClonePair." + e);
            e.printStackTrace();
        }
        
    }
    
    public CodeFragment getInstanceInNextRevision(CodeFragment cf){
        CodeFragment instance = new CodeFragment ();
        
        int crevision = cf.revision;
        int nrevision = crevision+1;
        
        int nstartline = 999999999;
        int nendline = -1;
        
        int changed = 0;
        
        String cfilepath = "D:/ManiBhaiBackup/systems/ctags/repository/version-" + crevision + "/"+ cf.filepath;
        String nfilepath = "D:/ManiBhaiBackup/systems/ctags/repository/version-" + nrevision + "/"+ cf.filepath;
        
        File file = new File (nfilepath);
        if (!file.exists()) { return null; }
        
        
        String [][] filecompare = cc.compareFiles(cfilepath, nfilepath);
                
        for (int i =0;filecompare[i][0] != null;i++)
        {
            String ln = filecompare[i][0].trim();
            if (ln.length() == 0) {continue;}
            int line = Integer.parseInt(ln);
            if (line > cf.endline) {break;}
            if (line >= cf.startline && line <= cf.endline)
            {
                String nln = filecompare[i][2].trim();
                if (nln.trim().length() > 0)
                {
                    int nline = Integer.parseInt (nln);
                    if (nstartline > nline){ nstartline = nline; }
                    if (nendline < nline) { nendline = nline; }                
                }
                if (!filecompare[i][1].trim().equals (filecompare[i][3].trim()))
                {
                    if (filecompare[i][1].trim().length() > 0 || filecompare[i][3].trim().length() > 0)
                    {
                        changed = 1;
                    }
                }
            }
        }
        
        if (nendline == -1)
        {
            return null;
        }
        
        instance.revision = nrevision;
        instance.filepath = cf.filepath;
        instance.startline = nstartline;
        instance.endline = nendline;
        instance.changetype = cf.changetype;
        
        return instance;
    }
}
