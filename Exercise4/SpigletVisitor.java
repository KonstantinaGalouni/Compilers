import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class SpigletVisitor extends GJDepthFirst<String, String>{
    PrintWriter out = null;
    static int label_counter = 0;
    static int temp_counter = 0;
    int label_counter_init;
    int temp_counter_init;
    boolean call = false;
    List<String> args;
    boolean simplestm = false;
    
    SpigletVisitor(PrintWriter stream){
        this.out = stream;
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
    
    public void initialize(int temp, int label)
    {
        temp_counter = temp;
        label_counter = label;
        if(label_counter_init == 0 && temp_counter_init == 0)
        {
            temp_counter_init = temp;
            label_counter_init = label;
        }
    }
    
    public String visit(Goal g, String _)
    {
        System.out.println("Goal");
        out.print("MAIN\n");
        g.f1.accept(this, null);
        out.print("END\n");
        if(g.f3.present())
            g.f3.accept(this, null);
        return null;
    }
    
    public String visit(StmtList sl, String _)
    {
        System.out.println("StmtList");
        if(sl.f0.present())
            sl.f0.accept(this, "StmtList");
        return null;
    }
    
    public String visit(Procedure p, String _)
    {
        System.out.println("\nProcedure");
        initialize(temp_counter_init, label_counter_init);
        out.print("\n");
        out.print(p.f0.accept(this, null));
        out.print("[");
        out.print(p.f2.accept(this, null));
        out.print("]\n");
        out.print("BEGIN\n");
        String temp = p.f4.accept(this, null);
        out.print("RETURN "+temp+"\n");
        out.print("END\n");
        return null;
    }
    
    public String visit(NoOpStmt n, String _)
    {
        System.out.println("NoOpStmt");
        out.print("NOOP\n");
        return null;
    }
    
    public String visit(ErrorStmt e, String _)
    {
        System.out.println("ErrorStmt");
        out.print("ERROR\n");
        return null;
    }
    
    public String visit(Operator o, String _)
    {
        System.out.println("Orepator");
        return o.f0.choice.toString();
    }
    
    public String visit(Temp t, String _)
    {
        System.out.println("Temp");
        String temp;
        temp = t.f1.accept(this, null);
        return "TEMP "+temp;
    }
    
    public String visit(IntegerLiteral i, String _)
    {
        System.out.println("IntegerLiteral");
        simplestm = true;
        return i.f0.toString();
    }
    
    public String visit(Label l, String from)
    {
        System.out.println("Label");
        if(from != null && from.equals("StmtList"))
        {
            out.print(l.f0.toString()+" NOOP\n");
            String lbl = get_Label();
            out.print(lbl+" ");
        }
        simplestm = true;
        return l.f0.toString();
    }

    public String visit(CJumpStmt cj, String _)
    {
        System.out.println("CJumpStmt");
        String temp = cj.f1.accept(this, null);
        out.print("CJUMP "+temp+" ");
        
        out.print(cj.f2.accept(this, null));
        
        out.print("\n");
        return null;
    }
    
    public String visit(JumpStmt j, String _)
    {
        System.out.println("JumpStmt");
        out.print("JUMP ");
        
        out.print(j.f1.accept(this, null));
        out.print("\n");
                
        return null;
    }
    
    public String visit(HStoreStmt hs, String _)
    {
        System.out.println("HStoreStmt");
        String temp1 = hs.f1.accept(this, null);
        String temp2 = hs.f3.accept(this, null);
        
        String temp = get_Temp();
        if(!temp2.startsWith("TEMP "))
            out.print("MOVE "+temp+" "+temp2+"\n");
        else
            temp = temp2;
        out.print("HSTORE "+temp1+" ");
        out.print(hs.f2.accept(this, null));
        out.print(" "+temp+"\n");
        return null;
    }
    
    public String visit(HLoadStmt hl, String _)
    {
        System.out.println("HLoadStmt");
        String temp = hl.f2.accept(this, null);
        out.print("HLOAD "+" ");
        
        out.print(hl.f1.accept(this, null));
        
        out.print(" "+temp+" ");
        
        out.print(hl.f3.accept(this, null));
        
        out.print("\n");
        return null;
    }
    
    public String visit(MoveStmt m, String _)
    {
        System.out.println("MoveStmt");
        String temp = m.f2.accept(this, null);
        out.print("MOVE ");       
        out.print(m.f1.accept(this, null));
        out.print(" "+temp+"\n");
        return null;
    }

    public String visit(PrintStmt p, String _)
    {
        System.out.println("PrintStmt");
        String temp = get_Temp();
        String temp1 = p.f1.accept(this, null);
        if(!temp1.startsWith("TEMP "))
            out.print("MOVE "+temp+" "+temp1+"\n");
        else
            temp = temp1;
        out.print("PRINT "+temp+"\n");
        return null;
    }
    
    public String visit(Exp e, String from)
    {
        System.out.println("Exp");
        simplestm = false;
        String temp;
        temp = e.f0.accept(this, "Exp");

        if(call == true && from!=null)
        {
            if(simplestm == true)
            {
                String temp1 = get_Temp();
                if(!temp.startsWith("TEMP "))
                    out.print("MOVE "+temp1+" "+temp+"\n");
                else
                    temp1 = temp;
                args.add(temp1);
            }
            else
                args.add(temp);
        }
        return temp;
    }
    
    public String visit(StmtExp se, String _)
    {
        System.out.println("StmtExp");
        se.f1.accept(this, null);
        
        String temp1 = get_Temp();
        String temp = se.f3.accept(this, null);
        if(!temp.startsWith("TEMP "))
            out.print("MOVE "+temp1+" "+temp+"\n");
        else
            temp1 = temp;
        return temp1;
    }
    
    public String visit(Call c, String from)
    {
        System.out.println("Call");
        List temporary_List = null;
        if(from != null)
            temporary_List = args;
        if(call == false)
            from = null;
        String temp1 = c.f1.accept(this, null);
        call = true;
        args = new ArrayList<String>();
        if(c.f3.present())
            c.f3.accept(this, "Call");
        String temp = get_Temp();
        out.print("MOVE "+temp+" ");
        out.print("CALL "+temp1+" ( ");
        for(String s : args)
            out.print(s+" ");
        out.print(" )\n");
        call = false;
        if(from!=null && from.equals("Exp"))
            call = true;
        if(temporary_List != null)
            args = temporary_List;
        return temp;
    }
    
    public String visit(HAllocate h, String _)
    {
        System.out.println("HAllocate");
        String temp1 = h.f1.accept(this, null);
        String temp = get_Temp();
        out.print("MOVE "+temp+" ");
        out.print("HALLOCATE "+temp1+"\n");
        return temp;
    }
    
    public String visit(BinOp bo, String _)
    {
        System.out.println("BinOp");
        String temp1 = bo.f1.accept(this, null);
        String temp2 = get_Temp();
        if(!temp1.startsWith("TEMP "))
            out.print("MOVE "+temp2+" "+temp1+"\n");
        else
            temp2 = temp1;
        String temp3 = bo.f2.accept(this, null);
        String temp4 = get_Temp();
        if(!temp3.startsWith("TEMP "))
            out.print("MOVE "+temp4+" "+temp3+"\n");
        else
            temp4 = temp3;
        String temp5 = get_Temp();
        out.print("MOVE "+temp5+" ");
        out.print(bo.f0.accept(this, null));
        out.print(" "+temp2+" "+temp4);
        out.print("\n");
        return temp5;
    }
}
