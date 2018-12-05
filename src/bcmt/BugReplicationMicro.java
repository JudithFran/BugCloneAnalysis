/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcmt;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


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

        String abs_filepath = "D:/ManiBhaiBackup/systems/ctags/repository/version-" + revision + "/" + filepath; // Have to make it variable
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
        String abs_filepath = "D:/ManiBhaiBackup/systems/ctags/repository/version-" + revision + "/" + filepath; // Have to make it variable
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
    
    //This is the main method. This method is calling all other methods.
    public void bugReplication(){
        CodeFragment[][] cfXmlFileMicro = new CodeFragment[50000][2];
        try{
            SingleChange[] changedBugFixCommits = new SingleChange[10000];
            changedBugFixCommits = getChangedBugFixCommits();
            
            CodeFragment cf = new CodeFragment();
            CodeFragment[] cloneFragmentPair = new CodeFragment[2];
            int numReplicatedBugFixCommits = 0;
            ArrayList<CodeFragment> bugRep = new ArrayList<>();
            
            cfXmlFileMicro = xmlFileParseMicro(10, "x");
            
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
            
            for (int m = 0; changedBugFixCommits[m] != null; m++) {
                cf.revision = Integer.parseInt(changedBugFixCommits[m].revision);
                cf.startline = Integer.parseInt(changedBugFixCommits[m].startline);
                cf.endline = Integer.parseInt(changedBugFixCommits[m].endline);
                cf.filepath = changedBugFixCommits[m].filepath;
                cf.changetype = changedBugFixCommits[m].changetype;

                System.out.println("\nCode Fragment (CF): ");
                cf.getFragment();
                cf.showFragment();
                
                cloneFragmentPair = isClonePair(cf);
                
                if(cloneFragmentPair[0] != null && cloneFragmentPair[1] != null){
                    CodeFragment[] cloneFragmentPairINR = new CodeFragment[2];

                    cloneFragmentPairINR[0] = getInstanceInNextRevision(cloneFragmentPair[0]);
                    cloneFragmentPairINR[1] = getInstanceInNextRevision(cloneFragmentPair[1]);

                    if(cloneFragmentPairINR[0] != null && cloneFragmentPairINR[1] != null){
                        if(isClonePairBinary(cloneFragmentPairINR[0], cloneFragmentPairINR[1]) == 1){
                            numReplicatedBugFixCommits++;
                            System.out.println("////////////////////////////////////////////////////////////////////////////Replicated Bug Fixing Change Found////////////////////////////////////////////////////////////////////////////.");
                            System.out.println("numReplicatedBugFixCommits for Regular Clones = " + numReplicatedBugFixCommits);
                            
                            if(bugRep.contains(cloneFragmentPair[0])){
                            //if(bugRep.equals(cloneFragmentPair[0])){
                                //bugRep.remove(cloneFragmentPair[0]);
                            }
                            else
                                bugRep.add(cloneFragmentPair[0]);
                            
                            if(bugRep.contains(cloneFragmentPair[1])){
                            //if(bugRep.equals(cloneFragmentPair[1])){
                                //bugRep.remove(cloneFragmentPair[1]);
                            }
                            else 
                                bugRep.add(cloneFragmentPair[1]);
  
                        }
                    }
                }
            }
  
            for(int j=0; j<bugRep.size() ;j++){
                System.out.println("This is the array of replicated bugs: j = " + j);
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
 
            System.out.println("Total Replicated Bug-Fixing Commits = " + numReplicatedBugFixCommits);
            
            
        }catch(Exception e){
            System.out.println("error in BugReplication()." + e);
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

    public SingleChange[] getChangedBugFixCommits() {

        SingleChange[] changedBugFixCommits = new SingleChange[10000];

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
        return changedBugFixCommits;
    }
    public int isClonePairBinary(CodeFragment cf1, CodeFragment cf2){
        int pair = 0;
        try{
            CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];

            cfXmlFile = xmlFileParse(cf1.revision, cf1.changetype);

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
    public CodeFragment[] isClonePair(CodeFragment cf1){
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
            CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
            cfXmlFile = xmlFileParse(cf1.revision, cf1.changetype);

            for (int i = 0; i < cfXmlFile.length; i++) {
                for (int j = 0; j < cfXmlFile.length; j++) {

                    if (cfXmlFile[i][j] != null){
                        //System.out.println("cfXmlFile[" + i + "][" + j + "] = " + cfXmlFile[i][j].filepath + " Start Line = " + cfXmlFile[i][j].startline 
                                        //+ " End Line = " + cfXmlFile[i][j].endline);
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
                                System.out.println("isClonePair: cfXmlFile[" + i + "][" + j + "] filepath = " + cfXmlFile[i][j].filepath + " revision = " 
                                    + cfXmlFile[i][j].revision + " startline = " + cfXmlFile[i][j].startline + " endline = " + cfXmlFile[i][j].endline);
                                                              
                                if(cfXmlFile[i][j+1] != null && cf1.filepath.equals(cfXmlFile[i][j+1].filepath)){
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
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("error in method isClonePair." + e);
            e.printStackTrace();
        }
        return cfp;
    }

    public CodeFragment[][] xmlFileParse(int rev, String ctype) {
        CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
        try {
            // Have to make it variable
            File regularXmlFile = new File("D:/ManiBhaiBackup/systems/ctags/repository/version-" + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.3.xml"); //All Type

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

                    int classID = 0;
                    classID = Integer.parseInt(nNodeRegular.getAttributes().getNamedItem("id").getNodeValue());
                    //System.out.println("classID = " + classID);

                    if (nNodeRegular.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElementRegular = (Element) nNodeRegular;

                        for (int j = 0; j < numFragments; j++) {

                            Element cElementRegular = (Element) eElementRegular.getElementsByTagName("source").item(j);
                            //System.out.println("\nNumber of Child Node of Element in Regular : " + cElementRegular.getChildNodes().getLength());

                            cfXmlFile[i][j] = new CodeFragment();
                            cfXmlFile[i][j].revision = rev;
                            cfXmlFile[i][j].changetype = ctype;
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
    
    public CodeFragment[][] xmlFileParseMicro(int rev, String ctype) {
        CodeFragment[][] cfXmlFileMicro = new CodeFragment[50000][2];
        CodeFragment[][] cfXmlFileMicroNew = new CodeFragment[50000][2];
        int lenMicroList;
        try {
            CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
            cfXmlFile = xmlFileParse(rev, ctype);
            // Have to make it variable
            File microXmlFile = new File("D:/MicroClones/Systems/Ctags/Repository/version-" + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.30.xml"); //All Type
            //File microXmlFile = new File("D:/MicroClones/Systems/Ctags/Repository/version-" + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.3.xml"); //All Type
                                           
            if (microXmlFile.exists()) {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                Document docMicro = dBuilder.parse(microXmlFile); 

                //optional, but recommended
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                docMicro.getDocumentElement().normalize();

                //System.out.println("Root element :" + docMicro.getDocumentElement().getNodeName());
                NodeList nListMicro = docMicro.getElementsByTagName("clone");
                
                System.out.println("Length of nListMicro = " + nListMicro.getLength());
                
                lenMicroList = nListMicro.getLength();

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
                            cfXmlFileMicro[i][j].changetype = ctype;
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
        
        // -----------------Converting array to arrayList-------------    
            
        ArrayList<CodeFragment> cfXmlFileAL = new ArrayList<>();    
        
        for (int i = 0; i < cfXmlFile.length; i++) {
            for (int j = 0; j < cfXmlFile.length; j++) {
                if(cfXmlFile[i][j] != null){
                    cfXmlFileAL.add(cfXmlFile[i][j]);
                }
            }
        }
            
            
        ArrayList<CodeFragment> cfXmlFileMicroAL = new ArrayList<>();
        
        for (int i = 0; i < cfXmlFileMicro.length; i++) {
            for (int j = 0; j < 2; j++) {
                if(cfXmlFileMicro[i][j] != null){
                    cfXmlFileMicroAL.add(cfXmlFileMicro[i][j]);
                }
            }
        }
        
        //----------------Eliminating Micro-clones which are part of Regular clones-------------------
        
        int match = 0;
        for(int i = 0; i<cfXmlFileMicroAL.size(); i++){
            for(int j = 0; j<cfXmlFileAL.size(); j++){
                if(cfXmlFileMicroAL.get(i).filepath.equals(cfXmlFileAL.get(j).filepath)){
                    if((cfXmlFileMicroAL.get(i).startline >= cfXmlFileAL.get(j).startline) && (cfXmlFileMicroAL.get(i).endline <= cfXmlFileAL.get(j).endline)){
                        
                        match++;
                        System.out.println("Total match found = " + match);
                        
                        System.out.println("This is the array of Micro-clones that matches with Regular clones: i = " + i);
                        System.out.println("Micro cfXmlFileMicro[" + i + "] = " + cfXmlFileMicroAL.get(i).filepath 
                            + " Micro Start Line = " + cfXmlFileMicroAL.get(i).startline + " Micro End Line = " + cfXmlFileMicroAL.get(i).endline + "\n");
                        
                        System.out.println("This is the array of Regular clones that matches with Micro-clones: j = " + j);
                        System.out.println("cfXmlFileMicro[" + j + "] = " + cfXmlFileAL.get(j).filepath 
                            + " Start Line = " + cfXmlFileAL.get(j).startline + " End Line = " + cfXmlFileAL.get(j).endline + "\n");
                        
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
        int match = 0; 
        int i_new = 0; 
        
        for(int i = 0; i<cfXmlFileMicro.length; i++){
            for(int j = 0; j<2; j++){
                
                if(cfXmlFileMicro[i][j] != null){
                    
                    for(int k = 0; k<cfXmlFile.length ;k++){
                        for(int l = 0; cfXmlFile[k][l] != null ;l++){
                            
                            if(cfXmlFile[k][l] != null){
                                
                                if(cfXmlFileMicro[i][j].filepath.equals(cfXmlFile[k][l].filepath)){
                                    if((cfXmlFileMicro[i][j].startline >= cfXmlFile[k][l].startline) && (cfXmlFileMicro[i][j].endline <= cfXmlFile[k][l].endline)){
                                        
                                        System.out.println("Micro cfXmlFileMicro[" + i + "][" + j + "] = " + cfXmlFileMicro[i][j].filepath + " Micro Start Line = " 
                                            + cfXmlFileMicro[i][j].startline + " Micro End Line = " + cfXmlFileMicro[i][j].endline);
                                        
                                        match++;
                                        System.out.println("Total match number = " + match + " cfXmlFile[" + k + "][" + l + "] = " + cfXmlFile[k][l].filepath 
                                                + " Start Line = " + cfXmlFile[k][l].startline + " End Line = " + cfXmlFile[k][l].endline + "\n");
                                        
                                        
                                        
                                        //if(j == 0)
                                            //cfXmlFileMicroNew[i][j] = cfXmlFileMicro[i][j+1];
                                            //j = j+1;
                                        //else if(j == 1)
                                            //cfXmlFileMicroNew[i][j] = cfXmlFileMicro[i+1][j];
                                            //i = i+1;
                                        
                                    }    
                                }
                                //else{
                                    //cfXmlFileMicroAL.add(cfXmlFileMicro[i][j]);
                                //}
                                
                                else{
                                    System.out.println("i_new = " + i_new + " j = " + j);
                                    //cfXmlFileMicroNew[i_new][j_new] = cfXmlFileMicro[i][j];
                                    cfXmlFileMicroNew[i_new][j] = new CodeFragment();
                                    cfXmlFileMicroNew[i_new][j].revision = cfXmlFileMicro[i][j].revision;
                                    cfXmlFileMicroNew[i_new][j].startline = cfXmlFileMicro[i][j].startline;
                                    cfXmlFileMicroNew[i_new][j].endline = cfXmlFileMicro[i][j].endline;
                                    cfXmlFileMicroNew[i_new][j].filepath = cfXmlFileMicro[i][j].filepath;
                                    cfXmlFileMicroNew[i_new][j].changetype = cfXmlFileMicro[i][j].changetype;
                                    
                                    System.out.println("New Micro cfXmlFileMicroNew[" + i_new + "][" + j + "] = " + cfXmlFileMicroNew[i_new][j].filepath 
                                            + " New Micro Start Line = " + cfXmlFileMicroNew[i_new][j].startline + " New Micro End Line = " + cfXmlFileMicroNew[i_new][j].endline);
                                    
                                    //i_new++;
                                }
                                
                            }
                        }
                    }
                }
            }
        }
        
        */
        
        
        
        
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
        for(int j=0; j<cfXmlFileMicroAL.size(); j++){
            System.out.println("This is the array of Micro-clones which are not part of Regular clones: j = " + j);
            System.out.println("Micro cfXmlFileMicro[" + j + "] = " + cfXmlFileMicroAL.get(j).filepath 
                    + " Micro Start Line = " + cfXmlFileMicroAL.get(j).startline + " Micro End Line = " + cfXmlFileMicroAL.get(j).endline + "\n");
        }
            
        } catch (Exception e) {
            System.out.println("error in method xmlFileParseMicro." + e);
            e.printStackTrace();
        }
        return cfXmlFileMicroNew;
    }

    public CodeFragment getInstanceInNextRevision(CodeFragment cf) {
        try {
            CodeFragment instance = new CodeFragment();

            int crevision = cf.revision;
            int nrevision = crevision + 1;

            int nstartline = 999999999;
            int nendline = -1;

            int changed = 0;

            String cfilepath = "D:/ManiBhaiBackup/systems/ctags/repository/version-" + crevision + "/" + cf.filepath; // Have to make it variable
            String nfilepath = "D:/ManiBhaiBackup/systems/ctags/repository/version-" + nrevision + "/" + cf.filepath; // Have to make it variable

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
