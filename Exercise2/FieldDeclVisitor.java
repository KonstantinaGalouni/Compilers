import java.util.Map;
import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.*;

public class FieldDeclVisitor extends GJDepthFirst<Map, Interface_Class>{
      
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
        
        return icl.fieldsOfClass;
    }
    
    public Map visit(VarDeclaration n, Interface_Class icl_param) {
        //System.out.println("VarDeclaration");
        
        List<String> list1 = new ArrayList<>();
        icl.helper = list1;
        
        icl_param.check = true;
        n.f0.f0.choice.accept(this, icl_param);
        icl_param.check = false;

        if(icl_param.typeOfVars!= null && icl_param.typeOfVars.containsKey(n.f1.f0.toString()))
        {
            System.out.println("FieldDeclVisitor: VarDeclaration");
            throw new SemException();
        }
        
        if(icl_param.bool == true)
        {
            icl_param.typeOfVars.put(n.f1.f0.toString(), icl_param.myString);
        }
        
        if(icl_param.typeOfFields2!= null && icl_param.typeOfFields2.containsKey(n.f1.f0.toString()))
        {
            System.out.println("FieldDeclVisitor: VarDeclaration");
            throw new SemException();
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
        
        if((icl_param.allClasses.contains(i.f0.toString()) == false) && (icl_param.check == true))
        {
            System.out.println("FielsDeclVisitor: Identifier");
            throw new SemException();
        }
        
        if(icl_param.check2 == true && icl_param.helper.contains(i.f0.toString()))
        {
            System.out.println("FielsDeclVisitor: Identifier");
            throw new SemException();
        }
        
        icl_param.helper.add(i.f0.toString());
        icl_param.myString = i.f0.toString();
        
        return null;
    }
    
    public Map visit(MethodDeclaration m, Interface_Class icl_param) {
        icl_param.bool = true;
        //System.out.println("MethodDeclaration");

        List<String> list1 = new ArrayList<>();
        icl_param.helper = list1;

        Map<String, String> typeOfFields2 = new HashMap();
        icl_param.typeOfFields2 = typeOfFields2;

        icl_param.check = true;
        m.f1.f0.choice.accept(this, icl_param);
        icl_param.check = false;
        
        if(m.f4.present())
        {
            m.f4.accept(this, icl_param);
        }

        if(icl_param.typeOfFields1.containsKey(m.f2.f0.toString()))
        {
            throw new SemException();
        }
        
        //prepei mia methodos me to idio onoma me mia methodo kapoias superclass na exei idio return type kai arguments
        String temp = icl_param.current_class;
        while(icl_param.superType.containsKey(temp))
        {
            temp = icl_param.superType.get(temp);
            if(icl_param.fieldsOfClass.containsKey(temp))
            {
                if(icl_param.fieldsOfClass.get(temp).containsKey(m.f2.f0.toString()))
                {
                    if(list1.size() != icl_param.fieldsOfClass.get(temp).get(m.f2.f0.toString()).size())
                        throw new SemException();
                    if(list1.get(0).equals(icl_param.fieldsOfClass.get(temp).get(m.f2.f0.toString()).get(0)) == false)
                        throw new SemException();
                    for(int i=1; i<list1.size(); i=i+2)
                        if(list1.get(i).equals(icl_param.fieldsOfClass.get(temp).get(m.f2.f0.toString()).get(i)) == false)
                            throw new SemException();
                    break;
                }
            }
        }
        
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
        
        return icl.fieldsOfClass;
    }    
    
    public void field_decl_checker(Set s)
    {
        icl.allClasses = s;
    }
}
