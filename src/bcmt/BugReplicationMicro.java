/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcmt;


import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import scala.actors.threadpool.Arrays;


/**
 *
 * @author Judith
 */
class CodeFragment {

    int revision = -1;
    String filepath = "";
    int startline = -1, endline = -1;
    String changetype = "-1";

    String[] lines = new String[10000];

    public void getFragment() {
        
        String abs_filepath = "C:/ManiBhaiBackup/systems/ctags/repository/version-" + revision + "/" + filepath; // Have to make it variable
        //String abs_filepath = "D:/ManiBhaiBackup/systems/ctags/repository/version-" + revision + "/" + filepath; // Have to make it variable
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(abs_filepath)));
            String str = "";

            int line = 0;
            int i = 0;
            while ((str = br.readLine()) != null) {
                line++;
                if (line > endline) {
                    break;
                }
                if (line >= startline && line <= endline) {
                    lines[i] = str.trim();
                    //System.out.println(lines[i]);
                    i++;
                }
            }
        } catch (Exception e) {
            System.out.println("error.getFragment." + e);
        }

    }

    public void showFragment() {
        
        String abs_filepath = "C:/ManiBhaiBackup/systems/ctags/repository/version-" + revision + "/" + filepath; // Have to make it variable
        //String abs_filepath = "D:/ManiBhaiBackup/systems/ctags/repository/version-" + revision + "/" + filepath; // Have to make it variable
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(abs_filepath)));
            String str = "";

            System.out.println("\n" + revision + ": " + filepath + ", " + startline + " - " + endline + ", " + changetype);
            System.out.println("---------------------------------------------------");
            int line = 0;
            int i = 0;
            while ((str = br.readLine()) != null) {
                line++;
                if (line > endline) {
                    break;
                }
                if (line >= startline && line <= endline) {
                    lines[i] = str.trim();
                    i++;
                    System.out.println(str);
                }
            }
            System.out.println("---------------------------------------------------");
        } catch (Exception e) {
            System.out.println("error.showFragment." + e);
        }
    }
}

public class BugReplicationMicro {
    public void bugReplicationM(){
        //CodeFragment[][] cfXmlFileMicro = new CodeFragment[50000][2];
        ArrayList<CodeFragment> cfXmlFileMicroAL = new ArrayList<>();
        try{
            SingleChange[] changedBugFixCommits = new SingleChange[10000];
            //changedBugFixCommits = getChangedBugFixCommits();
            
            CodeFragment cf = new CodeFragment();
            CodeFragment[] cloneFragmentPair = new CodeFragment[2];
            //cfXmlFileMicroAL = xmlFileParseMicro(10, "x");
            
            int numReplicatedBugFixCommitsMicro = 0;
            ArrayList<CodeFragment> bugRep = new ArrayList<>();
            
            for (int m = 0; changedBugFixCommits[m] != null; m++) {
                cf.revision = Integer.parseInt(changedBugFixCommits[m].revision);
                cf.startline = Integer.parseInt(changedBugFixCommits[m].startline);
                cf.endline = Integer.parseInt(changedBugFixCommits[m].endline);
                cf.filepath = changedBugFixCommits[m].filepath;
                cf.changetype = changedBugFixCommits[m].changetype;

                //System.out.println("\nCode Fragment (CF): ");
                //cf.getFragment();
                //cf.showFragment();
                
                //cfXmlFileMicroAL = xmlFileParseMicro(cf.revision, cf.changetype);
                
                cloneFragmentPair = isClonePairMicro(cf);
                               
                if(cloneFragmentPair[0] != null && cloneFragmentPair[1] != null){
                    CodeFragment[] cloneFragmentPairINR = new CodeFragment[2];
                    
                    cloneFragmentPairINR[0] = getInstanceInNextRevision(cloneFragmentPair[0]);
                    cloneFragmentPairINR[1] = getInstanceInNextRevision(cloneFragmentPair[1]);
                    
                    if(cloneFragmentPairINR[0] != null && cloneFragmentPairINR[1] != null){
                        if(isClonePairBinaryMicro(cloneFragmentPairINR[0], cloneFragmentPairINR[1]) == 1){
                            numReplicatedBugFixCommitsMicro++;
                            System.out.println("////////////////////////////////////////////////////////////////////////////Micro Replicated Bug Fixing Change Found////////////////////////////////////////////////////////////////////////////.");
                            System.out.println("numReplicatedBugFixCommits for Micro Clones = " + numReplicatedBugFixCommitsMicro);
                            
                            bugRep.add(cloneFragmentPair[0]);
                            bugRep.add(cloneFragmentPair[1]);
                        }
                    }   
                }          
            }
            System.out.println("\nTotal replicated bugs in micro code clone = " + bugRep.size());
            
            for(int j=0; j<bugRep.size(); j++){
                System.out.println("\nThis is the array of replicated bugs in micro-clones: j = " + j);
                bugRep.get(j).getFragment();
                bugRep.get(j).showFragment();
            }
            
            for(int i = 0; i < bugRep.size(); i++){
                for(int j = i+1; j < bugRep.size(); j++){
                    
                    if(bugRep.get(i).revision == bugRep.get(j).revision && bugRep.get(i).filepath.equals(bugRep.get(j).filepath) 
                        && bugRep.get(i).startline == bugRep.get(j).startline && bugRep.get(i).endline == bugRep.get(j).endline){
                        bugRep.remove(i);
                        j--;                          
                    }
                }
            }
            
            for(int j=0; j<bugRep.size(); j++){
                System.out.println("\nThis is the array of replicated bugs after removing duplicate objects in micro-clones: j = " + j);
                bugRep.get(j).getFragment();
                bugRep.get(j).showFragment();
            }
 
            System.out.println("Total Replicated Bug-Fixing Commits in Micro-clones = " + numReplicatedBugFixCommitsMicro);
            
        }catch(Exception e){
            System.out.println("error in BugReplicationM()." + e);
            e.printStackTrace();
        }
    }
    
    //This is the main method. This method is calling all other methods.
    public void bugReplicationR(){
        try{
            //SingleChange[] changedBugFixCommits = new SingleChange[10000];
            SingleChange[][] changedBugFixCommits2D = new SingleChange[10000][10000];
            //changedBugFixCommits = getChangedBugFixCommits();
            changedBugFixCommits2D = getChangedBugFixCommits();
            
            CodeFragment[] cf = new CodeFragment[10000];
            CodeFragment[] cloneFragmentPair = new CodeFragment[2];
            CodeFragment[][] cfp = new CodeFragment[10000][2];
            int numReplicatedBugFixCommits = 0;
            ArrayList<CodeFragment> bugRep = new ArrayList<>();
        
           
            /*
            for (int i = 0; i < cfXmlFileMicro.length; i++) {
                for (int j = 0; j < cfXmlFileMicro.length; j++) {
                    if(cfXmlFileMicro[i][j] != null){
                        System.out.println("bugReplication: cfXmlFileMicro["+i+"]["+j+"].startline = " + cfXmlFileMicro[i][j].startline + " cfXmlFileMicro["+i+"]["+j+"].endline = " 
                                + cfXmlFileMicro[i][j].endline + " cfXmlFileMicro["+i+"]["+j+"].revision = " + cfXmlFileMicro[i][j].revision);
                    }
                }
            }
            */
            
            for (int m = 0; m<changedBugFixCommits2D.length; m++) {
                Arrays.fill(cf, null);
                for(int n = 0; n<changedBugFixCommits2D.length; n++){
                    if(changedBugFixCommits2D[m][n] != null){
                        cf[n] = new CodeFragment();
                        cf[n].revision = Integer.parseInt(changedBugFixCommits2D[m][n].revision);
                        cf[n].startline = Integer.parseInt(changedBugFixCommits2D[m][n].startline);
                        cf[n].endline = Integer.parseInt(changedBugFixCommits2D[m][n].endline);
                        cf[n].filepath = changedBugFixCommits2D[m][n].filepath;
                        cf[n].changetype = changedBugFixCommits2D[m][n].changetype;

                        System.out.println("\nCode Fragment (CF) for m = " + m + " and n = " + n);
                        cf[n].getFragment();
                        cf[n].showFragment();
                    }
                    //cfp = isClonePair(cf);
                }
                    //cloneFragmentPair = isClonePair(cf);
                cfp = isClonePair(cf);
                
                if(cfp != null){
                for (int i = 0; i < cfp.length; i++) {
                    for (int j = 0; j < 2; j++) {
                        if(cfp[i][j] != null){
                            System.out.println("bugReplicationR: cfp["+i+"]["+j+"].revision = " + cfp[i][j].revision + " cfp["+i+"]["+j+"].filepath = " + cfp[i][j].filepath
                                + " cfp["+i+"]["+j+"].startline = " + cfp[i][j].startline + " cfXmlFileMicro["+i+"]["+j+"].endline = " 
                                    + cfp[i][j].endline + " cfp["+i+"]["+j+"].changetype = " + cfp[i][j].changetype);
                        
                        }
                    }
                }
                }
                
                    if(cloneFragmentPair[0] != null && cloneFragmentPair[1] != null){
                        CodeFragment[] cloneFragmentPairINR = new CodeFragment[2];

                        cloneFragmentPairINR[0] = getInstanceInNextRevision(cloneFragmentPair[0]);
                        cloneFragmentPairINR[1] = getInstanceInNextRevision(cloneFragmentPair[1]);

                        if(cloneFragmentPairINR[0] != null && cloneFragmentPairINR[1] != null){
                            if(isClonePairBinary(cloneFragmentPairINR[0], cloneFragmentPairINR[1]) == 1){
                                numReplicatedBugFixCommits++;
                                System.out.println("////////////////////////////////////////////////////////////////////////////Replicated Bug Fixing Change Found////////////////////////////////////////////////////////////////////////////.");
                                System.out.println("numReplicatedBugFixCommits for Regular Clones = " + numReplicatedBugFixCommits);
                                
                                bugRep.add(cloneFragmentPair[0]);
                                bugRep.add(cloneFragmentPair[1]);
                            }
                        }
                    }
                //} // for(int n = 0; changedBugFixCommits2D[m][n] != null; n++){
            } // for (int m = 0; changedBugFixCommits[m] != null; m++)
            
            System.out.println("\nTotal replicated bugs in regular code clone = " + bugRep.size());
            
            for(int j=0; j<bugRep.size(); j++){
                System.out.println("\nThis is the array of replicated bugs: j = " + j);
                bugRep.get(j).getFragment();
                bugRep.get(j).showFragment();
            }
            
            /*
            for(int i = 0; i < bugRep.size(); i++){
                for(int j = i+1; j < bugRep.size(); j++){
                    if(bugRep.get(i).equals(bugRep.get(j))){
                        bugRep.remove(i);
                        j--;    
                    }
                }
            }
            */
            
            for(int i = 0; i < bugRep.size(); i++){
                for(int j = i+1; j < bugRep.size(); j++){
                    
                    if(bugRep.get(i).revision == bugRep.get(j).revision && bugRep.get(i).filepath.equals(bugRep.get(j).filepath) 
                        && bugRep.get(i).startline == bugRep.get(j).startline && bugRep.get(i).endline == bugRep.get(j).endline){
                        bugRep.remove(i);
                        j--;                          
                    }
                }
            }
            
            for(int j=0; j<bugRep.size(); j++){
                System.out.println("\nThis is the array of replicated bugs after removing duplicate objects: j = " + j);
                bugRep.get(j).getFragment();
                bugRep.get(j).showFragment();
            }
 
            System.out.println("Total Replicated Bug-Fixing Commits = " + numReplicatedBugFixCommits);
            
            
        }catch(Exception e){
            System.out.println("error in BugReplicationR()." + e);
            e.printStackTrace();
        }
    }

    DBConnect db = new DBConnect();
    CompareChanges cc = new CompareChanges();

    public String getBugFixCommits() {
        String bugFixCommits = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("commitlog.txt"))); // Have to make it variable

            String str = "";
            String prevString = "";

            int commit = 0;
            while ((str = br.readLine()) != null) {
                if (str.trim().length() == 0) {
                    continue;
                }

                if (prevString.contains("--------------------------------")) {
                    //this is the starting of a commit report.
                    //we need to know the commit number.
                    String str1 = str.trim().split("[ ]+")[0].trim();
                    str1 = str1.substring(1);
                    commit = Integer.parseInt(str1);
                    //System.out.println (commit);
                } else {
                    //according to the study of Mockus
                    if (str.toLowerCase().contains("bug") || str.toLowerCase().contains("fix") || str.toLowerCase().contains("fixup") || str.toLowerCase().contains("error") || str.toLowerCase().contains("fail")) //if (str.contains ("bug") || str.contains("fix") || str.contains ("fixup") || str.contains ("error") || str.contains ("fail"))
                    {
                        if (!bugFixCommits.contains(" " + commit + " ")) {
                            bugFixCommits += " " + commit + " ";
                        }
                    }
                }
                prevString = str;
            }
            br.close();
            //System.out.println ("Revisions that were created because of a bug fix = " + bugFixCommits);

        } catch (Exception e) {
            System.out.println("error.getBugFixCommits." + e);
        }

        return bugFixCommits;
    }

    public SingleChange[][] getChangedBugFixCommits() {

        SingleChange[] changedBugFixCommits = new SingleChange[10000];
        SingleChange[][] changedBugFixCommits2D = new SingleChange[10000][10000];

        try {

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
            for (int j = bugFixCommits.length - 1; j >= 0; j--) {
                bugFixCommitsReverse[i] = bugFixCommits[j];
                //System.out.println("Bug Fix Revision [" + i + "] in Reverse = " + bugFixCommitsReverse[i] + " Where j = " + j);
                i++;
            }
            int len = i;

            //Changing x to x-1 for revision numbers
            for (i = 0; i < len; i++) {
                bugFixCommitsReverse[i] = Integer.toString(Integer.parseInt(bugFixCommitsReverse[i].trim()) - 1);
                //System.out.println("Bug Fix Revision [" + i + "] decreased value by 1 = " + bugFixCommitsReverse[i]);
            }
            /*---------------------------------------- Preprocessing bugFixCommits End ---------------------------------------------------------------*/

            //Matching bug-fix commits with changed revisions
            
            int k = 0;
            for (i = 0; i < len; i++) {
                for (int j = 0; changes[j] != null; j++) {
                    if (bugFixCommitsReverse[i].equals(changes[j].revision)) {
                        changedBugFixCommits[k] = changes[j];
                        //System.out.println("Revision [" + k + "] in changedBugFixCommits = " + changedBugFixCommits[k].revision);
                        k++;
                    }
                }
            }
            
            //Matching bug-fix commits with changed revisions and saving in 2D array
            int a = 0, b = 0;
            for (i = 0; i < len; i++) {
                for (int j = 0; changes[j] != null; j++) {
                    if (bugFixCommitsReverse[i].equals(changes[j].revision)) {
                        changedBugFixCommits2D[a][b] = changes[j];
                        if(changes[j+1] != null){ 
                            if(changes[j].revision.equals(changes[j+1].revision)){
                                b++;
                                changedBugFixCommits2D[a][b] = changes[j+1];
                            }
                            else
                                a++;
                        }
                    }
                }
                b = 0;
            }
            /*
            for(int j = 0; j<changedBugFixCommits2D.length; j++){
                for(k = 0; k<changedBugFixCommits2D.length; k++){
                    if(changedBugFixCommits2D[j][k] != null){
                        System.out.println("getChangedBugFixCommits: getChangedBugFixCommits["+j+"]["+k+"].revision = " + changedBugFixCommits2D[j][k].revision 
                            + " getChangedBugFixCommits["+j+"]["+k+"].filepath = " + changedBugFixCommits2D[j][k].filepath 
                                + " getChangedBugFixCommits["+j+"]["+k+"].startline = " + changedBugFixCommits2D[j][k].startline 
                                    + " getChangedBugFixCommits["+j+"]["+k+"].endline = " + changedBugFixCommits2D[j][k].endline
                                        + " getChangedBugFixCommits["+j+"]["+k+"].changetype = " + changedBugFixCommits2D[j][k].changetype);
                    }
                }
                System.out.println();
            }
            */
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
        } catch (Exception e) {
            System.out.println("error.getChangedBugFixCommits." + e);
        }
        //return changedBugFixCommits;
        return changedBugFixCommits2D;
    }
    public int isClonePairBinary(CodeFragment cf1, CodeFragment cf2){
        int pair = 0;
        try{
            CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];

            cfXmlFile = xmlFileParse(cf1.revision);

            for (int i = 0; i < cfXmlFile.length; i++) {
                for (int j = 0; j < cfXmlFile.length; j++) {

                    if (cfXmlFile[i][j] != null){
                        //System.out.println("cfXmlFile[" + i + "][" + j + "] = " + cfXmlFile[i][j].filepath);
                        if(cf1.filepath.equals(cfXmlFile[i][j].filepath)){
                            /*
                            System.out.println("cf1.startline = " + cf1.startline);
                            System.out.println("cfXmlFile["+i+"]["+j+"].startline = " +cfXmlFile[i][j].startline);
                            System.out.println("cf1.endline = " + cf1.endline);
                            System.out.println("cfXmlFile["+i+"]["+j+"].endline = " +cfXmlFile[i][j].endline);
                            */
                            if(((cf1.startline >= cfXmlFile[i][j].startline) && (cf1.endline <= cfXmlFile[i][j].endline))
                                    ||((cf1.startline <= cfXmlFile[i][j].startline) && (cf1.endline <= cfXmlFile[i][j].endline) && (cf1.endline >= cfXmlFile[i][j].startline)) 
                                        ||((cf1.startline >= cfXmlFile[i][j].startline) && (cf1.endline >= cfXmlFile[i][j].endline) && (cf1.startline <= cfXmlFile[i][j].endline))){
                                //System.out.println("cfXmlFile[" + i + "][" + j + "] filepath = " + cfXmlFile[i][j].filepath + " revision = " 
                                    //+ cfXmlFile[i][j].revision + " startline = " + cfXmlFile[i][j].startline + " endline = " + cfXmlFile[i][j].endline);
                                
                                if(cfXmlFile[i][j+1] != null && cf1.filepath.equals(cfXmlFile[i][j+1].filepath)){
                                    cf2.revision = cfXmlFile[i][j+1].revision;
                                    cf2.startline = cfXmlFile[i][j+1].startline;
                                    cf2.endline = cfXmlFile[i][j+1].endline;
                                    cf2.filepath = cfXmlFile[i][j+1].filepath;
                                    cf2.changetype = cfXmlFile[i][j+1].changetype;
                                    //System.out.println("************************************************************************************Clone Pair Found************************************************************************************");
                                    //System.out.println("\nCode Fragment 2 (CF2): ");
                                    //cf2.getFragment();
                                    //cf2.showFragment();
                                    pair = 1;   
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            System.out.println("error in method isClonePair." + e);
            e.printStackTrace();
        }
        return pair;
    }
    
    public int isClonePairBinaryMicro(CodeFragment cf1, CodeFragment cf2){
        int pair = 0;
        try{
            CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
            ArrayList<CodeFragment> cfXmlFileMicroAL = new ArrayList<>();

            cfXmlFileMicroAL = xmlFileParseMicro(cf1.revision);

            for (int i = 0; i < cfXmlFileMicroAL.size(); i++) {
                for (int j = 0; j < cfXmlFileMicroAL.size(); j++) {

                    if (cfXmlFileMicroAL != null){
                        //System.out.println("cfXmlFile[" + i + "][" + j + "] = " + cfXmlFile[i][j].filepath);
                        if(cf1.filepath.equals(cfXmlFileMicroAL.get(i).filepath)){
                            /*
                            System.out.println("cf1.startline = " + cf1.startline);
                            System.out.println("cfXmlFile["+i+"]["+j+"].startline = " +cfXmlFile[i][j].startline);
                            System.out.println("cf1.endline = " + cf1.endline);
                            System.out.println("cfXmlFile["+i+"]["+j+"].endline = " +cfXmlFile[i][j].endline);
                            */
                            if(((cf1.startline >= cfXmlFileMicroAL.get(i).startline) && (cf1.endline <= cfXmlFileMicroAL.get(i).endline))
                                    ||((cf1.startline <= cfXmlFileMicroAL.get(i).startline) && (cf1.endline <= cfXmlFileMicroAL.get(i).endline) && (cf1.endline >= cfXmlFileMicroAL.get(i).startline)) 
                                        ||((cf1.startline >= cfXmlFileMicroAL.get(i).startline) && (cf1.endline >= cfXmlFileMicroAL.get(i).endline) && (cf1.startline <= cfXmlFileMicroAL.get(i).endline))){
                                //System.out.println("cfXmlFile[" + i + "][" + j + "] filepath = " + cfXmlFile[i][j].filepath + " revision = " 
                                    //+ cfXmlFile[i][j].revision + " startline = " + cfXmlFile[i][j].startline + " endline = " + cfXmlFile[i][j].endline);
                                
                                //if(cfXmlFileMicroAL.get(i+1) != null && cf1.filepath.equals(cfXmlFileMicroAL.get(i+1).filepath)){
                                
                                if(i%2 == 0){
                                    if(cfXmlFileMicroAL.get(i+1) != null){    
                                        cf2.revision = cfXmlFileMicroAL.get(i+1).revision;
                                        cf2.startline = cfXmlFileMicroAL.get(i+1).startline;
                                        cf2.endline = cfXmlFileMicroAL.get(i+1).endline;
                                        cf2.filepath = cfXmlFileMicroAL.get(i+1).filepath;
                                        cf2.changetype = cfXmlFileMicroAL.get(i+1).changetype;
                                        //System.out.println("************************************************************************************Clone Pair Found************************************************************************************");
                                        //System.out.println("\nCode Fragment 2 (CF2): ");
                                        //cf2.getFragment();
                                        //cf2.showFragment();
                                        pair = 1;   
                                    }
                                }
                                else{
                                    if(cfXmlFileMicroAL.get(i-1) != null){    
                                        cf2.revision = cfXmlFileMicroAL.get(i-1).revision;
                                        cf2.startline = cfXmlFileMicroAL.get(i-1).startline;
                                        cf2.endline = cfXmlFileMicroAL.get(i-1).endline;
                                        cf2.filepath = cfXmlFileMicroAL.get(i-1).filepath;
                                        cf2.changetype = cfXmlFileMicroAL.get(i-1).changetype;
                                        //System.out.println("************************************************************************************Clone Pair Found************************************************************************************");
                                        //System.out.println("\nCode Fragment 2 (CF2): ");
                                        //cf2.getFragment();
                                        //cf2.showFragment();
                                        pair = 1;   
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            System.out.println("error in method isClonePair." + e);
            e.printStackTrace();
        }
        return pair;
    }
       
    int countCF1 = 0;
    
    public CodeFragment[][] isClonePair(CodeFragment[] cf){
        CodeFragment[][] cfp = new CodeFragment[10000][2];
        try {
            for(int n = 0; cf[n] != null; n++){
                System.out.println("isClonePair: cf["+n+"] = " + cf[n].revision + " cf["+n+"].filepath = " + cf[n].filepath 
                    + " cf.startline = " + cf[n].startline + " cf.endline = " + cf[n].endline + " cf["+n+"].changetype = " + cf[n].changetype);
           
            CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
            cfXmlFile = xmlFileParse(cf[n].revision);
            
            int m = 0;
            
            for (int i = 0; i < cfXmlFile.length; i++) {
                for (int j = 0; j < cfXmlFile.length; j++) {

                    if (cfXmlFile[i][j] != null){
                        //System.out.println("cfXmlFile[" + i + "][" + j + "] = " + cfXmlFile[i][j].filepath + " Start Line = " + cfXmlFile[i][j].startline 
                                        //+ " End Line = " + cfXmlFile[i][j].endline);
                        if(cf[n].filepath.equals(cfXmlFile[i][j].filepath)){
                            /*
                            System.out.println("cf.startline = " + cf[n].startline);
                            System.out.println("cfXmlFile["+i+"]["+j+"].startline = " +cfXmlFile[i][j].startline);
                            System.out.println("cf.endline = " + cf[n].endline);
                            System.out.println("cfXmlFile["+i+"]["+j+"].endline = " +cfXmlFile[i][j].endline);
                            */
                            
                            // -----------------------Checking for matches with changed bug-fix commits from DB with each line of xml file of a particular revision--------------------------
                            
                            if(((cf[n].startline >= cfXmlFile[i][j].startline) && (cf[n].endline <= cfXmlFile[i][j].endline))
                                ||((cf[n].startline <= cfXmlFile[i][j].startline) && (cf[n].endline <= cfXmlFile[i][j].endline) && (cf[n].endline >= cfXmlFile[i][j].startline)) 
                                ||((cf[n].startline >= cfXmlFile[i][j].startline) && (cf[n].endline >= cfXmlFile[i][j].endline) && (cf[n].startline <= cfXmlFile[i][j].endline))){
                                
                            // If match found check all element of changed bug-fix commit of a particular commit with all code fragments that resides in the same clone class in xml file.
                            // This is checking if the other code fragments of same clone class are also from changed bug-fix commit.
                            
                                for(int k = 0; cfXmlFile[i][k] != null; k++){
                                    for(int l = 0; cf[l] != null; l++){
                                        if(((cf[1].startline >= cfXmlFile[i][k].startline) && (cf[1].endline <= cfXmlFile[i][k].endline))
                                            ||((cf[1].startline <= cfXmlFile[i][k].startline) && (cf[1].endline <= cfXmlFile[i][k].endline) && (cf[1].endline >= cfXmlFile[i][k].startline)) 
                                            ||((cf[1].startline >= cfXmlFile[i][k].startline) && (cf[1].endline >= cfXmlFile[i][k].endline) && (cf[1].startline <= cfXmlFile[i][k].endline))){
                                        
                                        cfp[m][0] = cfXmlFile[i][j];
                                        cfp[m][1] = cfXmlFile[i][k];
                                        m++;
                                        }
                                    }
                                }
                                
                                //System.out.println("isClonePair: cfXmlFile[" + i + "][" + j + "] filepath = " + cfXmlFile[i][j].filepath + " revision = " 
                                    //+ cfXmlFile[i][j].revision + " startline = " + cfXmlFile[i][j].startline + " endline = " + cfXmlFile[i][j].endline);
                                /*
                                cfp[k] = new CodeFragment();
                                cfp[k] = cfXmlFile[i][j];
                                                                    
                                k++;
                                */    
                                /*    
                                if(cfXmlFile[i][j+1] != null && cf.filepath.equals(cfXmlFile[i][j+1].filepath)){
                                    cfp[0] = new CodeFragment();
                                    cfp[0].revision = cfXmlFile[i][j].revision;
                                    cfp[0].startline = cfXmlFile[i][j].startline;
                                    cfp[0].endline = cfXmlFile[i][j].endline;
                                    cfp[0].filepath = cfXmlFile[i][j].filepath;
                                    cfp[0].changetype = cfXmlFile[i][j].changetype;
                                    
                                    //System.out.println("cfp[0].startline = " + cfp[0].startline + " cfp[0].endline = " + cfp[0].endline + " cfp[0].revision = " + cfp[0].revision);
                                    
                                    cfp[1] = new CodeFragment();
                                    cfp[1].revision = cfXmlFile[i][j+1].revision;
                                    cfp[1].startline = cfXmlFile[i][j+1].startline;
                                    cfp[1].endline = cfXmlFile[i][j+1].endline;
                                    cfp[1].filepath = cfXmlFile[i][j+1].filepath;
                                    cfp[1].changetype = cfXmlFile[i][j+1].changetype;
                                    System.out.println("************************************************************************************Clone Pair Found************************************************************************************");
                                    System.out.println("\nCode Fragment Pair (CFP): ");
                                    cfp[1].getFragment();
                                    cfp[1].showFragment();
                                }
                                */
                            }
                        }
                    }
                }
            }
            }
            /*
            for(int i = 0; cfp[i] != null; i++){
                System.out.println("cfp["+i+"].revision = " + cfp[i].revision + " cfp["+i+"].filepath = " + cfp[i].filepath + " cfp["+i+"].startline = " + cfp[i].startline 
                        + " cfp["+i+"].endline = " + cfp[i].endline + " cfp["+i+"].changetype = " + cfp[i].changetype);
                countCF1++;
            }
            */
            System.out.println("countCF1 = " + countCF1);
            
        } catch (Exception e) {
            System.out.println("error in method isClonePair." + e);
            e.printStackTrace();
        }
        return cfp;
        //return null;
    }
    
    public CodeFragment[] isClonePairMicro(CodeFragment cf1){
        CodeFragment[] cfp = new CodeFragment[2];
        try {
            /*
            int lenChangedBugFixCommits = 0;
            for(int i = 0; changedBugFixCommits[i] != null; i++){
                System.out.println("Revision [" + i + "] in changedBugFixCommits in isClonePair()= " + changedBugFixCommits[i].revision);
                lenChangedBugFixCommits = i;
            }
            System.out.println("Length of the changedBugFixCommits = " + lenChangedBugFixCommits);
            */
            //CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
            ArrayList<CodeFragment> cfXmlFileMicroAL = new ArrayList<>();
            
            cfXmlFileMicroAL = xmlFileParseMicro(cf1.revision);

            for (int i = 0; i < cfXmlFileMicroAL.size(); i++) {
                //for (int j = 0; j < cfXmlFileMicroAL.size(); j++) {

                    if (cfXmlFileMicroAL != null){
                        //System.out.println("cfXmlFile[" + i + "][" + j + "] = " + cfXmlFile[i][j].filepath + " Start Line = " + cfXmlFile[i][j].startline 
                                        //+ " End Line = " + cfXmlFile[i][j].endline);
                        if(cf1.filepath.equals(cfXmlFileMicroAL.get(i).filepath)){
                            /*
                            System.out.println("cf1.startline = " + cf1.startline);
                            System.out.println("cfXmlFile["+i+"]["+j+"].startline = " +cfXmlFile[i][j].startline);
                            System.out.println("cf1.endline = " + cf1.endline);
                            System.out.println("cfXmlFile["+i+"]["+j+"].endline = " +cfXmlFile[i][j].endline);
                            */
                            if(((cf1.startline >= cfXmlFileMicroAL.get(i).startline) && (cf1.endline <= cfXmlFileMicroAL.get(i).endline))
                                    ||((cf1.startline <= cfXmlFileMicroAL.get(i).startline) && (cf1.endline <= cfXmlFileMicroAL.get(i).endline) && (cf1.endline >= cfXmlFileMicroAL.get(i).startline)) 
                                        ||((cf1.startline >= cfXmlFileMicroAL.get(i).startline) && (cf1.endline >= cfXmlFileMicroAL.get(i).endline) && (cf1.startline <= cfXmlFileMicroAL.get(i).endline))){
                                //System.out.println("isClonePair: cfXmlFile[" + i + "][" + j + "] filepath = " + cfXmlFile[i][j].filepath + " revision = " 
                                    //+ cfXmlFile[i][j].revision + " startline = " + cfXmlFile[i][j].startline + " endline = " + cfXmlFile[i][j].endline);
                                                              
                                //if(cfXmlFileMicroAL.get(i+1) != null && cf1.filepath.equals(cfXmlFileMicroAL.get(i).filepath)){
                                
                                if(i%2 == 0){
                                    if(cfXmlFileMicroAL.get(i+1) != null){
                                        cfp[0] = new CodeFragment();
                                        cfp[0].revision = cfXmlFileMicroAL.get(i).revision;
                                        cfp[0].startline = cfXmlFileMicroAL.get(i).startline;
                                        cfp[0].endline = cfXmlFileMicroAL.get(i).endline;
                                        cfp[0].filepath = cfXmlFileMicroAL.get(i).filepath;
                                        cfp[0].changetype = cfXmlFileMicroAL.get(i).changetype;
                                        
                                        //System.out.println("cfp[0].startline = " + cfp[0].startline + " cfp[0].endline = " + cfp[0].endline + " cfp[0].revision = " + cfp[0].revision);
                                        
                                        cfp[1] = new CodeFragment();
                                        cfp[1].revision = cfXmlFileMicroAL.get(i+1).revision;
                                        cfp[1].startline = cfXmlFileMicroAL.get(i+1).startline;
                                        cfp[1].endline = cfXmlFileMicroAL.get(i+1).endline;
                                        cfp[1].filepath = cfXmlFileMicroAL.get(i+1).filepath;
                                        cfp[1].changetype = cfXmlFileMicroAL.get(i+1).changetype;
                                        System.out.println("************************************************************************************Micro Clone Pair Found************************************************************************************");
                                        System.out.println("\nCode Fragment Pair (MCFP): ");
                                        cfp[1].getFragment();
                                        cfp[1].showFragment();
                                    }
                                }
                                else{
                                    if(cfXmlFileMicroAL.get(i-1) != null){
                                        cfp[0] = new CodeFragment();
                                        cfp[0].revision = cfXmlFileMicroAL.get(i-1).revision;
                                        cfp[0].startline = cfXmlFileMicroAL.get(i-1).startline;
                                        cfp[0].endline = cfXmlFileMicroAL.get(i-1).endline;
                                        cfp[0].filepath = cfXmlFileMicroAL.get(i-1).filepath;
                                        cfp[0].changetype = cfXmlFileMicroAL.get(i-1).changetype;
                                        
                                        //System.out.println("cfp[0].startline = " + cfp[0].startline + " cfp[0].endline = " + cfp[0].endline + " cfp[0].revision = " + cfp[0].revision);
                                        
                                        cfp[1] = new CodeFragment();
                                        cfp[1].revision = cfXmlFileMicroAL.get(i).revision;
                                        cfp[1].startline = cfXmlFileMicroAL.get(i).startline;
                                        cfp[1].endline = cfXmlFileMicroAL.get(i).endline;
                                        cfp[1].filepath = cfXmlFileMicroAL.get(i).filepath;
                                        cfp[1].changetype = cfXmlFileMicroAL.get(i).changetype;
                                        System.out.println("************************************************************************************Micro Clone Pair Found************************************************************************************");
                                        System.out.println("\nCode Fragment Pair (MCFP): ");
                                        cfp[1].getFragment();
                                        cfp[1].showFragment();
                                    }
                                }
                            }
                        }
                    }
                //} end of for loop j
            }
        } catch (Exception e) {
            System.out.println("error in method isClonePair." + e);
            e.printStackTrace();
        }
        return cfp;
    }
    
    int classID;
    
    public CodeFragment[][] xmlFileParse(int rev) {
        CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
        try {
            //classID = 0;
            // Have to make it variable
            
            File regularXmlFile = new File("C:/ManiBhaiBackup/systems/ctags/repository/version-" + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.3.xml"); //All Type
            //File regularXmlFile = new File("D:/ManiBhaiBackup/systems/ctags/repository/version-" + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.3.xml"); //All Type

            if (regularXmlFile.exists()) {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                Document docRegular = dBuilder.parse(regularXmlFile);

                //optional, but recommended
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work

                docRegular.getDocumentElement().normalize();
                //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                NodeList nListRegular = docRegular.getElementsByTagName("class");

                for (int i = 0; i < nListRegular.getLength(); i++) {

                    Node nNodeRegular = nListRegular.item(i);
                    //System.out.println("\nCurrent Element in Regular : " + nNodeRegular.getNodeName());

                    int numFragments = 0;
                    numFragments = Integer.parseInt(nNodeRegular.getAttributes().getNamedItem("nfragments").getNodeValue());
                    //System.out.println("numFragments = " + numFragments);

                    //int classID = 0;
                    classID = Integer.parseInt(nNodeRegular.getAttributes().getNamedItem("id").getNodeValue());
                    //System.out.println("classID = " + classID);

                    if (nNodeRegular.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElementRegular = (Element) nNodeRegular;

                        for (int j = 0; j < numFragments; j++) {

                            Element cElementRegular = (Element) eElementRegular.getElementsByTagName("source").item(j);
                            //System.out.println("\nNumber of Child Node of Element in Regular : " + cElementRegular.getChildNodes().getLength());

                            cfXmlFile[i][j] = new CodeFragment();
                            cfXmlFile[i][j].revision = rev;
                            cfXmlFile[i][j].filepath = cElementRegular.getAttribute("file");
                            cfXmlFile[i][j].startline = Integer.parseInt(cElementRegular.getAttribute("startline"));
                            cfXmlFile[i][j].endline = Integer.parseInt(cElementRegular.getAttribute("endline"));
                            if (cfXmlFile[i][j].filepath.contains("version-")) {
                                cfXmlFile[i][j].filepath = cfXmlFile[i][j].filepath.replaceAll(".ifdefed", "");

                                String[] filePath = cfXmlFile[i][j].filepath.split("version-\\d*\\/");
                                cfXmlFile[i][j].filepath = filePath[1];

                                //System.out.println("cfXmlFile[" + i + "][" + j + "] = " + cfXmlFile[i][j].filepath + " Start Line = " + cfXmlFile[i][j].startline 
                                        //+ " End Line = " + cfXmlFile[i][j].endline);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("error in method xmlFileParse." + e);
            e.printStackTrace();
        }
        return cfXmlFile;
    }
    
    public ArrayList<CodeFragment> xmlFileParseMicro(int rev) {
        CodeFragment[][] cfXmlFileMicro = new CodeFragment[50000][2];
        ArrayList<CodeFragment> cfXmlFileMicroAL = new ArrayList<>();

        try {
            CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
            cfXmlFile = xmlFileParse(rev);
            // Have to make it variable
            
            File microXmlFile = new File("C:/MicroClones/Systems/Ctags/Repository/version-" + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.30.xml"); //All Type
            //File microXmlFile = new File("D:/MicroClones/Systems/Ctags/Repository/version-" + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.30.xml"); //All Type
            
                                           
            if (microXmlFile.exists()) {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                Document docMicro = dBuilder.parse(microXmlFile); 

                //optional, but recommended
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                docMicro.getDocumentElement().normalize();

                //System.out.println("Root element :" + docMicro.getDocumentElement().getNodeName());
                NodeList nListMicro = docMicro.getElementsByTagName("clone");
                
                //System.out.println("Length of nListMicro = " + nListMicro.getLength());
                System.out.println("Revision number = " + rev);

                for (int i = 0; i < nListMicro.getLength(); i++) {

                    Node nNodeMicro = nListMicro.item(i);
                    //System.out.println("\nCurrent Element in Micro : " + nNodeMicro.getNodeName());

                    if (nNodeMicro.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElementMicro = (Element) nNodeMicro;

                        for (int j = 0; j < 2; j++) {

                            Element cElementMicro = (Element) eElementMicro.getElementsByTagName("source").item(j);
                            //System.out.println("\nNumber of Child Node of Element in Micro : " + cElementMicro.getChildNodes().getLength());

                            cfXmlFileMicro[i][j] = new CodeFragment();
                            cfXmlFileMicro[i][j].revision = rev;
                            //cfXmlFileMicro[i][j].changetype = ctype;
                            cfXmlFileMicro[i][j].filepath = cElementMicro.getAttribute("file");
                            cfXmlFileMicro[i][j].startline = Integer.parseInt(cElementMicro.getAttribute("startline"));
                            cfXmlFileMicro[i][j].endline = Integer.parseInt(cElementMicro.getAttribute("endline"));
                            if (cfXmlFileMicro[i][j].filepath.contains("version-")) {
                                cfXmlFileMicro[i][j].filepath = cfXmlFileMicro[i][j].filepath.replaceAll(".ifdefed", "");

                                String[] filePath = cfXmlFileMicro[i][j].filepath.split("version-\\d*\\/");
                                cfXmlFileMicro[i][j].filepath = filePath[1];

                                //System.out.println("Micro cfXmlFileMicro[" + i + "][" + j + "] = " + cfXmlFileMicro[i][j].filepath + " Micro Start Line = " + cfXmlFileMicro[i][j].startline 
                                        //+ " Micro End Line = " + cfXmlFileMicro[i][j].endline);
                            }
                        }
                    }
                }
            }
        
        // -----------------Converting array to arrayList----------------------    
            
        ArrayList<CodeFragment> cfXmlFileAL = new ArrayList<>();    
        
        for (int i = 0; i < cfXmlFile.length; i++) {
            for (int j = 0; j < cfXmlFile.length; j++) {
                if(cfXmlFile[i][j] != null){
                    cfXmlFileAL.add(cfXmlFile[i][j]);
                }
            }
        }
            
            
        
        
        for (int i = 0; i < cfXmlFileMicro.length; i++) {
            for (int j = 0; j < 2; j++) {
                if(cfXmlFileMicro[i][j] != null){
                    cfXmlFileMicroAL.add(cfXmlFileMicro[i][j]);
                }
            }
        }
        
        //---------------------------------------Eliminating Micro-clones which are part of Regular clones----------------------------------------------------------------
        
        //int match = 0;
        for(int i = 0; i<cfXmlFileMicroAL.size(); i++){
            for(int j = 0; j<cfXmlFileAL.size(); j++){
                if(cfXmlFileMicroAL.get(i).filepath.equals(cfXmlFileAL.get(j).filepath)){
                    if((cfXmlFileMicroAL.get(i).startline >= cfXmlFileAL.get(j).startline) && (cfXmlFileMicroAL.get(i).endline <= cfXmlFileAL.get(j).endline)){
                        
                        //match++;
                        //System.out.println("Total match found = " + match);
                        
                        //System.out.println("This is the array of Micro-clones that matches with Regular clones: i = " + i);
                        //System.out.println("Micro cfXmlFileMicro[" + i + "] = " + cfXmlFileMicroAL.get(i).filepath 
                            //+ " Micro Start Line = " + cfXmlFileMicroAL.get(i).startline + " Micro End Line = " + cfXmlFileMicroAL.get(i).endline + "\n");
                        
                        //System.out.println("This is the array of Regular clones that matches with Micro-clones: j = " + j);
                        //System.out.println("cfXmlFileMicro[" + j + "] = " + cfXmlFileAL.get(j).filepath 
                            //+ " Start Line = " + cfXmlFileAL.get(j).startline + " End Line = " + cfXmlFileAL.get(j).endline + "\n");
                        
                        if(i%2 == 0){
                            cfXmlFileMicroAL.remove(i);
                            cfXmlFileMicroAL.remove(i);
                        }
                        else{
                            cfXmlFileMicroAL.remove(i-1);
                            cfXmlFileMicroAL.remove(i-1);
                        }
                    }
                }
            }
        }
        
        /*
        int total = 0;
        for (int i = 0; i < cfXmlFileMicroNew.length; i++) {
            for (int j = 0; j < 2; j++) {
                if(cfXmlFileMicroNew[i][j] != null){
                    total++;
                    System.out.println("Total number of row = " + total + " cfXmlFileMicroNew["+i+"]["+j+"].startline = " + cfXmlFileMicroNew[i][j].startline 
                            + " cfXmlFileMicroNew["+i+"]["+j+"].endline = " + cfXmlFileMicroNew[i][j].endline + " cfXmlFileMicroNew["+i+"]["+j+"].revision = " 
                                + cfXmlFileMicroNew[i][j].revision);
                }
            }
        }
        */
        /*
        for(int j=0; j<cfXmlFileMicroAL.size(); j++){
            System.out.println("This is the array of Micro-clones: j = " + j);
            System.out.println("Micro cfXmlFileMicro[" + j + "] = " + cfXmlFileMicroAL.get(j).filepath 
                    + " Micro Start Line = " + cfXmlFileMicroAL.get(j).startline + " Micro End Line = " + cfXmlFileMicroAL.get(j).endline + "\n");
        }
        
        for(int j=0; j<cfXmlFileAL.size(); j++){
            System.out.println("This is the array of Regular clones: j = " + j);
            System.out.println("Micro cfXmlFileMicro[" + j + "] = " + cfXmlFileAL.get(j).filepath 
                    + " Micro Start Line = " + cfXmlFileAL.get(j).startline + " Micro End Line = " + cfXmlFileAL.get(j).endline + "\n");
        }
        */
        
        /*
        for(int j=0; j<cfXmlFileMicroAL.size(); j++){
            System.out.println("This is the array of Micro-clones which are not part of Regular clones: j = " + j);
            System.out.println("Micro cfXmlFileMicro[" + j + "] = " + cfXmlFileMicroAL.get(j).filepath 
                    + " Micro Start Line = " + cfXmlFileMicroAL.get(j).startline + " Micro End Line = " + cfXmlFileMicroAL.get(j).endline + "\n");
        }
        */
        
            
        } catch (Exception e) {
            System.out.println("error in method xmlFileParseMicro." + e);
            e.printStackTrace();
        }
        return cfXmlFileMicroAL;
    }

    public CodeFragment getInstanceInNextRevision(CodeFragment cf) {
        try {
            CodeFragment instance = new CodeFragment();

            int crevision = cf.revision;
            int nrevision = crevision + 1;

            int nstartline = 999999999;
            int nendline = -1;

            int changed = 0;
            
            String cfilepath = "C:/ManiBhaiBackup/systems/ctags/repository/version-" + crevision + "/" + cf.filepath; // Have to make it variable
            String nfilepath = "C:/ManiBhaiBackup/systems/ctags/repository/version-" + nrevision + "/" + cf.filepath; // Have to make it variable
            
            //String cfilepath = "D:/ManiBhaiBackup/systems/ctags/repository/version-" + crevision + "/" + cf.filepath; // Have to make it variable
            //String nfilepath = "D:/ManiBhaiBackup/systems/ctags/repository/version-" + nrevision + "/" + cf.filepath; // Have to make it variable

            File file = new File(nfilepath);
            if (!file.exists()) {
                return null;
            }

            String[][] filecompare = cc.compareFiles(cfilepath, nfilepath);

            for (int i = 0; filecompare[i][0] != null; i++) {
                String ln = filecompare[i][0].trim();
                if (ln.length() == 0) {
                    continue;
                }
                int line = Integer.parseInt(ln);
                if (line > cf.endline) {
                    break;
                }
                if (line == 509) {
                    int a = 10;
                }
                if (line >= cf.startline && line <= cf.endline) {
                    String nln = filecompare[i][2].trim(); // SHOWING NULL POINTER EXCEPTION HERE
                    if (nln.trim().length() > 0) {
                        int nline = Integer.parseInt(nln);
                        if (nstartline > nline) {
                            nstartline = nline;
                        }
                        if (nendline < nline) {
                            nendline = nline;
                        }
                    }
                    if (!filecompare[i][1].trim().equals(filecompare[i][3].trim())) {
                        if (filecompare[i][1].trim().length() > 0 || filecompare[i][3].trim().length() > 0) {
                            changed = 1;
                        }
                    }
                }
            }

            if (nendline == -1) {
                return null;
            }

            instance.revision = nrevision;
            instance.filepath = cf.filepath;
            instance.startline = nstartline;
            instance.endline = nendline;
            instance.changetype = cf.changetype;

            return instance;

        } catch (Exception e) {
            return null;
        }
    }
}
