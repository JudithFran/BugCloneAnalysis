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
            System.out.println("error in getBugFixCommits = " + e);
        }

        return bugFixCommits;
    }
    
    public CodeFragment[][] getChangedBugFixCommits() {

        SingleChange[] changedBugFixCommits = new SingleChange[10000];
        SingleChange[][] changedBugFixCommits2D = new SingleChange[10000][10000];
        CodeFragment[][] changedBugFixCommits2DNew = new CodeFragment[10000][10000];

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

            //Matching bug-fix commits with changed revisions and saving in 1D array
            
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
            // Changing the data type from SingleChange to CodeFragment of the 2D array          
            for(i = 0; i<changedBugFixCommits2D.length; i++){
                for(int j = 0; j<changedBugFixCommits2D.length; j++){
                    if(changedBugFixCommits2D[i][j] != null){
                        changedBugFixCommits2DNew[i][j] = new CodeFragment();
                        changedBugFixCommits2DNew[i][j].revision = Integer.parseInt(changedBugFixCommits2D[i][j].revision);
                        changedBugFixCommits2DNew[i][j].startline = Integer.parseInt(changedBugFixCommits2D[i][j].startline);
                        changedBugFixCommits2DNew[i][j].endline = Integer.parseInt(changedBugFixCommits2D[i][j].endline);
                        changedBugFixCommits2DNew[i][j].filepath = changedBugFixCommits2D[i][j].filepath;
                        changedBugFixCommits2DNew[i][j].changetype = changedBugFixCommits2D[i][j].changetype;
                    }
                }
            }
            /*
            int count = 0;
            for(i = 0; i<changedBugFixCommits2D.length; i++){
                for(int j = 0; j<changedBugFixCommits2D.length; j++){
                    if(changedBugFixCommits2D[i][j] != null){
                        System.out.println("getChangedBugFixCommits: getChangedBugFixCommits["+i+"]["+j+"].revision = " + changedBugFixCommits2D[i][j].revision 
                            + " Filepath = " + changedBugFixCommits2D[i][j].filepath + " Startline = " + changedBugFixCommits2D[i][j].startline 
                                + " Endline = " + changedBugFixCommits2D[i][j].endline + " Changetype = " + changedBugFixCommits2D[i][j].changetype);
                        count++;   
                    }
                }
            }
            System.out.println("Total number of changed bug-fix code fragments (CF) = " + count);
            */
        } catch (Exception e) {
            System.out.println("error in getChangedBugFixCommits = " + e);
        }
        return changedBugFixCommits2DNew;
    }
    
    public void bugReplicationR(){
        try{
            CodeFragment[][] cloneFragmentPair = new CodeFragment[10000][2];
            ArrayList<CodeFragment> bugRep = new ArrayList<>();
            
            cloneFragmentPair = isClonePair();
            
            if(cloneFragmentPair != null)
                for (int i = 0; i < cloneFragmentPair.length; i++) 
                    for (int j = 0; j < 2; j++) 
                        if(cloneFragmentPair[i][j] != null)
                            System.out.println("bugReplicationR: cfp["+i+"]["+j+"].revision = " + cloneFragmentPair[i][j].revision + " Filepath = " + cloneFragmentPair[i][j].filepath
                                + " Startline = " + cloneFragmentPair[i][j].startline + " Endline = " + cloneFragmentPair[i][j].endline);
            
            // Finding Replicated Bugs
            int numReplicatedBugFixCommits = 0;
            for(int x = 0; cloneFragmentPair[x][0] != null; x++){
                if(cloneFragmentPair[x][0] != null && cloneFragmentPair[x][1] != null){
                        CodeFragment[] cloneFragmentPairINR = new CodeFragment[2];

                        cloneFragmentPairINR[0] = getInstanceInNextRevision(cloneFragmentPair[x][0]);
                        cloneFragmentPairINR[1] = getInstanceInNextRevision(cloneFragmentPair[x][1]);

                        if(cloneFragmentPairINR[0] != null && cloneFragmentPairINR[1] != null){
                            if(isClonePairBinary(cloneFragmentPairINR[0], cloneFragmentPairINR[1]) == 1){
                                numReplicatedBugFixCommits++;
                                System.out.println("////////////////////////////////////////////////////////////////////////////Replicated Bug Fixing Change Found////////////////////////////////////////////////////////////////////////////.");
                                System.out.println("numReplicatedBugFixCommits for Regular Clones = " + numReplicatedBugFixCommits);
                                
                                bugRep.add(cloneFragmentPair[x][0]);
                                bugRep.add(cloneFragmentPair[x][1]);
                            }
                        }
                }
            }
            
        }catch(Exception e){
            System.out.println("error in BugReplicationR = " + e);
            e.printStackTrace();
        }
    }
    
    public CodeFragment[][] isClonePair(){
        CodeFragment[][] cfp = new CodeFragment[10000][2];
        try{
            CodeFragment[][] changedBugFixCommits = new CodeFragment[10000][10000];
            changedBugFixCommits = getChangedBugFixCommits();
            
            CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
            CodeFragment[] cfXmlFileMatch = new CodeFragment[10000];
            int x = 0;
            
            // Looping through the changed bug-fix commit 2D array
            for(int i = 0; i<changedBugFixCommits.length; i++){
                for(int j = 0; j<changedBugFixCommits.length; j++){
                    if(changedBugFixCommits[i][j] != null){
                        System.out.println("Revision number = " + changedBugFixCommits[i][j].revision);
                        cfXmlFile = xmlFileParse(changedBugFixCommits[i][j].revision);
                        
                        // Looping through the xml file of each revision
                        for (int m = 0; m < cfXmlFile.length; m++) {
                            for (int n = 0; n < cfXmlFile.length; n++) {

                                if (cfXmlFile[m][n] != null){
                                    
                                    if(changedBugFixCommits[i][j].filepath.equals(cfXmlFile[m][n].filepath)){
                                        
                                        // Checking for matches with changed bug-fix commits with each line of xml file of a particular revision
                                        // Matches if line numbers of code fragment from changedBugFixCommits are overlapping with line numbers range of cfXmlFile
                                        if(((changedBugFixCommits[i][j].startline >= cfXmlFile[m][n].startline) && (changedBugFixCommits[i][j].endline <= cfXmlFile[m][n].endline))
                                            ||((changedBugFixCommits[i][j].startline <= cfXmlFile[m][n].startline) && (changedBugFixCommits[i][j].endline <= cfXmlFile[m][n].endline) 
                                                && (changedBugFixCommits[i][j].endline >= cfXmlFile[m][n].startline)) 
                                            ||((changedBugFixCommits[i][j].startline >= cfXmlFile[m][n].startline) && (changedBugFixCommits[i][j].endline >= cfXmlFile[m][n].endline) 
                                                && (changedBugFixCommits[i][j].startline <= cfXmlFile[m][n].endline))){
                                        
                                            System.out.println("*********************************************** File Name matched ***********************************************");
                                            
                                            System.out.println("Matched CF from changedBugFixCommits["+i+"]["+j+"] = " + changedBugFixCommits[i][j].filepath + " Start Line = " 
                                                    + changedBugFixCommits[i][j].startline + " End Line = " + changedBugFixCommits[i][j].endline);
                                            
                                            System.out.println("Matched CF1 from cfXmlFile["+m+"]["+n+"] = " + cfXmlFile[m][n].filepath + " Start Line = " + cfXmlFile[m][n].startline 
                                                + " End Line = " + cfXmlFile[m][n].endline);
                                            
                                            // Saving the matched entries into a separate 1D array
                                            cfXmlFileMatch[x] = cfXmlFile[m][n];
                                            x++;
                                        }
                                    }
                                }
                            }
                        }
                        
                        
                    }
                }
            }
            int len = 0;
            for(x = 0; cfXmlFileMatch[x] != null; x++){
                System.out.println("cfXmlFileMatch["+x+"] Revision = " + cfXmlFileMatch[x].revision + " Filepath = " + cfXmlFileMatch[x].filepath 
                    + " Startline = " + cfXmlFileMatch[x].startline + " Endline = " + cfXmlFileMatch[x].endline);
                len++;
            }
            System.out.println("len = " + len);
            
            // Delete duplicate values from cfXmlFileMatch[x] array
            for(int i = 0; i < len; i++){
                for(int j = i+1; j < len; ){
                    if(cfXmlFileMatch[i].revision == cfXmlFileMatch[j].revision && cfXmlFileMatch[i].filepath.equals(cfXmlFileMatch[j].filepath) 
                            && cfXmlFileMatch[i].startline == cfXmlFileMatch[j].startline && cfXmlFileMatch[i].endline == cfXmlFileMatch[j].endline){
                        for(x = j; x < len; x++){
                            cfXmlFileMatch[x] = cfXmlFileMatch[x+1];
                        }
                        len--;    
                    }
                    else{
                        j++;
                    }
                }
            }
            
            System.out.println("After removing duplicate values: ");
            for(x = 0; cfXmlFileMatch[x] != null; x++){
                System.out.println("cfXmlFileMatch["+x+"] Revision = " + cfXmlFileMatch[x].revision + " Filepath = " + cfXmlFileMatch[x].filepath 
                    + " Startline = " + cfXmlFileMatch[x].startline + " Endline = " + cfXmlFileMatch[x].endline);
            }
            
            int classID1 = 0, classID2 = 0;
            x = 0;
            for(int i = 0; cfXmlFileMatch[i] != null; i++){
                for(int j = i+1; cfXmlFileMatch[j] != null; j++){
                    if(cfXmlFileMatch[i].revision == cfXmlFileMatch[j].revision){
                        System.out.println("Revision = " + cfXmlFileMatch[i].revision);
                        
                        classID1 = getClassID(cfXmlFileMatch[i]);
                        //System.out.println("classID1 = " + classID1);
                        
                        classID2 = getClassID(cfXmlFileMatch[j]);
                        //System.out.println("classID2 = " + classID2 + "\n");
                        
                        if(classID1 == classID2){
                            System.out.println("********************************************Pair Found********************************************");
                            cfp[x][0] = cfXmlFileMatch[i];
                            cfp[x][1] = cfXmlFileMatch[j];
                            x++;
                        }
                    }
                }
            }
            
        }catch (Exception e) {
            System.out.println("error in method isClonePair = " + e);
            e.printStackTrace();
        }
        return cfp;
    }
    
    public int getClassID(CodeFragment cf) {
        CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
        int classID = 0;
        try {
            // Have to make it variable
            
            File regularXmlFile = new File("C:/ManiBhaiBackup/systems/ctags/repository/version-" + cf.revision + "_blocks-blind-clones/version-" + cf.revision + "_blocks-blind-clones-0.3.xml"); //All Type
            //File regularXmlFile = new File("D:/ManiBhaiBackup/systems/ctags/repository/version-" + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.3.xml"); //All Type

            if (regularXmlFile.exists()) {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                Document docRegular = dBuilder.parse(regularXmlFile); // xml file

                //optional, but recommended
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work

                docRegular.getDocumentElement().normalize();
                //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                NodeList nListRegular = docRegular.getElementsByTagName("class"); // class

                for (int i = 0; i < nListRegular.getLength(); i++) { // i = each class

                    Node nNodeRegular = nListRegular.item(i);
                    //System.out.println("\nCurrent Element in Regular : " + nNodeRegular.getNodeName());

                    int numFragments = 0;
                    numFragments = Integer.parseInt(nNodeRegular.getAttributes().getNamedItem("nfragments").getNodeValue());
                    //System.out.println("numFragments = " + numFragments);

                    
                    classID = Integer.parseInt(nNodeRegular.getAttributes().getNamedItem("id").getNodeValue());
                    //System.out.println("classID = " + classID);
                    
                    

                    if (nNodeRegular.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElementRegular = (Element) nNodeRegular;

                        for (int j = 0; j < numFragments; j++) { // j = each source

                            Element cElementRegular = (Element) eElementRegular.getElementsByTagName("source").item(j);
                            //System.out.println("\nNumber of Child Node of Element in Regular : " + cElementRegular.getChildNodes().getLength());

                            cfXmlFile[i][j] = new CodeFragment();
                            cfXmlFile[i][j].revision = cf.revision;
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
                            
                            if(cf.filepath.equals(cfXmlFile[i][j].filepath) && cf.startline == cfXmlFile[i][j].startline && cf.endline == cfXmlFile[i][j].endline){
                                return classID;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("error in method xmlFileParse." + e);
            e.printStackTrace();
        }
        return 0;
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
    
    public CodeFragment[][] xmlFileParse(int rev) {
        CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
        try {
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