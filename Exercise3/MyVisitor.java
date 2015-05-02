import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class MyVisitor extends GJDepthFirst<Map, Interface_Class>{    
    public Interface_Class icl = new Interface_Class();
    
    public Map visit(Goal g, Interface_Class _)
    {   
        g.f0.accept(this, null);
        if(g.f1.present())
            g.f1.accept(this, null);
        return icl.fieldsOfClass;
    }
    
    public Map visit(MainClass m, Interface_Class _)
    {
        //System.out.println("MainClass");
        
        icl.current_class = m.f1.f0.toString();
        Map<String, List<String>> typeOfFields1 = new HashMap();
        icl.typeOfFields1 = typeOfFields1;
        List<String> list1 = new ArrayList<>();
        icl.list1 = list1;
        
        Map<String, Map<String, String>> fieldsOfMethods = new HashMap();
        icl.fieldsOfMethods = fieldsOfMethods;
        
        Map<String, String> typeOfFields2 = new HashMap();
        icl.typeOfFields2 = typeOfFields2;
        
        list1.add("void");
        typeOfFields1.put("main", list1);

        icl.fieldsOfClass.put(m.f1.f0.toString(), typeOfFields1);
        icl.bool = false;
        
        if(m.f14.present())
        {
            m.f14.accept(this, icl);
        }
        
        if(!typeOfFields2.isEmpty())
        {
            icl.fieldsOfMethods.put("main", icl.typeOfFields2);     
            icl.methodsOfClass.put(m.f1.f0.toString(), icl.fieldsOfMethods);
        }
        
        return icl.fieldsOfClass;
    }
    
    public Map visit(ClassDeclaration c, Interface_Class _)
    {
        //System.out.println("ClassDeclaration");
        
        icl.current_class = c.f1.f0.toString();
        icl.bool = true;

        Map<String, List<String>> typeOfFields1 = new HashMap();
        icl.typeOfFields1 = typeOfFields1;
        
        Map<String, Map<String, String>> fieldsOfMethods = new HashMap();
        icl.fieldsOfMethods = fieldsOfMethods;
        
        Map<String, String> typeOfVars = new HashMap();
        icl.typeOfVars = typeOfVars; 
        
        List<String> namesOfFields = new ArrayList<>();
        List<String> namesOfMethods = new ArrayList<>();
        if(icl.superType.containsKey(c.f1.f0.toString()))
        {
            if(icl.namesOfFieldsInClass!=null && icl.namesOfFieldsInClass.get(icl.superType.get(c.f1.f0.toString()))!=null)
            {
                namesOfFields = icl.namesOfFieldsInClass.get(icl.superType.get(c.f1.f0.toString()));
            }
            if(icl.namesOfMethodsInClass!=null && icl.namesOfMethodsInClass.get(icl.superType.get(c.f1.f0.toString()))!=null)
            {
                namesOfMethods = icl.namesOfMethodsInClass.get(icl.superType.get(c.f1.f0.toString()));
            }
        }
        icl.namesOfFields = namesOfFields;
        icl.namesOfMethods = namesOfMethods;
        
        if(c.f3.present())
        {
            c.f3.accept(this, icl);
        }
        if(c.f4.present())
        {
            icl.bool = true;
            c.f4.accept(this, icl);
        }
        
        if(!typeOfVars.isEmpty())
        {
            icl.fieldsOfClass_vars.put(c.f1.f0.toString(), typeOfVars);
        }
        
        if(!typeOfFields1.isEmpty())
        {
            icl.fieldsOfClass.put(c.f1.f0.toString(), typeOfFields1);  
        }       
        
        if(!fieldsOfMethods.isEmpty())
        {  
           icl.methodsOfClass.put(c.f1.f0.toString(), icl.fieldsOfMethods);  
        }
        
        icl.namesOfFieldsInClass.put(c.f1.f0.toString(), icl.namesOfFields);
        icl.namesOfMethodsInClass.put(c.f1.f0.toString(), icl.namesOfMethods);
        
        return icl.fieldsOfClass;
    }
    
    public Map visit(VarDeclaration n, Interface_Class icl_param) {
        //System.out.println("VarDeclaration");
        
        List<String> list1 = new ArrayList<>();
        icl.helper = list1;
        
        icl_param.check = true;
        n.f0.f0.choice.accept(this, icl_param);
        icl_param.check = false;
        
        if(icl_param.bool == true)
        {
            icl_param.typeOfVars.put(n.f1.f0.toString(), icl_param.myString);
            icl.namesOfFields.add(icl.current_class+"_"+n.f1.f0.toString());
        }

        if(icl_param.bool == false)
            icl_param.typeOfFields2.put(n.f1.f0.toString(), icl_param.myString);

        return icl_param.typeOfFields1;      
    }
    
    
    public Map visit(IntegerType n, Interface_Class icl_param) {
        //System.out.println("IntegerType");

        icl_param.helper.add(n.f0.toString());
        icl_param.myString = n.f0.toString();

        return null;
   }
    
    
    public Map visit(BooleanType n, Interface_Class icl_param) {
        //System.out.println("BooleanType");

        icl_param.helper.add(n.f0.toString());
        icl_param.myString = n.f0.toString();

        return null;
   }
    
    public Map visit(ArrayType n, Interface_Class icl_param) {
        //System.out.println("ArrayType");
   
        icl_param.helper.add(n.f0.toString()+n.f1.toString()+n.f2.toString());
        icl_param.myString = n.f0.toString()+n.f1.toString()+n.f2.toString();;
        
        return null;
    }
    
    public Map visit(Identifier i, Interface_Class icl_param)
    {
        //System.out.println("Identifier!!!");
        
        icl_param.helper.add(i.f0.toString());
        icl_param.myString = i.f0.toString();

        return null;
    }
    
    public Map visit(MethodDeclaration m, Interface_Class icl_param) {   
        Iterator<String> iterator = icl_param.allClasses.iterator();
        int where=0;
        while(iterator.hasNext()) {
            String check = iterator.next();
            if(icl.namesOfMethods.contains(check+"_"+m.f2.f0.toString()))
            {
                where = icl.namesOfMethods.indexOf(check+"_"+m.f2.f0.toString());
                icl.namesOfMethods.set(where, icl.current_class+"_"+m.f2.f0.toString());
                where = -1;
                break;
            }
        }
        if(where!=-1)
        {
            icl_param.namesOfMethods.add(icl.current_class+"_"+m.f2.f0.toString());
        }
        
        icl_param.bool = true;

        List<String> list1 = new ArrayList<>();
        icl_param.helper = list1;

        Map<String, String> typeOfFields2 = new HashMap();
        icl_param.typeOfFields2 = typeOfFields2;

        icl_param.check = true;
        m.f1.f0.choice.accept(this, icl_param);
        icl_param.check = false;
        
        if(m.f4.present())  //there are parameters
        {
            m.f4.accept(this, icl_param);
        }
        
        //prepei mia methodos me to idio onoma me mia methodo kapoias superclass na exei idio return type kai arguments
        
        if(icl_param.bool == true)
            icl_param.typeOfFields1.put(m.f2.f0.toString(), list1);
        
        if(m.f7.present())
        {
            icl_param.bool = false;
            m.f7.accept(this, icl_param);
        }
        
        if(!icl_param.typeOfFields2.isEmpty())
           icl.fieldsOfMethods.put(m.f2.f0.toString(), icl_param.typeOfFields2);        
        
        return icl_param.typeOfFields1;
        
    }
    
    public Map visit(FormalParameterList f, Interface_Class icl_param) {
        //System.out.println("FormalParameterList");
        
        icl_param.check = true;
        f.f0.f0.f0.choice.accept(this, icl_param);
        icl_param.check = false;
        icl_param.check2 = true;
        f.f0.f1.accept(this, icl_param);
        icl_param.check2 = false;

        if(f.f1.f0.present())
        {
            f.f1.f0.accept(this, icl_param);
        }
        
        return null;
    }
    
    public Map visit(FormalParameterTerm f, Interface_Class icl_param) {
        //System.out.println("FormalParameterTern");
        
        icl_param.check = true;
        f.f1.f0.accept(this, icl_param);
        icl_param.check = false;
        icl_param.check2 = true;
        f.f1.f1.accept(this, icl_param);
        icl_param.check2 = false;
        return null;
    }
    
    public Map visit(ClassExtendsDeclaration c, Interface_Class _)
    {
        //System.out.println("ClassExtendsDeclaration!!");
        
        icl.current_class = c.f1.f0.toString();
        icl.bool = true;

        Map<String, List<String>> typeOfFields1 = new HashMap();
        icl.typeOfFields1 = typeOfFields1;
        
        Map<String, Map<String, String>> fieldsOfMethods = new HashMap();
        icl.fieldsOfMethods = fieldsOfMethods;

        Map<String, String> typeOfVars = new HashMap();
        icl.typeOfVars = typeOfVars;
        
        icl.superType.put(c.f1.f0.toString(), c.f3.f0.toString());
        
        List<String> namesOfFields = new ArrayList<>(icl.namesOfFieldsInClass.get(icl.superType.get(c.f1.f0.toString())));
        List<String> namesOfMethods = new ArrayList<>(icl.namesOfMethodsInClass.get(icl.superType.get(c.f1.f0.toString())));
        
        icl.namesOfFields = namesOfFields;
        icl.namesOfMethods = namesOfMethods;
        
        if(c.f5.present())
        {
            c.f5.accept(this, icl);   
        }
      
        if(c.f6.present())
        {
            icl.bool = true;
            c.f6.accept(this, icl);
        }
        
        if(!typeOfVars.isEmpty())
        {
            icl.fieldsOfClass_vars.put(c.f1.f0.toString(), typeOfVars);
        }

        if(!typeOfFields1.isEmpty())
        {
            icl.fieldsOfClass.put(c.f1.f0.toString(), typeOfFields1);  
        }

        
        if(!icl.fieldsOfMethods.isEmpty())
        {
           icl.methodsOfClass.put(c.f1.f0.toString(), icl.fieldsOfMethods); 
        }
        
        icl.namesOfFieldsInClass.put(c.f1.f0.toString(), icl.namesOfFields);
        icl.namesOfMethodsInClass.put(c.f1.f0.toString(), icl.namesOfMethods);
        
        return icl.fieldsOfClass;
    }    
    
    public void field_decl_checker(Set s)
    {
        icl.allClasses = s;
    }
}