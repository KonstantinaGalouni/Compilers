import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class CtrlFlowGraphVisitor extends GJDepthFirst<String, String>{
    public Interface_Class icl = new Interface_Class();
    int i = 0;
    int procedure_num = 0;
    boolean jump = false;
    Map<String, Integer> edges_to_do = new HashMap();
    int args = 0;
    Map<Integer, Set<Integer>> list_of_args = new HashMap();
    public Map<Integer, Set<Integer>> list_of_pos_of_calls = new HashMap();
    Set<Integer> pos_of_calls;
    public Map<Integer, List<Integer>> list_of_callArgs= new HashMap();
    List<Integer> callArgs;
    Map<String, Integer> label_num = new HashMap();
    
    public String visit(Goal g, String _)
    {
        list_of_args.put(0, null);
        pos_of_calls = new HashSet();
        callArgs = new ArrayList<>();
        
        g.f1.accept(this, null);
        
        list_of_pos_of_calls.put(procedure_num, pos_of_calls);
        list_of_callArgs.put(procedure_num, callArgs);
        
        if(icl.ctrl_flow_graph.containsKey(i-1))
        {
            if(icl.ctrl_flow_graph.get(i-1).next.contains(i))
               icl.ctrl_flow_graph.get(i-1).next.remove(i);
        }
        
        set_prev();
        set_in_out(icl);

        Map<Integer, CtrlFlowGraph> graph1 = new HashMap();
        graph1.putAll(icl.ctrl_flow_graph);
        icl.ctrl_flow_graph.clear();
        icl.list_of_graphs.add(graph1);
        
        if(g.f3.present())
        {
            g.f3.accept(this, null);
        }
        return null;
    }
    
    public String visit(Procedure p, String _)
    {
        label_num.clear();
        procedure_num++;
        Set<Integer> arguments = new HashSet();
        pos_of_calls = new HashSet();
        callArgs = new ArrayList<>();
        int num_of_args = Integer.parseInt(p.f2.f0.toString());
        for(int k=0; k<num_of_args; k++)
            arguments.add(k);
        list_of_args.put(procedure_num, arguments);
        p.f4.accept(this, null);
        list_of_pos_of_calls.put(procedure_num, pos_of_calls);
        list_of_callArgs.put(procedure_num, callArgs);
        return null;
    }
    
    public String visit(Exp e, String _)
    {
        //System.out.println("Exp");
        e.f0.choice.accept(this, null);
        return null;
    }
    
    public String visit(SimpleExp se, String _)
    {
        //System.out.println("SimpleExp");
        String temp = se.f0.choice.accept(this, null);
        if(temp != null )
        {
            icl.ctrl_flow_graph.get(i).use.add(Integer.parseInt(temp));
        }
        return null;
    }
    
    public String visit(Call c, String _)
    {
        //System.out.println("Call");
        pos_of_calls.add(i);
        c.f1.accept(this, null);
        
        if(c.f3.present())
        {
            args = 0;
            c.f3.accept(this, "call");
            callArgs.add(args);
        }
        return "call";
    }
    
    public String visit(HAllocate ha, String _)
    {
        //System.out.println("HAllocate");
        ha.f1.accept(this, null);
        return null;
    }
    
    public String visit(BinOp bo, String _)
    {
        //System.out.println("BinOp");
        String temp = bo.f1.accept(this, null);
       icl.ctrl_flow_graph.get(i).use.add(Integer.parseInt(temp));
        bo.f2.accept(this, null);
        return null;
    }
    
    public String visit(StmtExp se, String _)
    {
        //System.out.println("StmtExp");
        i=0;
        se.f1.accept(this, null);
        
        CtrlFlowGraph gr = new CtrlFlowGraph();
        icl.ctrl_flow_graph.put(i, gr);
        se.f3.accept(this, null);
        gr.next.add(i+1);
        i++;
        
        if(icl.ctrl_flow_graph.containsKey(i-1))
        {
            if(icl.ctrl_flow_graph.get(i-1).next.contains(i))
                icl.ctrl_flow_graph.get(i-1).next.remove(i);
        }
        
        set_prev();
        set_in_out(icl);
        
        Map<Integer, CtrlFlowGraph> graph1 = new HashMap();
        graph1.putAll(icl.ctrl_flow_graph);
        icl.list_of_graphs.add(graph1);      
        icl.ctrl_flow_graph.clear();
        
        return null;
    }
    
    public String visit(Stmt s, String _)
    {
        //System.out.println("Stmt");
        CtrlFlowGraph gr = new CtrlFlowGraph();
        icl.ctrl_flow_graph.put(i, gr);
            s.f0.choice.accept(this, null);
        if(jump == false)
            gr.next.add(i+1);
        i++;
        return null;
    }
    
    public String visit(NoOpStmt n, String _)
    {
        //System.out.println("Noop");
        jump = false;
        return null;
    }
            
    public String visit(ErrorStmt e, String _)
    {
        //System.out.println("Error");
        jump = false;
        return null;
    }
            
    public String visit(CJumpStmt cj, String _)
    {
        //System.out.println("CJumpStmt");
        edges_to_do.put(cj.f2.f0.toString(), i);
        jump = false;
        
        String temp = cj.f1.accept(this, null);
        icl.ctrl_flow_graph.get(i).use.add(Integer.parseInt(temp));
        return null;
    }
            
    public String visit(JumpStmt j, String _)
    {
        //System.out.println("JumpStmt");
        if(label_num.containsKey(j.f1.f0.toString()))
            icl.ctrl_flow_graph.get(i).next.add(label_num.get(j.f1.f0.toString()));
        else
            edges_to_do.put(j.f1.f0.toString(), i);
        jump = true;
        return null;
    }
            
    public String visit(HStoreStmt hs, String _)
    {
        //System.out.println("HStoreStmt");
        String temp1 = hs.f1.accept(this, null);
            icl.ctrl_flow_graph.get(i).use.add(Integer.parseInt(temp1));
        String temp2 = hs.f3.accept(this, null);
        icl.ctrl_flow_graph.get(i).use.add(Integer.parseInt(temp2));
        
        jump = false;
        return null;
    }
            
    public String visit(HLoadStmt hl, String _)
    {
        //System.out.println("HLoadStmt");
        String temp1 = hl.f1.accept(this, null);
        icl.ctrl_flow_graph.get(i).def.add(Integer.parseInt(temp1));
        String temp2 = hl.f2.accept(this, null);
        icl.ctrl_flow_graph.get(i).use.add(Integer.parseInt(temp2));
        
        jump = false;
        return null;
    }
            
    public String visit(MoveStmt m, String _)
    {
        //System.out.println("MoveStmt");
        String temp = m.f1.accept(this, null);
        icl.ctrl_flow_graph.get(i).def.add(Integer.parseInt(temp));
        
        String temp2 = m.f2.accept(this, null);
        if(temp2!=null && temp2.equals("call")) //if after move we have a call, never consider it as dead code
            icl.ctrl_flow_graph.get(i).use.add(Integer.parseInt(temp));
        
        jump = false;
        return null;
    }
            
    public String visit(PrintStmt p, String _)
    {
        //System.out.println("PrintStmt");
        p.f1.accept(this, null);
        
        jump = false;
        return null;
    }
    
    public String visit(Label l, String _)
    {
        //System.out.println("Label");
        if(!l.f0.toString().contains("_"))
        {
            label_num.put(l.f0.toString(), i);
            if(edges_to_do.containsKey(l.f0.toString()))
            {
                icl.ctrl_flow_graph.get(edges_to_do.get(l.f0.toString())).next.add(i);
                edges_to_do.remove(l.f0.toString());
            }
        }
        return null;
    }
    
    
    
    public String visit(Temp t, String from)
    {
        //System.out.println("Temp");
        
        if(from!=null && from.equals("call"))
        {
            icl.ctrl_flow_graph.get(i).use.add(Integer.parseInt(t.f1.f0.toString()));
            args++;
        }
        return t.f1.f0.toString();
    }
    
    void set_prev()
    {
        int j;
        for(j=0; j<icl.ctrl_flow_graph.size(); j++)
        {
            for(Integer n : icl.ctrl_flow_graph.get(j).next)
            {
                icl.ctrl_flow_graph.get(n).prev.add(j);
            }
        }
    }
    
    void set_in_out(Interface_Class icl)
    {
//        System.out.println("Set_In_Out");
        int j=0;
        for(j=icl.ctrl_flow_graph.size()-1; j>=0; j--)
        {
            icl.ctrl_flow_graph.get(j).out.clear();
            icl.ctrl_flow_graph.get(j).in.clear();
        }
        
        boolean change = true;
        while(change == true)
        {
            change = false;
            int k;
            for(k=icl.ctrl_flow_graph.size()-1; k>=0; k--)
            {                
                Set<Integer> temp_set = new HashSet<>();
                temp_set.addAll(icl.ctrl_flow_graph.get(k).out);
                temp_set.removeAll(icl.ctrl_flow_graph.get(k).def);
                temp_set.addAll(icl.ctrl_flow_graph.get(k).use);
                if(icl.ctrl_flow_graph.get(k).in.addAll(temp_set) == true)
                    change = true;
                for(Integer n: icl.ctrl_flow_graph.get(k).next)
                    if(icl.ctrl_flow_graph.get(k).out.addAll(icl.ctrl_flow_graph.get(n).in) == true)
                        change = true;
            }
            
        }
    }
}
