import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Interface_Class {
    public Set<String> allClasses = new HashSet<>();
    
    public Map<String, Map<String, List<String>>> fieldsOfClass = new HashMap();

    public Map<String, String> superType = new HashMap();
    
    Map<String, List<String>> typeOfFields1;
    List<String> list1;
    
    public Map<String, Map<String, String>> fieldsOfClass_vars = new HashMap();
    Map<String, String> typeOfVars;

    public Map<String, Map<String, Map<String, String>>> methodsOfClass = new HashMap();
    
    Map<String, Map<String, String>> fieldsOfMethods;
    Map<String, String> typeOfFields2;

    String myString;
    List<String> helper;
    boolean bool=true;
    boolean check=false;
    boolean check2=false;
    public String current_class;
}
