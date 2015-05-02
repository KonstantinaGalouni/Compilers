import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class PigletVisitor1 extends GJDepthFirst<String, String>{
    public Interface_Class icl;
    public String classname;
    public List<String> myList;
    
    String return_type;
    
    PrintWriter out = null;
    
    static int label_counter = 0;
    static int temp_counter = 0;
    
    PigletVisitor1(PrintWriter stream){
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
        //return "";
        return null;
    }
    
    public String visit(MainClass m, String _)
    {
        //System.out.println("MainClass");
        out.print("MAIN \n");
        icl.current_class = m.f1.f0.toString();
        classname = m.f1.f0.toString();
        if(m.f15.present())
            m.f15.accept(this, "main");
        out.print("END \n");
        //return "";
        return null;
    }
    
    public String visit(AssignmentStatement n, String methodname) {
        //System.out.println("AssignmentStatement");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);

        return_type = right;
        //return "";//right;  <------------------------------------
        return null;
    }
    
    public String visit(ArrayAssignmentStatement n, String methodname) {
        //System.out.println("ArrayAssignmentStatement");
        
        String left = n.f0.accept(this, methodname);
        String in = n.f2.accept(this, methodname);

        String right = n.f5.accept(this, methodname);
        
        return_type = "int[]";
        //return "";//"int[]";    <---------------------------------
        return null;
    }
    
    public String visit(IfStatement n, String methodname) 
    {
        //System.out.println("IfStatement");
        
        String expr = n.f2.accept(this, methodname);

        n.f4.accept(this, methodname);
        
        n.f6.accept(this, methodname);
        
        return_type = "boolean";
        //return "";//"boolean";  <--------------------------------
        return null;
    }
    
    public String visit(WhileStatement n, String methodname) 
    {
        //System.out.println("WhileStatement");
        String expr = n.f2.accept(this, methodname);

        n.f4.accept(this, methodname);
        
        return_type = "boolean";
        //return "";//"boolean";  <---------------------------------
        return null;
    }
    
    public String visit(PrintStatement n, String methodname) 
    {
        //System.out.println("PrintStatement");
        out.print("PRINT ");
        /*String expr = */n.f2.accept(this, methodname);    //<----------
        //-------------------->
//        if(expr.equals("this"))
//            expr = classname;
        //<--------------------

        return_type = "boolean";
        //return "";//"boolean";   <-----------------------------------
        return null;
    }
    
    public String visit(AndExpression n, String methodname)
    {
        //System.out.println("AndExpression");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);
        
        return_type = "boolean";
        //return "";//"boolean";   <-----------------------------------------
        return null;
    }
    
    public String visit(CompareExpression n, String methodname)
    {
        //System.out.println("CompareExpression");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);
        
        return_type = "boolean";
        //return "";//"boolean";   <-----------------------------------------
        return null;
    }
    
    
//    t1=generate(expr1)
//    t2 =generate(expr2)
//    r =new_temp()
//    emit(r =t1 op t2)
//    return r
    public String visit(PlusExpression n, String methodname)
    {
        //System.out.println("PlusExpression");
        
        out.print("PLUS ");
        
        String left = n.f0.accept(this, methodname);
        out.print(left+" ");
        System.out.println(left);
        String right = n.f2.accept(this, methodname);
        out.print(right+"\n");
        System.out.println(right);
        
        return_type = "int";
        //return "";//"int";  <----------------------------------------------
        return null;
    }
    
    public String visit(MinusExpression n, String methodname)
    {
        //System.out.println("MinusExpression");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);
        
        return_type = "int";
        //return "";//"int";  <----------------------------------------------
        return null;
    }
    
    public String visit(TimesExpression n, String methodname)
    {
        //System.out.println("TimesExpression");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);
        
        return_type = "int";
        //return "";//"int";  <----------------------------------------------
        return null;
    }
    
    public String visit(ArrayLookup n, String methodname)
    {
        //System.out.println("ArrayLookup");
        String left = n.f0.accept(this, methodname);
        String right = n.f2.accept(this, methodname);
        
        return_type = "int";
        //return "";//"int";  <----------------------------------------------
        return null;
    }
    
    public String visit(ArrayLength n, String methodname)
    {
        //System.out.println("ArrayLength");
        String left = n.f0.accept(this, methodname);
        
        return_type = "int";
        //return "";//"int";  <----------------------------------------------
        return null;
    }
    
    public String visit(MessageSend n, String methodname)
    {
        //System.out.println("MessageSend");
        out.print("CALL\n");//BEGIN\n");
        //String temp1 = get_Temp();
        //out.print("MOVE "+temp1+" ");
        
        String left = n.f0.accept(this, methodname);
        
        String which_class = return_type;//left;    <--------------
        //-------------------------->
        //if(left.equals("this"))
        //    which_class  = classname;
        if(which_class.equals("this"))
            which_class = classname;
        //<--------------------------
        
        String temp2 = get_Temp();
        String temp3 = get_Temp();
        
        String in_move="AAAAAAAAAAAAAAAAAAAAAAAAAAA";
        
        if(left != null && !left.contains(" "))
        {
            out.print("BEGIN\n");
            String temp1 = get_Temp();
            out.print("MOVE "+temp1+" ");
            if(left.equals("this"))
            {
                in_move = "TEMP 0";
                out.print(" "+in_move+"\n");
   
                out.print("HLOAD "+temp2+" "+temp1+" 0\n");
                out.print("HLOAD "+temp3+" "+temp2+" \n");
                //prepei na brw to offset ths sunarthshs pou kalw
            }
            else
            {
                //in_move = //prepei na typwsw thn temp3 apo to allocationexpression
                out.print(" "+in_move+"\n");
                
                out.print("HLOAD "+temp2+" "+temp1+" 0\n");
                out.print("HLOAD "+temp3+" "+temp2+" \n");
                //prepei na brw to offset ths sunarthshs pou kalw
            }
        }
        else
        {
            //in_move = temp3 apo to allocationexpression
            
            out.print("HLOAD "+temp2+" "+/*prepei na typwsw thn temp3 apo to allocationexpression+*/" 0\n");
            out.print("HLOAD "+temp3+" "+temp2+" \n");
            //prepei na brw to offset ths sunarthshs pou kalw
        }
        
        String right = n.f2.accept(this, methodname);
        return_type =  icl.fieldsOfClass.get(which_class).get(return_type).get(0);

        out.print("RETUTN\n"+temp2+"\n");
        out.print("END\n");
        out.print("("+in_move);
        //other arguments
        out.print(" )\n");
        
        
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
        
        //System.out.println(n.f2.f0.toString()+ " " +myList);
        
        for(int i=0; i<myList.size(); i++)
            if(myList.get(i).equals("this"))
                myList.set(i, in_move);
        
        //return "";
        return null;
        //return icl.fieldsOfClass.get(which_class).get(right).get(0);  <---------------
    }
    
    public String visit(ExpressionList n, String methodname)
    {
        //System.out.println("ExpressionList");
        
        /*String return_type = */n.f0.f0.accept(this, methodname);  //<-------------
        if(return_type.equals("this"))
            return_type = classname;
        //if(return_type.equals("this") || return_type.equals("int") || return_type.equals("boolean"))
        //    myList.add(n.f0.f0.choice.toString());
        if(n.f1.f0.present())
            n.f1.f0.accept(this, methodname);
        
        //return "";//return_type;    <---------------------------------
        return null;
    }
    
    public String visit(ExpressionTerm n, String methodname)
    {
        //System.out.println("ExpressionTerm");
        /*String return_type =*/n.f1.accept(this, methodname);  //<-------------
        if(return_type.equals("this"))
            return_type = classname;
        //myList.add(return_type);
        //return "";//return_type;    <-----------------------------
        return null;
    }
    
    public String visit(IntegerLiteral n, String methodname)
    {
        //System.out.println("IntegerLiteral");
        return_type = "int";
        //myList.add(n.f0.toString());
        //return "";//"int";  <--------------------------------
        //return null;
        return n.f0.toString();
    }
    
    public String visit(TrueLiteral n, String methodname)
    {
        //System.out.println("TrueLiteral");
        return_type = "boolean";
        //myList.add(n.f0.toString());
        //return "";//"boolean";  <------------------------------
        return null;
    }
    
    public String visit(FalseLiteral n, String methodname)
    {
        //System.out.println("FalseLiteral");
        return_type = "boolean";
        //myList.add(n.f0.toString());
        //return "";//"boolean";  <--------------------------------
        return null;
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
                    return_type = icl.methodsOfClass.get(classname).get(methodname).get(n.f0.toString());
                    //return "";//icl.methodsOfClass.get(classname).get(methodname).get(n.f0.toString()); <---------
                    //return null;
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
                        {
                            return_type = icl.fieldsOfClass.get(classname).get(methodname).get(i-1);
                            //return "";//icl.fieldsOfClass.get(classname).get(methodname).get(i-1);  <--------
                            //return null;
                            return icl.fieldsOfClass.get(classname).get(methodname).get(i-1);
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
                //return "";//icl.fieldsOfClass_vars.get(classname).get(n.f0.toString()); <------------
                //return null;
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
                    return_type = icl.fieldsOfClass_vars.get(temp).get(n.f0.toString());
                    //return "";//icl.fieldsOfClass_vars.get(temp).get(n.f0.toString());  <---------------
                    //return null;
                    return icl.fieldsOfClass_vars.get(temp).get(n.f0.toString());
                }
            }
        }
        //den to brhke pou8ena ara apla epistrefw to onoma n.f0.toString()
        //gia na to diaxeiristei o apo panw
      
        return_type = n.f0.toString();
        //return "";//n.f0.toString();    <-------------------------------------
        //return null;
        return n.f0.toString();
    }
    
    public String visit(ThisExpression n, String methodname)
    {
        //System.out.println("ThisExpression");
        return_type = "this";
        //return "";//"this"; <----------------------------------------
        //return null;
        return "this";
    }
    
    public String visit(ArrayAllocationExpression n, String methodname)
    {
        return_type = "int[]";
        //return "";//"int[]";    <---------------------------
        return null;
    }
    
    public String visit(AllocationExpression n, String methodname)
    {
        //System.out.println("AllocationExpression");
        String return_class = n.f1.accept(this, methodname);
        
        out.print("BEGIN\n");
        String temp3 = get_Temp();
        out.print("MOVE "+temp3+"\n");
        
        out.print("BEGIN \n");
        String temp1 = get_Temp();
        String temp2 = get_Temp();

        int position1=0;
        int position2=4;
        
        //----------------------------------->
        out.print("MOVE " + temp1 + " HALLOCATE " + icl.namesOfMethodsInClass.get(return_type).size()*4 + " \n");
        out.print("MOVE " + temp2 + " HALLOCATE " + (icl.namesOfFieldsInClass.get(return_type).size() +1)*4 + " \n");
//        out.print("MOVE " + temp1 + " HALLOCATE " + icl.namesOfMethodsInClass.get(return_class).size()*4 + " \n");
//        out.print("MOVE " + temp2 + " HALLOCATE " + (icl.namesOfFieldsInClass.get(return_class).size() +1)*4 + " \n");
        //<-----------------------------------
        
        //------------------------------------>
        for(int i=0; i<icl.namesOfMethodsInClass.get(return_type).size(); i++)
        //for(int i=0; i<icl.namesOfMethodsInClass.get(return_class).size(); i++)
        //<------------------------------------
        {
            //----------------->
            out.print("HSTORE "+temp1+ " "+position1+" "+icl.namesOfMethodsInClass.get(return_type).get(i)+"\n");
            //out.print("HSTORE "+temp1+ " "+position1+" "+icl.namesOfMethodsInClass.get(return_class).get(i)+"\n");
            //<-----------------
            position1 = position1+4;
        }
        //---------------------->
        for(int i=0; i<icl.namesOfFieldsInClass.get(return_type).size(); i++)
        //for(int i=0; i<icl.namesOfFieldsInClass.get(return_class).size(); i++)
        //<----------------------
        {
            //-------------------->
            out.print("HSTORE "+temp2+ " "+position2+" "+icl.namesOfFieldsInClass.get(return_type).get(i)+"\n");
            //out.print("HSTORE "+temp2+ " "+position2+" "+icl.namesOfFieldsInClass.get(return_class).get(i)+"\n");
            //<--------------------
            position2 = position2+4;
        }
        
        out.print("HSTORE "+temp2+" 0 "+temp1+"\n");
        //"iterator" sto icl.namesInClass.get(icl.current_class), meta gia ka8e ena koitaw an yparxei eite sto 
        //icl.fieldsOfClass_vars.get(icl.current_class) eite sto icl.fieldsOfClass.get(icl.current_class)
        //antistoixa to bazw mesa ston virtual table sth 8esh pou prepei me HSTORE
        
        out.print("RETURN \n"+temp2+"\n"+"END\n");
        
        //return "";//return_class;
        return null;
    }
    
    public String visit(NotExpression n, String methodname)
    {
        n.f1.accept(this, methodname);
        //return "";//n.f1.accept(this, methodname);  <-----------------------
        return null;
    }
    
    public String visit(BracketExpression n, String methodname)
    {
        /*String return_type = */n.f1.accept(this, methodname); //<---------
        if(return_type.equals("this"))
            return_type = classname;
        //return "";//return_type;    <-------------------------
        return null;
    }
    
    public String visit(ClassDeclaration c, String _)
    {
        //System.out.println("ClassDeclaration");
        icl.current_class = c.f1.f0.toString();
        classname = c.f1.f0.toString();
        if(c.f4.present())
            c.f4.accept(this, null);
        //return "";
        return null;
    }
    
    public String visit(ClassExtendsDeclaration c, String _)
    {
        //System.out.println("ClassExtendsDeclaration");
        icl.current_class = c.f1.f0.toString();
        classname = c.f1.f0.toString();
        if(c.f6.present())
            c.f6.accept(this, null);
        //return "";
        return null;
    }
    
    public String visit(MethodDeclaration m, String _) 
    {   
        label_counter = 0;
        temp_counter = (icl.fieldsOfClass.get(classname).get(m.f2.f0.toString()).size()-1)/2;
        out.print("\n"+icl.current_class+"_"+m.f2.f0.toString()+" ");
        
        out.print("[ "+ ((icl.fieldsOfClass.get(classname).get(m.f2.f0.toString()).size()-1)/2 + 1) +" ] \n");
        out.print("BEGIN \n");
        
        if(m.f8.present())
            m.f8.accept(this, m.f2.f0.toString());
        
        /*String return_type = */m.f10.accept(this, m.f2.f0.toString());    //<------------------

        if(return_type.equals("this"))
            return_type = classname;
 
        out.print("END \n");
        
        return_type = icl.fieldsOfClass.get(classname).get(m.f2.f0.toString()).get(0);
        //return "";//icl.fieldsOfClass.get(classname).get(m.f2.f0.toString()).get(0);    <--------------
        return null;
    }
}
