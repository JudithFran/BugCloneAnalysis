/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcmt;

/**
 *
 * @author Judith
 */
public class InputParameters {
    
    public static String systemName = "", pathRegular = "", pathMicro = "", programmingLanguage = "";
    public static int lastRevision;
    
    public void setParameters(String sysName){
        try{
        
        if(sysName.equals("Ctags")){
                systemName = sysName;
                lastRevision = 774;
                pathRegular = "C:/RegularClones/Ctags/Repository/version-";
                pathMicro = "C:/MicroClones/Systems/Ctags/Repository/version-";
                programmingLanguage = "C";
        }
        else if(sysName.equals("Brlcad")){
                systemName = sysName;
                lastRevision = 735;
                pathRegular = "C:/RegularClones/Brlcad/Repository/version-";
                pathMicro = "C:/MicroClones/Systems/Brlcad/Repository/version-";
                programmingLanguage = "C";
        }
        else if(sysName.equals("MonoOSC")){
                systemName = sysName;
                lastRevision = 315;
                pathRegular = "C:/RegularClones/Monoosc/Repository/version-";
                pathMicro = "C:/MicroClones/Systems/Monoosc/Repository/version-";
                programmingLanguage = "C#";
        }
        else if(sysName.equals("Freecol")){
                systemName = sysName;
                lastRevision = 1950;
                pathRegular = "C:/RegularClones/Freecol/Repository/version-";
                pathMicro = "C:/MicroClones/Systems/Freecol/Repository/version-";
                programmingLanguage = "Java";
        }
        else if(sysName.equals("Carol")){
                systemName = sysName;
                lastRevision = 1700;
                pathRegular = "C:/RegularClones/Carol/Repository/version-";
                pathMicro = "C:/MicroClones/Systems/Carol/Repository/version-";
                programmingLanguage = "Java";
        }
        else if(sysName.equals("Jabref")){
                systemName = sysName;
                lastRevision = 1545;
                pathRegular = "C:/RegularClones/Jabref/Repository/version-";
                pathMicro = "C:/MicroClones/Systems/Jabref/Repository/version-";
                programmingLanguage = "Java";
        }
        else if(sysName.equals("Select")){
            System.out.println("Please select a valid subject system.");
        }    
        
        
        System.out.println("This is inside setParameters systemName = " + systemName + " Programming language = " + programmingLanguage + " Regular path = " + pathRegular 
            + " Micro path = " + pathMicro + " Last revision = " + lastRevision);
        
        }catch(Exception e){
            System.out.println("error in method setParameters = " + e);
            e.printStackTrace();
        }
    }
    
    public void getParameters(){
        try{
            System.out.println("Inside getParameters, System name = " + systemName);
        }catch(Exception e){
            System.out.println("error in method getParameters = " + e);
            e.printStackTrace();
        }
    }
    
}
