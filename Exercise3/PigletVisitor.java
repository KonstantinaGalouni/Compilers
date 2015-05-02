import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class PigletVisitor extends GJDepthFirst<String, String>{
    public Interface_Class icl;
    public String classname;
    public List<String> myList;
    
    String return_type;
    
    PrintWriter out = null;
    
    static int label_counter = 0;
    static int temp_counter = 0;
    
    String my_temp;
    //gia ka8e klash exw map apo to temp pou antistoixei sto field(/variable)
    Map<String, Map<String, String>> class_Map = new HashMap();
    Map<String, String> class_map;
    
    //gia ka8e me8odo exw map apo to temp pou antistoixei sto variable
    Map<String, Map<String, String>> method_Map = new HashMap();
    Map<String, String> method_map;
    
    boolean field = false;
    boolean hload_or_move = false;
    boolean hstore_or_hload = false;
    boolean assignment = false;
    boolean call = false;
    String last_instruction=null;
    String my_left = null;
    String my_right = null;
    boolean arrayallocation = false;
    
    PigletVisitor(PrintWriter stream){
        this.out = stream;
    }
    
    public void get_InterfaceClass(Interface_Class icl_param)
    {
        icl = icl_param;
    }
    
    String get_Label()
    {
        label_counter++;
        return "L"+label_counter;
    }
    
    String get_Temp()
    {
        temp_counter++;
        return "TEMP "+temp_counter;
    }
    
    public String visit(Goal g, String _)
    {
        //System.out.println("Goal");
        g.f0.accept(this, null);
        if(g.f1.present())
            g.f1.accept(this, null);
        return null;
    }
    
    public String visit(MainClass m, String _)
    {
        //System.out.println("MainClass");
        class_map = new HashMap();
        method_map = new HashMap();
        
        out.print("MAIN \n");
        icl.current_class = m.f1.f0.toString();
        classname = m.f1.f0.toString();
        if(m.f15.present())
            m.f15.accept(this, "main");
        out.print("END \n");
        last_instruction = "main";
        return null;
    }
    
    public String visit(AssignmentStatement n, String methodname) {
        //System.out.println("AssignmentStatement");
        hstore_or_hload = true;
        assignment = true;
        String left = n.f0.accept(this, methodname);
        if(field == true)
        {
            out.print("HSTORE ");
        }
        else
        {
            out.print("MOVE ");
        }
        out.print(left+" ");
        hstore_or_hload = false;
        String right = n.f2.accept(this, methodname);
        out.print("\n");
        
        my_temp = left;
        return_type = right;
        assignment = false;
        last_instruction = "AssignmentStatement";
        return null;
    }
    
    public String visit(ArrayAssignmentStatement n, String methodname) {
        //System.out.println("ArrayAssignmentStatement");
        
        String temp = get_Temp();
        out.print("HSTORE PLUS\nBEGIN\nMOVE "+temp+"\n");
        String left = n.f0.accept(this, methodname);
        if(left!=null && !left.equals("allocation expression"))
            out.print(left);
        out.print(" RETURN "+temp+"\nEND\n");
        
        String temp1 = get_Temp();
        out.print("BEGIN\nMOVE "+temp1+" ");
        String in = n.f2.accept(this, methodname);
        if(in!=null && !in.equals("allocation expression"))
            out.print(in);
        String temp3 = get_Temp();  //size of the table
        out.print("\nHLOAD "+temp3+" "+temp+" 0\n");
        String Label1 = get_Label();
        out.print("\nCJUMP LT MINUS "+temp3+" 1 "+temp1+" "+Label1+"\nERROR\n");
        String Label2 = get_Label();
        out.print(Label1+" CJUMP LT "+temp1+" 0 "+" "+Label2+"\nERROR\n"+Label2+" NOOP\n");
        out.print("MOVE "+temp1+" PLUS 4 TIMES "+temp1+" 4\n");
        out.print("RETURN "+temp1+"\nEND\n0\n");
        
        n.f5.accept(this, methodname);
        out.print("\n");
        
        return_type = "int[]";
        last_instruction = "ArrayAssignmentStatement";
        return null;
    }

    public String visit(IfStatement n, String methodname) 
    {
        //System.out.println("IfStatement");
        
        String else_Label = get_Label();
        String end_Label = get_Label();
        
        out.print("CJUMP ");
        n.f2.accept(this, methodname);  //8a grapsei sto arxeio to expression
        out.print(else_Label+"\n");
        String stm1 = n.f4.accept(this, methodname);
        if(stm1 != null)
            out.print(stm1 + "\n"); //8a grapsei sto arxeio to statement
        out.print("JUMP "+end_Label+"\n");
        out.print(else_Label+" ");
        String stm2 = n.f6.accept(this, methodname);
        if(stm2 != null)
            out.print(stm2 + "\n"); //8a grapsei sto arxeio to statement
        out.print(end_Label+" NOOP\n");
        
        return_type = "boolean";
        last_instruction = "IfStatement";
        return null;
    }

    public String visit(WhileStatement n, String methodname) 
    {
        //System.out.println("WhileStatement");
        
        String End_Label = get_Label();
        String While_Label = get_Label();
        
        out.print(While_Label+" CJUMP\n");
        n.f2.accept(this, methodname);  //8a grapsei sto arxeio to expression
        out.print(End_Label+"\n");
        String stm = n.f4.accept(this, methodname);
        if(stm != null)
            out.print(stm + "\n");  //8a grapsei sto arxeio to statement
        out.print("JUMP "+While_Label+"\n");
        out.print(End_Label+" NOOP\n");
        
        return_type = "boolean";
        last_instruction = "WhileStatement";
        return null;
    }

    public String visit(PrintStatement n, String methodname) 
    {
        //System.out.println("PrintStatement");
        out.print("PRINT ");
        String stm = n.f2.accept(this, methodname);  
        if(stm != null)
            out.print(stm + "\n");  //8a grapsei sto arxeio to statement

        return_type = "boolean";
        last_instruction = "PrintStatement";
        return null;
    }
    
    public String visit(AndExpression n, String methodname)
    {
        //System.out.println("AndExpression");
        hload_or_move = false;
        
        String end_Label = get_Label();
        String result = get_Temp();
        
        out.print("BEGIN\n");
        out.print("MOVE "+result+" 0\n");
        out.print("CJUMP ");
        n.f0.accept(this, methodname);
        out.print(end_Label+"\n");
        out.print("CJUMP ");
        n.f2.accept(this, methodname);
        out.print(end_Label+"\n");
        out.print("MOVE "+result+" 1\n");
        out.print(end_Label+" NOOP\n");
        out.print("RETURN\n");
        out.print(result+"\n");
        out.print("END\n");
        
        return_type = "boolean";
        last_instruction = "AndExpression";
        return null;
    }
    
    public String visit(CompareExpression n, String methodname)
    {
        //System.out.println("CompareExpression");
        hload_or_move = false;
        
        out.print("LT ");
        String left =n.f0.accept(this, methodname);
        if(left!=null)
            out.print(left+" ");
        String right = n.f2.accept(this, methodname);
        if(right!=null)
            out.print(right+" ");
        
        return_type = "boolean";
        last_instruction = "CompareExpression";
        return null;
    }

    public String visit(PlusExpression n, String methodname)
    {
        //System.out.println("PlusExpression");
        hload_or_move = false;
        
        out.print("PLUS ");
        
        String left =n.f0.accept(this, methodname);
        if(left!=null)
            out.print(left+" ");
        String right = n.f2.accept(this, methodname);
        if(right!=null)
            out.print(right+" ");
                
        return_type = "int";
        last_instruction = "PlusExpression";
        return null;
    }

    public String visit(MinusExpression n, String methodname)
    {
        //System.out.println("MinusExpression");
        hload_or_move = false;
        
        out.print("MINUS ");
        String left =n.f0.accept(this, methodname);
        if(left!=null)
            out.print(left+" ");
        String right = n.f2.accept(this, methodname);
        if(right!=null)
            out.print(right+" ");
        
        return_type = "int";
        last_instruction = "MinusExpression";
        return null;
    }

    public String visit(TimesExpression n, String methodname)
    {
        //System.out.println("TimesExpression");
        hload_or_move = false;
        
        out.print("TIMES ");
        String left =n.f0.accept(this, methodname);
        if(left!=null)
            out.print(left+" ");
        String right = n.f2.accept(this, methodname);
        if(right!=null)
            out.print(right+" ");
        
        return_type = "int";
        last_instruction = "TimesExpression";
        return null;
    }

    public String visit(ArrayLookup n, String methodname)
    {
        //System.out.println("ArrayLookup");
        hload_or_move = false;

        String temp = get_Temp();
        out.print("BEGIN\nHLOAD "+temp+" PLUS\n");
        
        String temp1 = get_Temp();
        out.print("BEGIN MOVE "+temp1+"\n");
        String left = n.f0.accept(this, methodname);
        if(left!=null && !left.equals("allocation expression"))
            out.print(left);
        out.print(" RETURN "+temp1+"\nEND\n");
        
        String temp2 = get_Temp();
        out.print("BEGIN\nMOVE "+temp2+" ");
        String in = n.f2.accept(this, methodname);
        if(in!=null && !in.equals("allocation expression"))
            out.print(in);
        String temp3 = get_Temp();  //size of the table
        out.print("\nHLOAD "+temp3+" "+temp1+" 0\n");
        String Label1 = get_Label();
        out.print("\nCJUMP LT MINUS "+temp3+" 1 "+temp2+" "+Label1+"\nERROR\n");
        String Label2 = get_Label();
        out.print(Label1+" CJUMP LT "+temp2+" 0 "+" "+Label2+"\nERROR\n"+Label2+" NOOP\n");
        out.print("MOVE "+temp2+" PLUS 4 TIMES "+temp2+" 4\n");
        out.print("RETURN "+temp2+"\nEND\n0\n");
        
        out.print("RETURN "+temp+"\nEND\n");
        
        return_type = "int";
        last_instruction = "ArrayLookup";
        return null;
    }

    public String visit(ArrayLength n, String methodname)
    {
        //System.out.println("ArrayLength");
        
        hload_or_move = false;
        String temp = get_Temp();
        out.print("BEGIN\nHLOAD "+temp+" ");
        String left = n.f0.accept(this, methodname);
        if(left!=null && !left.equals("allocation expression"))
            out.print(left);
        out.print("\n0\nRETURN "+temp+"\nEND\n");
              
        return_type = "int";
        last_instruction = "ArrayLength";
        return null;
    }

    public String visit(MessageSend n, String methodname)
    {
        //System.out.println("MessageSend");
        hload_or_move = false;
        if(call == true)
        {
            String t = get_Temp();
            out.print("MOVE "+t+" ");
            my_right = t;
        }
        out.print("CALL\nBEGIN\n");
        
        call = true;
        String left = n.f0.accept(this, methodname);
        call = false;
        
        String which_class = return_type;
        if(which_class.equals("this"))
            which_class = classname;
        
        String temp2 = get_Temp();
        String temp3 = get_Temp();
        
        String in_move="TEMP 0";

        String right = n.f2.accept(this, methodname);
        return_type =  icl.fieldsOfClass.get(which_class).get(return_type).get(0);
        
        //an den einai new(allocationexpression) auto prin thn teleia
        if(left!=null && !left.equals("allocation expression") && my_left==null)
        {
            in_move = left;
            
            if(!last_instruction.equals("MessageSend"))
            {
                my_left = in_move;
            }
            String temp1 = get_Temp();
            out.print("MOVE "+temp1+" ");
            out.print(" "+in_move+"\n");
                
            out.print("HLOAD "+temp2+" "+temp1+" 0\n");
            out.print("HLOAD "+temp3+" "+temp2+" "+ right+" \n");
             in_move = temp1;
        }
        else
        {
            in_move = my_temp;
            if(my_right!=null)
            {
                in_move = my_right;
                my_right = null;
            }
            //in_move = temp3 apo to allocationexpression
            
            out.print("HLOAD "+temp2+" "+in_move+" 0\n");
            out.print("HLOAD "+temp3+" "+temp2+" "+ right+" \n");
        }

        out.print("RETURN\n"+temp3+"\n");
        out.print("END\n");
        out.print("("+in_move+" ");
        //other arguments        
        
        my_left = null;
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
        
        for(int i=0; i<myList.size(); i++)
            if(myList.get(i).equals("this"))
                myList.set(i, in_move);
        
        out.print(")\n");

        last_instruction = "MessageSend";
        return null;
    }
    
    public String visit(Clause c, String methodname)
    {
        //System.out.println("Clause");
        if(hload_or_move == true )
        {   
            hstore_or_hload = true;
        }
        String temp = c.f0.accept(this, methodname);
        if(temp == null)
            return null;
        String temp1 = get_Temp();
        if(hload_or_move == true)
        {
            out.print("BEGIN\n");
            if(field == true)
            {
                out.print("HLOAD "+temp1+" "+temp+"\n");
                hstore_or_hload = false;
            }
            else
            {
                out.print("MOVE "+temp1+" "+temp+"\n");
            }
            out.print("RETURN "+temp1+"\nEND\n");
        }
        else if(!temp.equals("allocation expression"))
            out.print(temp+" ");

        last_instruction = "Clause";
        return null;
    }
    
    public String visit(ExpressionList n, String methodname)
    {
        //System.out.println("ExpressionList");
        
        n.f0.f0.accept(this, methodname);
        if(return_type.equals("this"))
            return_type = classname;
        if(n.f1.f0.present())
            n.f1.f0.accept(this, methodname);
        
        last_instruction = "ExpressionList";
        return null;
    }

    public String visit(ExpressionTerm n, String methodname)
    {
        //System.out.println("ExpressionTerm");
        n.f1.accept(this, methodname);
        if(return_type.equals("this"))
            return_type = classname;
        
        last_instruction = "ExpressionTerm";
        return null;
    }

    public String visit(IntegerLiteral n, String methodname)
    {
        //System.out.println("IntegerLiteral");
        hload_or_move = false; 
        return_type = "int";
        return n.f0.toString();
    }

    public String visit(TrueLiteral n, String methodname)
    {
        //System.out.println("TrueLiteral");
        hload_or_move = false;
        return_type = "boolean";
        return "1";
    }

    public String visit(FalseLiteral n, String methodname)
    {
        //System.out.println("FalseLiteral");
        hload_or_move = false;
        return_type = "boolean";
        return "0";
    }

    public String visit(Identifier n, String methodname)
    {
        field = false;
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
                    return_type = icl.methodsOfClass.get(classname).get(methodname).get(n.f0.toString());
                    
                    //8elw to TEMP pou antistoixei sth metablhth..p.x. an identifier: a kai a -> TEMP 2 8elw na epistrepsw TEMP 2
                    if(method_Map!=null && method_Map.containsKey(methodname) 
                            && method_Map.get(methodname).containsKey(n.f0.toString()))
                        return method_Map.get(methodname).get(n.f0.toString());
                    else
                    {
                        String temp = get_Temp();
                        method_map.put(n.f0.toString(), temp);
                        method_Map.put(methodname, method_map);
                        return temp;
                    }                   
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
                        {
                            return_type = icl.fieldsOfClass.get(classname).get(methodname).get(i-1);
                            return "TEMP "+i/2;
                        }
                    }
                }
            }
        }
        
        if(icl.fieldsOfClass_vars.containsKey(classname))
        {
            
            if(icl.fieldsOfClass_vars.get(classname).containsKey(n.f0.toString()))
            {
                return_type = icl.fieldsOfClass_vars.get(classname).get(n.f0.toString());
                field = true;
                int offset = (icl.namesOfFieldsInClass.get(classname).indexOf(classname+"_"+n.f0.toString())+1)*4;
                if(hstore_or_hload == false)
                {
                    String ret_str = get_Temp();
                    return "BEGIN\nHLOAD "+ret_str+" "+"TEMP 0 "+offset+"\n"+"RETURN "+ret_str+"\nEND ";
                }
                else
                {
                    hstore_or_hload = false;
                    return "TEMP 0 "+offset;
                }
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
                    return_type = icl.fieldsOfClass_vars.get(temp).get(n.f0.toString());
                    field = true;
                    int offset = (icl.namesOfFieldsInClass.get(classname).indexOf(temp+"_"+n.f0.toString())+1)*4;

                    if(hstore_or_hload == false)
                    {
                        String ret_str = get_Temp();
                        hstore_or_hload = false;
                        return "BEGIN\nHLOAD "+ret_str+" "+"TEMP 0 "+offset+"\n"+"RETURN "+ret_str+"\nEND";
                    }
                    else
                    {
                        hstore_or_hload = false;
                        return "TEMP 0 "+offset;
                    }
                }
            }
        }
        //den to brhke pou8ena ara apla epistrefw to onoma n.f0.toString()
        //gia na to diaxeiristei o apo panw
      
        return_type = n.f0.toString();
        
        //8a epistrepsw sthn messagesend to swsto offset - einai to right
        String name;
        Iterator<String> iterator = icl.allClasses.iterator();
        while(iterator.hasNext()) {
            name = iterator.next();
            if(icl.namesOfMethodsInClass!= null && icl.namesOfMethodsInClass.containsKey(name)
                && icl.namesOfMethodsInClass.get(name).contains(name+"_"+n.f0.toString()))
            {
                return String.valueOf(icl.namesOfMethodsInClass.get(name).indexOf(name+"_"+n.f0.toString())*4);
            }
        }

        //einai to left apo th messagesend
        return n.f0.toString();
    }

    public String visit(ThisExpression n, String methodname)
    {
        //System.out.println("ThisExpression");
        hload_or_move = false;
        return_type = "this";
        return "TEMP 0";
    }
    
    public String visit(ArrayAllocationExpression n, String methodname)
    {
        String temp1 = get_Temp();
        String temp2 = get_Temp();
        
        out.print("BEGIN\nMOVE "+temp1+" HALLOCATE TIMES PLUS ");
        
        String temp3 = get_Temp();
        out.print("BEGIN\nMOVE "+temp3+" ");
        String in = n.f3.accept(this, methodname);
        if(in!=null)    //e.g. integerliteral
            out.print(in);
        out.print("\nRETURN "+temp3+"\nEND\n");
        
        out.print(" 1 4\nMOVE "+ temp2+" 4\n");
        String Label1 = get_Label();
        String Label2 = get_Label();
        out.print(Label1+" CJUMP LT "+temp2+" TIMES PLUS "+temp3+" 1 4 "+Label2+"\n");
        out.print("HSTORE PLUS "+temp1+" "+temp2+" 0 0\n");
        out.print("MOVE "+temp2+" PLUS "+temp2+" 4\n"+"JUMP "+Label1+"\n");
        out.print(Label2+" HSTORE "+temp1+" 0 "+temp3+"\n");
        out.print("RETURN "+temp1+"\nEND\n");
        hload_or_move = false;
        
        return_type = "int[]";
        last_instruction = "ArrayAllocationExpression";
        return null;
    }
    
    public String visit(AllocationExpression n, String methodname)
    {
        //System.out.println("AllocationExpression");
        hload_or_move = false;
        
        n.f1.accept(this, methodname);
        if(assignment == false)
        {
            String temp3 = get_Temp();
            out.print("MOVE "+temp3+"\n");
            my_temp = temp3;
        }
        assignment = false;
                
        out.print("BEGIN \n");
        String temp1 = get_Temp();
        String temp2 = get_Temp();

        int position1=0;
        int position2=4;
        
        out.print("MOVE " + temp1 + " HALLOCATE " + icl.namesOfMethodsInClass.get(return_type).size()*4 + " \n");
        out.print("MOVE " + temp2 + " HALLOCATE " + (icl.namesOfFieldsInClass.get(return_type).size() +1)*4 + " \n");

        for(int i=0; i<icl.namesOfMethodsInClass.get(return_type).size(); i++)
        {
            out.print("HSTORE "+temp1+ " "+position1+" "+icl.namesOfMethodsInClass.get(return_type).get(i)+"\n");
            position1 = position1+4;
        }

        for(int i=0; i<icl.namesOfFieldsInClass.get(return_type).size(); i++)
        {
            out.print("HSTORE "+temp2+ " "+position2+" 0\n");
            position2 = position2+4;
        }
        
        out.print("HSTORE "+temp2+" 0 "+temp1+"\n");
        //"iterator" sto icl.namesInClass.get(icl.current_class), meta gia ka8e ena koitaw an yparxei eite sto 
        //icl.fieldsOfClass_vars.get(icl.current_class) eite sto icl.fieldsOfClass.get(icl.current_class)
        //antistoixa to bazw mesa ston virtual table sth 8esh pou prepei me HSTORE
        
        out.print("RETURN \n"+temp2+"\n"+"END\n");

        last_instruction = "AllocationExpression";
        return "allocation expression";
    }

    public String visit(NotExpression n, String methodname)
    {
        //System.out.println("NotExpression");
        out.print("MINUS 1 ");
        String t = n.f1.accept(this, methodname);
        last_instruction = "NotExpression";
        return t;
    }

    public String visit(BracketExpression n, String methodname)
    {
        //System.out.println("BracketExpression");
        String ret = n.f1.accept(this, methodname);
        if(return_type.equals("this"))
            return_type = classname;
        last_instruction = "BracketExpression";
        return ret;
    }
    
    public String visit(ClassDeclaration c, String _)
    {
        //System.out.println("ClassDeclaration");
        class_map = new HashMap();

        icl.current_class = c.f1.f0.toString();
        classname = c.f1.f0.toString();
        if(c.f4.present())
            c.f4.accept(this, null);
        last_instruction = "ClassDeclaration";
        return null;
    }
    
    public String visit(ClassExtendsDeclaration c, String _)
    {
        //System.out.println("ClassExtendsDeclaration");
        class_map = new HashMap();
        
        icl.current_class = c.f1.f0.toString();
        classname = c.f1.f0.toString();
        if(c.f6.present())
            c.f6.accept(this, null);
        last_instruction = "ClassExtendsDeclaration";
        return null;
    }
    
    public String visit(MethodDeclaration m, String _) 
    {   
        hload_or_move = false;
        method_map = new HashMap();
        
        label_counter = 0;
        temp_counter = (icl.fieldsOfClass.get(classname).get(m.f2.f0.toString()).size()-1)/2;
        out.print("\n"+icl.current_class+"_"+m.f2.f0.toString()+" ");
        
        out.print("[ "+ ((icl.fieldsOfClass.get(classname).get(m.f2.f0.toString()).size()-1)/2 + 1) +" ] \n");
        out.print("BEGIN \n");
        
        if(m.f8.present())
            m.f8.accept(this, m.f2.f0.toString());
        
        out.print("RETURN\n");
                
        hload_or_move = true;
        m.f10.accept(this, m.f2.f0.toString());

        if(return_type.equals("this"))
            return_type = classname;
        
        out.print("\nEND \n");
        
        return_type = icl.fieldsOfClass.get(classname).get(m.f2.f0.toString()).get(0);
        last_instruction = "MethodDeclaration";
        return null;
    }
}

