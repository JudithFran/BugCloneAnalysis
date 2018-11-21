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

    public int isClonePair() throws ParserConfigurationException, SAXException, IOException {
        int pair = 0;
        try {
            CodeFragment cf1 = new CodeFragment();
            CodeFragment cf2 = new CodeFragment();

            SingleChange[] changedBugFixCommits = new SingleChange[10000];
            changedBugFixCommits = getChangedBugFixCommits();

            /*
            int lenChangedBugFixCommits = 0;
            for(int i = 0; changedBugFixCommits[i] != null; i++){
                System.out.println("Revision [" + i + "] in changedBugFixCommits in isClonePair()= " + changedBugFixCommits[i].revision);
                lenChangedBugFixCommits = i;
            }
            System.out.println("Length of the changedBugFixCommits = " + lenChangedBugFixCommits);
            */
            // Have to make loop for all changed bug-fix commits --- DONE
            for (int m = 0; changedBugFixCommits[m] != null; m++) {
                cf1.revision = Integer.parseInt(changedBugFixCommits[m].revision);
                cf1.startline = Integer.parseInt(changedBugFixCommits[m].startline);
                cf1.endline = Integer.parseInt(changedBugFixCommits[m].endline);
                cf1.filepath = changedBugFixCommits[m].filepath;
                cf1.changetype = changedBugFixCommits[m].changetype;

                System.out.println("\nCode Fragment 1 (CF1): ");
                cf1.getFragment();
                cf1.showFragment();

                CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];

                cfXmlFile = xmlFileParse(Integer.parseInt(changedBugFixCommits[m].revision), changedBugFixCommits[m].changetype);

                for (int i = 0; i < cfXmlFile.length; i++) {
                    for (int j = 0; j < cfXmlFile.length; j++) {

                        if (cfXmlFile[i][j] != null){
                            //System.out.println("cfXmlFile[" + i + "][" + j + "] = " + cfXmlFile[i][j].filepath);
                            
                            //cfXmlFile[i][j] = new CodeFragment();
                            if(cf1.filepath.equals(cfXmlFile[i][j].filepath)){
                                
                                System.out.println("cfXmlFile[" + i + "][" + j + "] filepath = " + cfXmlFile[i][j].filepath + " revision = " 
                                        + cfXmlFile[i][j].revision + " startline = " + cfXmlFile[i][j].startline + " endline = " + cfXmlFile[i][j].endline);
                                
                                if(cfXmlFile[i][j+1] != null && cf1.filepath.equals(cfXmlFile[i][j+1].filepath)){
                                    cf2.revision = cfXmlFile[i][j+1].revision;
                                    cf2.startline = cfXmlFile[i][j+1].startline;
                                    cf2.endline = cfXmlFile[i][j+1].endline;
                                    cf2.filepath = cfXmlFile[i][j+1].filepath;
                                    cf2.changetype = cfXmlFile[i][j+1].changetype;
                                    System.out.println("************************************************************************************Clone Pair Found************************************************************************************");
                                    System.out.println("\nCode Fragment 2 (CF2): ");
                                    cf2.getFragment();
                                    cf2.showFragment();
                                    pair = 1;
                                    
                                }
                            }
                        }
                    }
                }

            } // for(int m = 0; changedBugFixCommits[m] != null; m++){
        } catch (Exception e) {
            System.out.println("error in method isClonePair." + e);
            e.printStackTrace();
        }
        return pair;
    }

    public CodeFragment[][] xmlFileParse(int rev, String ctype) {
        CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
        try {
            SingleChange[] changedBugFixCommits = new SingleChange[10000];
            changedBugFixCommits = getChangedBugFixCommits();
            // Have to make it variable

            //File microfXmlFile = new File("carolversion-1700_blocks-blind-clones-0.11.xml"); //All Type
            File regularfXmlFile = new File("D:/ManiBhaiBackup/systems/ctags/repository/version-" + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.3.xml"); //All Type

            if (regularfXmlFile.exists()) {

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
 /*
                        for (int i = 0; i < nl.getLength(); i++)
                            {
                            Node currentItem = nl.item(i);
                            String key = currentItem.getAttributes().getNamedItem("type").getNodeValue();
                            System.out.println(key);
                            }
                 */
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

                                //System.out.println("cfXmlFile[" + i + "][" + j + "] = " + cfXmlFile[i][j].filepath);
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
