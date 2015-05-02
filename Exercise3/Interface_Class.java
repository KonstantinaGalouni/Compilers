import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Interface_Class {
    //names of classes
    public Set<String> allClasses = new HashSet<>();    
    
    //names and return types, names and types of arguments of every method in every class
    public Map<String, Map<String, List<String>>> fieldsOfClass = new HashMap();
    
    //names of fields (including the superclasses') in every class
    public Map<String, List<String>> namesOfFieldsInClass = new HashMap();
    //names of fields (including the superclasses') in a class
    List namesOfFields;
    //names of methods (including the superclasses') in every class
    public Map<String, List<String>> namesOfMethodsInClass = new HashMap();
    //names of methods (including the superclasses') in a class
    List namesOfMethods;
    

    //names of classes and names of theis superclasses
    public Map<String, String> superType = new HashMap();
    
    //names and return types, names and types of arguments of every method
    Map<String, List<String>> typeOfFields1;
    //return type, names and types of arguments in a method
    List<String> list1;
    
    //names and types of fields in every class
    public Map<String, Map<String, String>> fieldsOfClass_vars = new HashMap();
    //names and types of fields
    Map<String, String> typeOfVars;

    //names of every method and their variables' names and types in every class
    public Map<String, Map<String, Map<String, String>>> methodsOfClass = new HashMap();
    
    //names of every method and their variables' names and types
    Map<String, Map<String, String>> fieldsOfMethods;
    //names and types of variables in a method
    Map<String, String> typeOfFields2;

    //type of a field in a class
    String myString;
    //return type of a method and variables' names and types
    List<String> helper;
    //helpen variable - if true, this is the type of fiels in class - if false, this is the type of variable in method
    boolean bool=true;
    //if true we look for type
    boolean check=false;
    //if true we look for identifier
    boolean check2=false;
    //which class i am in
    public String current_class;
}
