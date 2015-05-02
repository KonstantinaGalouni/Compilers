import java.util.ArrayList;
import java.util.List;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class CheckVisitor extends GJDepthFirst<String, String>{
    public Interface_Class icl;
    public String classname;
    public List<String> myList;
    
    public void get_InterfaceClass(Interface_Class icl_param)
    {
        icl = icl_param;
    }
    
    public String visit(Goal g, String _)
    {
        //System.out.println("Goal");
        g.f0.accept(this, null);
        if(g.f1.present())
            g.f1.accept(this, null);
        return "";
    }
    
    public String visit(MainClass m, String _)
    {
        //System.out.println("MainClass");
        classname = m.f1.f0.toString();
        if(m.f15.present())
            m.f15.accept(this, "main");
        return "";
    }
    
    public String visit(AssignmentStatement n, String methodname) {
        //System.out.println("AssignmentStatement");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);

        if(left.equals("int") || left.equals("boolean") || left.equals("int[]"))
        {
            if(left.equals(right) == false)
            {
                System.out.println("CheckVisitor: AssignmentStatement");
                throw new SemException();
            }
        }
        else if(left.equals(right) == false && (right.equals("this") == false && classname.equals(left)))
        {
            String temp = right;
            while(icl.superType.containsKey(temp))
            {
                temp = icl.superType.get(temp);
                if(icl.superType.get(temp).equals(left))
                {
                    break;
                }
            }
            if(icl.superType.containsKey(temp) == false)
            {
                System.out.println("CheckVisitor: AssignmentStatement");
                throw new SemException();
            }
        }
        return right;
    }
    
    public String visit(ArrayAssignmentStatement n, String methodname) {
        //System.out.println("ArrayAssignmentStatement");
        
        String left = n.f0.accept(this, methodname);
        if(left.equals("int[]") == false)
        {
            System.out.println("CheckVisitor: ArrayAssignmentStatement");
            throw new SemException();
        }
        String in = n.f2.accept(this, methodname);
        if(in.equals("int") == false)
        {
            System.out.println("CheckVisitor: ArrayAssignmentStatement");
            throw new SemException();
        }
        String right = n.f5.accept(this, methodname);
        if(right.equals("int") == false)
        {
            System.out.println("CheckVisitor: ArrayAssignmentStatement");
            throw new SemException();
        }
        
        return "int[]";
    }
    
    public String visit(IfStatement n, String methodname) 
    {
        //System.out.println("IfStatement");
        
        String expr = n.f2.accept(this, methodname);
        if(expr.equals("boolean") == false)
        {
            System.out.println("CheckVisitor: IfStatement");
            throw new SemException();
        }
        n.f4.accept(this, methodname);
        
        n.f6.accept(this, methodname);
        return "boolean";
    }
    
    public String visit(WhileStatement n, String methodname) 
    {
        //System.out.println("WhileStatement");
        String expr = n.f2.accept(this, methodname);
        if(expr.equals("boolean") == false)
        {
            System.out.println("CheckVisitor: WhileStatement");
            throw new SemException();
        }
        n.f4.accept(this, methodname);
        return "boolean";
    }
    
    public String visit(PrintStatement n, String methodname) 
    {
        //System.out.println("PrintStatement");
        String expr = n.f2.accept(this, methodname);
        if(expr.equals("this"))
            expr = classname;
        if(expr.equals("boolean") == false && expr.equals("int") == false)
        {
            System.out.println("CheckVisitor: PrintStatement");
            throw new SemException();
        }
        return "boolean";
    }
    
    public String visit(AndExpression n, String methodname)
    {
        //System.out.println("AndExpression");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);
        if(left.equals("boolean") && right.equals("boolean"))
            return "boolean";
        else
        {
            System.out.println("CheckVisitor: AndExpression");
            throw new SemException();
        }
    }
    
    public String visit(CompareExpression n, String methodname)
    {
        //System.out.println("CompareExpression");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);
        if(left.equals("int") && right.equals("int"))
            return "boolean";
        else
        {
            System.out.println("CheckVisitor: CompareExpression");
            throw new SemException();
        }
    }
    
    public String visit(PlusExpression n, String methodname)
    {
        //System.out.println("PlusExpression");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);
        if(left.equals("int") && right.equals("int"))
            return "int";
        else
        {
            System.out.println("CheckVisitor: PlusExpression");
            throw new SemException();
        }
    }
    
    public String visit(MinusExpression n, String methodname)
    {
        //System.out.println("MinusExpression");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);
        if(left.equals("int") && right.equals("int"))
            return "int";
        else
        {
            System.out.println("CheckVisitor: MinusExpression");
            throw new SemException();
        }
    }
    
    public String visit(TimesExpression n, String methodname)
    {
        //System.out.println("TimesExpression");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);
        if(left.equals("int") && right.equals("int"))
            return "int";
        else
        {
            System.out.println("CheckVisitor: TimesExpression");
            throw new SemException();
        }
    }
    
    public String visit(ArrayLookup n, String methodname)
    {
        //System.out.println("ArrayLookup");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);
        if(left.equals("int[]") && right.equals("int"))
            return "int";
        else
        {
            System.out.println("CheckVisitor: ArrayLookup");
            throw new SemException();
        }
    }
    
    public String visit(ArrayLength n, String methodname)
    {
        //System.out.println("ArrayLength");
        String left = n.f0.accept(this, methodname);
        if(left.equals("int[]"))
            return "int";
        else
        {
            System.out.println("CheckVisitor: ArrayLength");
            throw new SemException();
        }
    }
    
    public String visit(MessageSend n, String methodname)
    {
        //System.out.println("MessageSend");
        String temp = null;// = myList.get(i);
        
        String left = n.f0.accept(this, methodname);

        if(icl.allClasses.contains(left) == false && left.equals("this") == false)
        {
            System.out.println("CheckVisitor: MessageSend");
            throw new SemException();
        }
        
        String right = n.f2.accept(this, methodname);

        //sthn klash left h this 8a koitaksw an yparxei h methodos right me ta swsta orismata - koitaw myList
        if(n.f4.present())
        {
            myList = new ArrayList<>();
            n.f4.accept(this, methodname);
        }
        else
        {
            myList = new ArrayList<>();
        }
        
        String which_class = left;
        if(left.equals("this"))
            which_class  = classname;

        if(icl.fieldsOfClass.containsKey(which_class))
        {
            if(icl.fieldsOfClass.get(which_class).containsKey(right))
            {
                if(icl.fieldsOfClass.get(which_class).get(right).size() == (myList.size()*2 + 1))
                {
                    for(int i=0; i<myList.size(); i++)
                    {
                        if(icl.fieldsOfClass.get(which_class).get(right).get(i*2+1).equals(myList.get(i)) == false)
                        {
                            temp = myList.get(i);
                            while(icl.superType.containsKey(temp))
                            {
                                temp = icl.superType.get(temp);
                                if(icl.fieldsOfClass.get(which_class).get(right).get(i*2+1).equals(temp))
                                    break;
                            }
                            if(icl.fieldsOfClass.get(which_class).get(right).get(i*2+1).equals(temp) == false)
                            {
                                System.out.println("CheckVisitor: MessageSend");
                                throw new SemException();
                            }
                        }
                     }
                 }
                else
                {
                    System.out.println("CheckVisitor: MessageSend");
                    throw new SemException();
                }
            }
        }
        //an den yparxei koitaw stis yperclasses tous
        if(icl.fieldsOfClass.containsKey(which_class) == false || 
                icl.fieldsOfClass.containsKey(which_class) == false)
        {
            while(icl.superType.containsKey(which_class))
            {
                which_class = icl.superType.get(which_class);
                if(icl.fieldsOfClass.containsKey(which_class))
                {
                    if(icl.fieldsOfClass.get(which_class).containsKey(right))
                    {
                        if(icl.fieldsOfClass.get(which_class).get(right).size() == (myList.size()*2 + 1))
                        {
                            for(int i=0; i<myList.size(); i++)
                            {
                                if(icl.fieldsOfClass.get(which_class).get(right).get(i*2+1).equals(myList.get(i)) == false)
                                {
                                    temp = myList.get(i);
                                    while(icl.superType.containsKey(temp))
                                    {
                                        temp = icl.superType.get(temp);
                                        if(icl.fieldsOfClass.get(which_class).get(right).get(i*2+1).equals(temp))
                                            break;
                                    }
                                    if(icl.fieldsOfClass.get(which_class).get(right).get(i*2+1).equals(temp) == false)
                                    {
                                        System.out.println("CheckVisitor: MessageSend");
                                        throw new SemException();
                                    }
                                }
                                else
                                    break;
                            }
                        }
                        else
                        {
                            System.out.println("CheckVisitor: MessageSend");
                            throw new SemException();
                        }
                    }
                }
            }
        }
        
        //an den yparxei genika error!!!
        if(icl.fieldsOfClass.containsKey(which_class) == false || 
                icl.fieldsOfClass.containsKey(which_class) == false)
        {
            System.out.println("CheckVisitor: MessageSend");
            throw new SemException();
        }
        return icl.fieldsOfClass.get(which_class).get(right).get(0);
    }
    
    public String visit(ExpressionList n, String methodname)
    {
        //System.out.println("ExpressionList");
        String return_type = n.f0.f0.accept(this, methodname);
        if(return_type.equals("this"))
            return_type = classname;
        myList.add(return_type);
        if(n.f1.f0.present())
            n.f1.f0.accept(this, methodname);
        return return_type;
    }
    
    public String visit(ExpressionTerm n, String methodname)
    {
        //System.out.println("ExpressionTerm");
        String return_type =n.f1.accept(this, methodname);
        if(return_type.equals("this"))
            return_type = classname;
        myList.add(return_type);
        return return_type;
    }
    
    public String visit(IntegerLiteral n, String methodname)
    {
        //System.out.println("IntegerLiteral");
        return "int";
    }
    
    public String visit(TrueLiteral n, String methodname)
    {
        //System.out.println("TrueLiteral");
        return "boolean";
    }
    
    public String visit(FalseLiteral n, String methodname)
    {
        //System.out.println("FalseLiteral");
        return "boolean";
    }
    
    public String visit(Identifier n, String methodname)
    {
        //System.out.println("Identifier");
        //prwta prwta koitaw an to n.f0.String() yparxei mesa sth me8odo pou eimai
        //meta prepei na koitaksw ta orismata ths me8odou
        //8a koitaksw sthn klash pou eimai an sta fields_vars exw to n.f0.toString()
        //an den to exw 8a koitaksw stis superclasses 
              
        if(icl.methodsOfClass.containsKey(classname))
        {
            if(icl.methodsOfClass.get(classname).containsKey(methodname))
            {
                if(icl.methodsOfClass.get(classname).get(methodname).containsKey(n.f0.toString()))
                {
                    return icl.methodsOfClass.get(classname).get(methodname).get(n.f0.toString());
                }
            }
        }
        
        if(icl.fieldsOfClass.containsKey(classname))
        {
            if(icl.fieldsOfClass.get(classname).containsKey(methodname))
            {
                if(icl.fieldsOfClass.get(classname).get(methodname).size() > 1)
                {
                    for(int i=2; i<icl.fieldsOfClass.get(classname).get(methodname).size(); i=i+2)
                    {
                        if(icl.fieldsOfClass.get(classname).get(methodname).get(i).equals(n.f0.toString()))
                            return icl.fieldsOfClass.get(classname).get(methodname).get(i-1);
                    }
                }
            }
        }
        
        if(icl.fieldsOfClass_vars.containsKey(classname))
        {
            
            if(icl.fieldsOfClass_vars.get(classname).containsKey(n.f0.toString()))
            {
                return icl.fieldsOfClass_vars.get(classname).get(n.f0.toString());
            }
        }
        //se auth th fash eite h class den eixe fields, eite to n.f0.toString() den yphrxe sta fields
        //eite den to phre ws orisma h methodos, eite den htan sta fields ths methodou
        //an to eixa brei 8a ekane return kai edw de 8a erxotan pote
        //prepei na koitaksw me th seira tis superclasses mexri na to brw
        //an den to brw exw error!!!
        String temp = classname;
        while(icl.superType.containsKey(temp))
        {
            temp = icl.superType.get(temp);
            if(icl.fieldsOfClass_vars.containsKey(temp))
            {
                if(icl.fieldsOfClass_vars.get(temp).containsKey(n.f0.toString()))
                {
                    return icl.fieldsOfClass_vars.get(temp).get(n.f0.toString());
                }
            }
        }
        //den to brhke pou8ena ara apla epistrefw to onoma n.f0.toString()
        //gia na to diaxeiristei o apo panw
      
        return n.f0.toString();
    }
    
    public String visit(ThisExpression n, String methodname)
    {
        //System.out.println("ThisExpression");
        return "this";
    }
    
    public String visit(ArrayAllocationExpression n, String methodname)
    {
        //System.out.println("ArrayAllocationExpression");
        if(n.f3.accept(this, methodname).equals("int") == false)
        {
            System.out.println("CheckVisitor: ArrayAllocationExpression");
            throw new SemException();
        }
        return "int[]";
    }
    
    public String visit(AllocationExpression n, String methodname)
    {
        //System.out.println("AllocationExpression");
        String return_class = n.f1.accept(this, methodname);
        if(icl.allClasses.contains(return_class) == false)
        {
            System.out.println("CheckVisitor: AllocationExpression");
            throw new SemException();
        }
        return return_class;
    }
    
    public String visit(NotExpression n, String methodname)
    {
        return n.f1.accept(this, methodname);
    }
    
    public String visit(BracketExpression n, String methodname)
    {
        String return_type = n.f1.accept(this, methodname);
        if(return_type.equals("this"))
            return_type = classname;
        return return_type;
    }
    
    public String visit(ClassDeclaration c, String _)
    {
        //System.out.println("ClassDeclaration");
        classname = c.f1.f0.toString();
        if(c.f4.present())
            c.f4.accept(this, null);
        return "";
    }
    
    public String visit(ClassExtendsDeclaration c, String _)
    {
        //System.out.println("ClassExtendsDeclaration");
        classname = c.f1.f0.toString();
        if(c.f6.present())
            c.f6.accept(this, null);
        return "";
    }
    
    public String visit(MethodDeclaration m, String _) 
    {   
        if(m.f8.present())
            m.f8.accept(this, m.f2.f0.toString());
        String return_type = m.f10.accept(this, m.f2.f0.toString());
        if(return_type.equals("this"))
            return_type = classname;
        if(return_type.equals(icl.fieldsOfClass.get(classname).get(m.f2.f0.toString()).get(0)))
        {
            return icl.fieldsOfClass.get(classname).get(m.f2.f0.toString()).get(0);
        }
        else
        {
            System.out.println("CheckVisitor: MethodDeclaration");
            throw new SemException();
        }
    }
}
