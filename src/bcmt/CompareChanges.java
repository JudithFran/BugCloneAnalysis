/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcmt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Judith
 */
public class CompareChanges {
    
    public String [][] compareFiles (String file1, String file2){
        String [] diff12 = getFileDiff(file1, file2);
        String [] fileString1 = readFile (file1);
        String [] fileString2 = readFile (file2);
        
        
        //[][0] = line number from file1.
        //[][1] = line from file1.
        //[][2] = line number from file2.
        //[][3] = line from file2.        
        String [][] compare = new String [10000][4];
        
        
        String [][] compare1 = new String[10000][2];
        String [][] compare2 = new String[10000][2];
        int n1 = 0, n2=0;
        String line = "";
        
        
        int c1=0, c2=0;
        
        for (int i =0;diff12[i] != null;i++ ){
            line = diff12[i];
            if (line.charAt(0)=='>' || line.charAt(0)=='<' || line.charAt(0)=='-' || line.charAt(0)=='\\') {continue;}
            int oldline1 = -1;
            int oldline2 = -1;
            int newline1 = -1;
            int newline2 = -1;

            String oldline = line.split("[a|c|d]+")[0];
            String newline = line.split("[a|c|d]+")[1];

            oldline1 = Integer.parseInt (oldline.split("[,]+")[0]);
            oldline2 = oldline1;
            if (oldline.contains(","))
            {
                oldline2 = Integer.parseInt (oldline.split("[,]+")[1]);
            }


            newline1 = Integer.parseInt (newline.split("[,]+")[0]);
            newline2 = newline1;
            if (newline.contains(","))
            {
                newline2 = Integer.parseInt (newline.split("[,]+")[1]);
            }
            
            oldline1 = oldline1 - 1;
            oldline2 = oldline2 - 1;
            newline1 = newline1 - 1;
            newline2 = newline2 - 1;
            
            //append case.
            if (line.contains("a"))
            {
                //taking care of file1.
                for (int j=c1;j<=oldline1;j++)
                {
                    compare1[n1][0] = j+"";
                    compare1[n1][1] = fileString1[j];
                    n1++;
                }
                for (int j=newline1;j<=newline2;j++)
                {
                    compare1[n1][0] = "";
                    compare1[n1][1] = "";
                    n1++;
                }
                c1 = oldline1+1;
                
                //taking care of file2.
                for (int j=c2;j<=newline2;j++)
                {
                    compare2[n2][0] = j+"";
                    compare2[n2][1] = fileString2[j];
                    n2++;
                }
                c2 = newline2+1;
            }
            
            //delete case
            if (line.contains("d"))
            {
                //taking care of file1.
                for (int j=c1;j<=oldline2;j++)
                {
                    compare1[n1][0] = j+"";
                    compare1[n1][1] = fileString1[j];
                    n1++;
                }
                c1 = oldline2+1;
                
                //taking care of file2.
                for (int j=c2;j<=newline1;j++)
                {
                    compare2[n2][0] = j+"";
                    compare2[n2][1] = fileString2[j];
                    n2++;
                }
                for (int j=oldline1;j<=oldline2;j++)
                {
                    compare2[n2][0] = "";
                    compare2[n2][1] = "";
                    n2++;
                }
                c2 = newline1+1;
            }
            
            //change case.
            if (line.contains ("c"))
            {
                //taking care of file1.
                for (int j=c1;j<=oldline2;j++)
                {
                    compare1[n1][0] = j+"";
                    compare1[n1][1] = fileString1[j];
                    n1++;
                }
                c1 = oldline2+1;
                
                //taking care of file2.
                for (int j=c2;j<=newline2;j++)
                {
                    compare2[n2][0] = j+"";
                    compare2[n2][1] = fileString2[j];
                    n2++;
                }
                c2 = newline2+1;
                
                //taking of white spaces.
                int l1 = oldline2-oldline1+1;
                int l2 = newline2-newline1+1;
                                               
                if (l1>l2)
                {
                    for (int j=0;j<l1-l2;j++)
                    {
                        compare2[n2][0] = "";
                        compare2[n2][1] = "";
                        n2++;
                    }
                }
                else
                {
                    for (int j=0;j<l2-l1;j++)
                    {
                        compare1[n1][0] = "";
                        compare1[n1][1] = "";
                        n1++;
                    }
                }                
            }                        
        }
        
        for (int j=c1;fileString1[j] != null;j++)
        {
            compare1[n1][0] = j+"";
            compare1[n1][1] = fileString1[j];
            n1++;
        }
        
        for (int j=c2;fileString2[j] != null;j++)
        {
            compare2[n2][0] = j+"";
            compare2[n2][1] = fileString2[j];
            n2++;
        }
        
        for (int i =0;i<n1;i++)
        {
            compare[i][0] = compare1[i][0];
            compare[i][1] = compare1[i][1];
            compare[i][2] = compare2[i][0];
            compare[i][3] = compare2[i][1];            
        }
        
        
        return compare;
    }
    
    
    public String [] getFileDiff (String file1, String file2)
    {
        String [] diff = new String [5000];
        try
        {
            Process p = Runtime.getRuntime().exec("tools/DiffWin/bin/diff " + " "+file1+" "+file2+""); // what is "tools/DiffWin/bin/diff " ?
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            
            int lineCount = 0;

            while ((line = in.readLine()) != null)
            {
                diff[lineCount] = line;
                lineCount++;
            }
            in.close();            
        }
        catch (Exception e)
        {
            System.out.println ("error in method = getFileDiff. "+ e);
        }

        return diff;
        
    }
    
    public String [] readFile (String file)
    {
        String [] fileString = new String[10000];
        int n =0;
        
        try
        {
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (file)));
            String str = "";
            
            while ((str = br.readLine())!= null)
            {
                fileString[n] = str;
                n++;
            }
        }
        catch (Exception e)
        {
            System.out.println ("error in method = readFile (String file). "+e);
        }
        
        return fileString;
    }
}
