import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class KangaVisitor extends GJDepthFirst<String, String>{
    PrintWriter out = null;
    InterferenceGraphVisitor my_igv;
    int procedure_num = 0;
    int instruction_num = -1;
    Set<Integer> s_to_spill = new HashSet();
    int which_arg = 0;
    List<Integer> all_spilled= new ArrayList<>();
    int num_of_args = 0;
    
    KangaVisitor(PrintWriter stream, InterferenceGraphVisitor igv){
        this.out = stream;
        my_igv = igv;
    }
    
    int to_be_saved(int stack_locations)
    {
        for(Integer temp1: my_igv.gv.list_of_pos_of_calls.get(procedure_num))
        {
            int to_spill = 0;
            for(Integer temp2: my_igv.icl.list_of_graphs.get(procedure_num).get(temp1).out)
            {
                if(my_igv.icl.list_of_graphs.get(procedure_num).get(temp1).in.contains(temp2) &&
                        my_igv.list_of_temp_to_reg.get(procedure_num).containsKey(temp2) &&
                        my_igv.list_of_temp_to_reg.get(procedure_num).get(temp2).startsWith("t"))
                {
                    to_spill++;
                }
            }
            
            if(stack_locations<to_spill)
                stack_locations=to_spill;
        }
        for(Integer temp1: my_igv.list_of_temp_to_reg.get(procedure_num).keySet())
        {
            if(my_igv.list_of_temp_to_reg.get(procedure_num).get(temp1).startsWith("s"))
            {
                for(int temp2=0; temp2<my_igv.icl.list_of_graphs.get(procedure_num).size(); temp2++)
                {
                    if(my_igv.icl.list_of_graphs.get(procedure_num).get(temp2).in.contains(temp1) && !s_to_spill.contains(temp1))
                    {
                        boolean what_to_do = true;
                        for(Integer t: s_to_spill)
                        {
                            if(my_igv.list_of_temp_to_reg.get(procedure_num).get(temp1).equals(my_igv.list_of_temp_to_reg.get(procedure_num).get(t)))
                            {
                                what_to_do = false;
                                break;
                            }
                        }
                        if(what_to_do == true){
                        stack_locations++;
                        s_to_spill.add(temp1);
                        }
                    }
                    else if(my_igv.icl.list_of_graphs.get(procedure_num).get(temp2).out.contains(temp1) && !s_to_spill.contains(temp1))
                    {
                        boolean what_to_do = true;
                        for(Integer t: s_to_spill)
                        {
                            if(my_igv.list_of_temp_to_reg.get(procedure_num).get(temp1).equals(my_igv.list_of_temp_to_reg.get(procedure_num).get(t)))
                            {
                                what_to_do = false;
                                break;
                            }
                        }
                        if(what_to_do == true){
                        stack_locations++;
                        s_to_spill.add(temp1);
                        }
                    }
                }
            }
        }
        return stack_locations;
    }
    
    public String visit(Goal g, String _)
    {      
        out.print("MAIN[0][");
        int stack_locations=0;
        stack_locations = to_be_saved(stack_locations);
        stack_locations=stack_locations+0+my_igv.spilled_temps.get(procedure_num).size();
        out.print(stack_locations);
        out.print("][");
        int max_args = 0;
        for(int temp=0; temp<my_igv.gv.list_of_callArgs.get(procedure_num).size(); temp++)
        {
            if(max_args<my_igv.gv.list_of_callArgs.get(procedure_num).get(temp))
                max_args = my_igv.gv.list_of_callArgs.get(procedure_num).get(temp);
        }
        out.print(max_args);
        out.print("]\n");
        
        g.f1.accept(this, null);
        
        out.print("END\n\n");
        
        if(g.f3.present())
            g.f3.accept(this, null);
        return null;
    }
    
    public String visit(StmtList sl, String _)
    {
        //System.out.println("StmtList");
        if(sl.f0.present())
            sl.f0.accept(this, "StmtList");
        return null;
    }
    
    public String visit(Procedure p, String _)
    {
        //System.out.println("Procedure");
        all_spilled.clear();
        s_to_spill.clear();
        
        procedure_num++;
        instruction_num=-1;
        out.print(p.f0.accept(this, null));
        out.print("[");
        num_of_args = Integer.parseInt(p.f2.accept(this, null));
        out.print(num_of_args);
        out.print("][");
        int stack_locations=0;
        stack_locations = to_be_saved(stack_locations);
        if(num_of_args>4)
            stack_locations=stack_locations+(num_of_args-4)+my_igv.spilled_temps.get(procedure_num).size();
        else
            stack_locations=stack_locations+0+my_igv.spilled_temps.get(procedure_num).size();
        out.print(stack_locations);
        out.print("][");
        int max_args = 0;
        for(int temp=0; temp<my_igv.gv.list_of_callArgs.get(procedure_num).size(); temp++)
        {
            if(max_args<my_igv.gv.list_of_callArgs.get(procedure_num).get(temp))
                max_args = my_igv.gv.list_of_callArgs.get(procedure_num).get(temp);
        }
        out.print(max_args);
        out.print("]\n");
        
        for(int k=5; k<=num_of_args; k++)   //more than 4 args are in stack
        {
            all_spilled.add(k-5, k-1);
        }
        
        int where=0;
        if(num_of_args>4)
            where = num_of_args-4;
        for(Integer k: s_to_spill)
        {
            all_spilled.add(where, k);
            out.print("ASTORE SPILLEDARG "+where+" "+my_igv.list_of_temp_to_reg.get(procedure_num).get(k)+"\n");
            where++;
        }
        
        for(int k=1; k<5; k++)
        {
            if(my_igv.list_of_temp_to_reg.get(procedure_num).containsKey(k-1))  //if this arg has a register
                out.print("MOVE "+my_igv.list_of_temp_to_reg.get(procedure_num).get(k-1)+" a"+(k-1)+"\n");
        }
        
        for(int k=5; k<=num_of_args; k++)   //more than 4 args are in stack
        {
            if(my_igv.list_of_temp_to_reg.get(procedure_num).containsKey(k-1))  //if this arg has a register
                out.print("ALOAD "+my_igv.list_of_temp_to_reg.get(procedure_num).get(k-1)+" SPILLEDARG "+(k-5)+"\n");
        }
        
        p.f4.accept(this, null);
        
        where=0;
        if(num_of_args>4)
            where = num_of_args-4;
        for(Integer k: s_to_spill)
        {
            out.print("ALOAD "+my_igv.list_of_temp_to_reg.get(procedure_num).get(k)+" SPILLEDARG "+where+"\n");
            where++;
        }
        for(Integer k: s_to_spill)
        {
            all_spilled.remove(k);
        }
        
        out.print("END\n\n");
        return null;
    }
    
    public String visit(StmtExp se, String _)
    {
        //System.out.println("StmtExp");
        se.f1.accept(this, null);
        instruction_num++;
        
        String temp = se.f3.accept(this, null);
        if(temp.startsWith("SPILLEDARG "))
            out.print("ALOAD v0 "+temp+"\n");
        else
            out.print("MOVE v0 "+temp+"\n");
        return null;
    }
    
    public String visit(NoOpStmt n, String _)
    {
        //System.out.println("NoOpStmt");
        instruction_num++;
        out.print("NOOP\n");
        return null;
    }
    
    public String visit(ErrorStmt e, String _)
    {
        //System.out.println("ErrorStmt");
        instruction_num++;
        out.print("ERROR\n");
        return null;
    }
    
    public String visit(CJumpStmt cj, String _)
    {
        //System.out.println("CJumpStmt");
        instruction_num++;
        String temp=cj.f1.accept(this, null);
        if(temp.startsWith("SPILLEDARG "))
        {
            out.print("ALOAD v0 "+temp+"\n");
            temp="v0";
        }
        out.print("CJUMP "+temp+" "+cj.f2.accept(this, null)+"\n");
        return null;
    }
    
    public String visit(JumpStmt j, String _)
    {
        //System.out.println("JumpStmt");
        instruction_num++;
        out.print("JUMP "+j.f1.accept(this, null)+"\n");
        return null;
    }
    
    public String visit(HStoreStmt hs, String _)
    {
        //System.out.println("HStoreStmt");
        instruction_num++;
        String temp1=hs.f1.accept(this, null);
        if(temp1.startsWith("SPILLEDARG "))
        {
            out.print("ALOAD v1 "+temp1+"\n");
            temp1="v1";
        }
        String temp2=hs.f3.accept(this, null);
        if(temp2.startsWith("SPILLEDARG "))
        {
            out.print("ALOAD v0 "+temp2+"\n");
            temp2="v0";
        }
        
        out.print("HSTORE "+temp1+" "+hs.f2.accept(this, null)+" "+temp2+"\n");
        return null;
    }
    
    public String visit(HLoadStmt hl, String _)
    {
        //System.out.println("HLoadStmt");
        instruction_num++;
        if(my_igv.icl.list_of_graphs.get(procedure_num).get(instruction_num).def.contains(-1))
            out.print("NOOP\n");
        else
        {
            String temp1=hl.f1.accept(this, "hload1");         
            String temp2=hl.f2.accept(this, null);
            if(temp2.startsWith("SPILLEDARG "))
            {
                out.print("ALOAD v0 "+temp2+"\n");
                temp2="v0";
            }
            String for_astore = "";
            if(temp1.startsWith("v1+"))
            {
                for_astore = temp1.substring(3);
                temp1="v1";
            }
            out.print("HLOAD "+temp1+" "+temp2+" "+hl.f3.accept(this, null)+"\n");
            if(temp1.equals("v1"))
                    out.print("ASTORE SPILLEDARG "+for_astore+" v1\n");
        }
        return null;
    }
    
    public String visit(MoveStmt m, String _)
    {
        //System.out.println("MoveStmt");
        instruction_num++;
        if(my_igv.icl.list_of_graphs.get(procedure_num).get(instruction_num).def.contains(-1))
        {  
            out.print("NOOP\n");
        }
        else
        {
            String temp = m.f1.accept(this, "move1");
            String ret_val = m.f2.accept(this, null);
            if(ret_val!=null && ret_val.equals("call"))
            {
                for(Integer k: my_igv.icl.list_of_graphs.get(procedure_num).get(instruction_num).in)
                {
                    if(my_igv.icl.list_of_graphs.get(procedure_num).get(instruction_num).out.contains(k) && 
                            my_igv.list_of_temp_to_reg.get(procedure_num).containsKey(k) && my_igv.list_of_temp_to_reg.get(procedure_num).get(k).startsWith("t"))
                    {
                        out.print("ALOAD "+my_igv.list_of_temp_to_reg.get(procedure_num).get(k)+" SPILLEDARG "+all_spilled.indexOf(k) +"\n");
                    }
                }
                for(Integer k: my_igv.icl.list_of_graphs.get(procedure_num).get(instruction_num).in)
                {
                    if(my_igv.icl.list_of_graphs.get(procedure_num).get(instruction_num).out.contains(k) && 
                            my_igv.list_of_temp_to_reg.get(procedure_num).containsKey(k) && my_igv.list_of_temp_to_reg.get(procedure_num).get(k).startsWith("t"))
                    {
                        all_spilled.remove(k);
                    }
                }
                String for_astore = "";
                if(temp.startsWith("v1+"))
                {
                    for_astore = temp.substring(3);
                    temp="v1";
                }
                out.print("MOVE "+temp+" "+" v0\n");
                if(temp.equals("v1"))
                    out.print("ASTORE SPILLEDARG "+for_astore+" v1\n");
            }
            else    // 	HAllocate| BinOp| SimpleExp
            {
                if(ret_val.startsWith("SPILLEDARG "))
                {
                    out.print("ALOAD v0 "+ret_val+"\n");
                    ret_val = "v0";
                }
                String for_astore = "";
                if(temp.startsWith("v1+"))
                {
                    for_astore = temp.substring(3);
                    temp="v1";
                }
                out.print("MOVE "+temp+" "+ret_val+"\n");
                if(temp.equals("v1"))
                    out.print("ASTORE SPILLEDARG "+for_astore+" v1\n");
            }
        }
        return null;
    }
    
    public String visit(PrintStmt p, String _)
    {
        //System.out.println("PrintStmt");
        instruction_num++;
        String temp=p.f1.accept(this, null);
        if(temp.startsWith("SPILLEDARG "))
        {
            out.print("ALOAD v0 "+temp+"\n");
            temp="v0";
        }
        out.print("PRINT "+temp+"\n");
        return null;
    }
    
    public String visit(Call c, String _)
    {
        //System.out.println("Call");
        for(Integer k: my_igv.icl.list_of_graphs.get(procedure_num).get(instruction_num).in)
        {
            if(my_igv.icl.list_of_graphs.get(procedure_num).get(instruction_num).out.contains(k) && 
                    my_igv.list_of_temp_to_reg.get(procedure_num).containsKey(k) && my_igv.list_of_temp_to_reg.get(procedure_num).get(k).startsWith("t"))
            {
                all_spilled.add(k);
                out.print("ASTORE SPILLEDARG "+(all_spilled.size()-1)+" "+my_igv.list_of_temp_to_reg.get(procedure_num).get(k)+"\n");
            }
        }
        which_arg = 0;
        c.f3.accept(this, "call");
        String temp = c.f1.accept(this, null);
        if(temp.startsWith("SPILLEDARG "))
        {
            out.print("ALOAD v1 "+temp+"\n");
            temp="v1";
        }
        out.print("CALL "+temp+"\n");
        return "call";
    }
    
    public String visit(HAllocate h, String _)
    {
        String temp = h.f1.accept(this, null);
        if(temp.startsWith("SPILLEDARG "))
        {
            out.print("ALOAD v1 "+temp+"\n");
            temp="v1";
        }
        return "HALLOCATE "+temp;
    }
    
    public String visit(BinOp bo, String _)
    {
        String temp0=bo.f0.accept(this, null);
        String temp1=bo.f1.accept(this, null);
        String temp2=bo.f2.accept(this, null);
        
        if(temp1.startsWith("SPILLEDARG "))
        {
            out.print("ALOAD v0 "+temp1+"\n");
            temp1="v0";
        }
        if(temp2.startsWith("SPILLEDARG "))
        {
            out.print("ALOAD v1 "+temp2+"\n");
            temp2="v1";
        }
        return temp0+" "+temp1+" "+temp2;
    }
    
    public String visit(Temp t, String from)
    {
        String temp_reg;
        int temp = Integer.parseInt(t.f1.accept(this, null));
        if(my_igv.list_of_temp_to_reg.get(procedure_num).containsKey(temp))
        {
            temp_reg = my_igv.list_of_temp_to_reg.get(procedure_num).get(temp);
            if(from!=null && from.equals("call"))   //this is an argument
            {
                if(which_arg<=3)
                {
                    out.print("MOVE a"+which_arg+" "+temp_reg+"\n");
                }
                else
                {
                    //e.g. PASSARG 1 t5 // more than 4 arguments placed in stack
                    out.print("PASSARG "+(which_arg-3)+" "+temp_reg+"\n");  //plase is 4-which_arg+1
                }
                which_arg++;
                return null;
            }
            else
                return temp_reg;    //if from!=call then my parent should print it
        }   
        else    //this is a spilled temp
        {
            if(!all_spilled.contains(temp))
            {
                if(temp < num_of_args)    //argument
                {
                    out.print("ASTORE SPILLEDARG "+all_spilled.size()+" a"+temp+"\n");
                    all_spilled.add(temp);
                }
                else
                {
                        all_spilled.add(temp);
                }
            }
            if(from!=null && from.equals("call"))   //this is an argument)
            {
                out.print("ALOAD v1 SPILLEDARG "+all_spilled.indexOf(temp)+"\n");
                if(which_arg > 3)
                    out.print("PASSARG "+(which_arg-3)+" v1\n");
                else
                    out.print("MOVE a"+which_arg+" v1\n");
                which_arg++;
                return null;
            }
            else if(from!=null && ((from.equals("move1")) || from.equals("hload1")))
            {
                return "v1+"+all_spilled.indexOf(temp);
            }
            else
            {
                return "SPILLEDARG "+all_spilled.indexOf(temp);
            }
        }
    }
    
    public String visit(IntegerLiteral i, String _)
    {
        return i.f0.toString();
    }
    
    public String visit(Label l, String from)
    {
        if(from!=null && from.equals("StmtList"))
            out.print(l.f0.toString()+" ");
        return l.f0.toString();
    }
    
    public String visit(Operator o, String _)
    {
        //System.out.println("Orepator");
        return o.f0.choice.toString();
    }
}
