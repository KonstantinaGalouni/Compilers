import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class InterferenceGraphVisitor extends GJDepthFirst<String, String>{
    public Interface_Class icl;
    boolean changes = true;
    int i = 0;
    int procedure_num = 0;
    public CtrlFlowGraphVisitor gv = new CtrlFlowGraphVisitor();
    Goal mytree;
    public List<Map<Integer, InterferenceGraph>> list_of_infr_graph = new ArrayList<>();
    public List<Map<Integer, InterferenceGraph>> initial_list_of_infr_graph = new ArrayList<>();
    public List<Map<Integer, String>> list_of_temp_to_reg = new ArrayList<>();
    public List<List<Integer>> spilled_temps = new ArrayList<>();

    String choose_reg(Set<Integer> my_neighbors, Map<Integer, String> temp_to_reg)
    {
        boolean ok = false;
        String ret_value = null;
        for(int i=0; i<10; i++) //there are t0 - t9 registers (10)
        {
            if(!temp_to_reg.containsValue("t"+i) )
            {
                ret_value = "t"+i;
                break;
            }
            else
            {
                for(Integer j: temp_to_reg.keySet())
                {
                    if(temp_to_reg.get(j).equals("t"+i) && my_neighbors.contains(j))
                    {
                        ok = false;
                        break;
                    }
                    else
                        ok = true;
                }
                if(ok == true)
                {
                    ret_value = "t"+i;
                    break;
                }
            }
        }
        if(ret_value == null)
        {
            for(int i=0; i<8; i++)  //there are s0 - s7 registers (8)
            {
                if(!temp_to_reg.containsValue("s"+i) || my_neighbors == null)
                {
                    ret_value = "s"+i;
                    break;
                }
                else
                {
                    for(Integer j: temp_to_reg.keySet())
                    {
                        if(temp_to_reg.get(j).equals("s"+i) && my_neighbors.contains(j))
                        {
                        ok = false;
                        break;
                        }
                        else
                            ok = true;
                    }
                    if(ok == true)
                    {
                        ret_value = "s"+i;
                        break;
                    }
                }
            }
        }
        
        return ret_value;
    }
    
    int heuristic(Map<Integer, CtrlFlowGraph> my_map1, Map<Integer, InterferenceGraph> my_map2)
    {
        int spill_cost=0;
        int to_spill=0;
        int min = -1;
        for(Integer i: my_map2.keySet())
        {
            for(int j=0; j<my_map1.size(); j++)
            {
                if(my_map1.get(j).def.contains(i))
                    spill_cost++;
                if(my_map1.get(j).use.contains(i))
                    spill_cost++;
            }
            if(min > (spill_cost/my_map2.get(i).neighbors.size()) || min == -1)
            {
                to_spill = i;
                min = spill_cost/my_map2.get(i).neighbors.size();
            }
        }
        return to_spill;
    }
    
    void chaitin_algorithm()
    {
        Stack s = new Stack<Integer>();
        String which_step = "Step1";
        
        int size = list_of_infr_graph.size();
        for(int i=0; i<size; i++)
        {
            which_step = "Step1";
            s.clear();
            Map<Integer, String> temp_to_reg = new HashMap();
            List<Integer> set_of_spilled_temps = new ArrayList<>();
        
        while(true)
        {
            //Step 1
            if(which_step.equals("Step1"))
            {
                which_step = "Step2";
                Set<Integer> temp_set = new HashSet();
                temp_set.addAll(list_of_infr_graph.get(i).keySet());
                for(Integer key: temp_set)
                {
                    if(list_of_infr_graph.get(i).get(key).neighbors.size() <18) //18 is for 18 registers s,t
                    {
                        which_step = "Step1";
                        s.push(key);
                        list_of_infr_graph.get(i).remove(key);
                        for (Integer key1 : list_of_infr_graph.get(i).keySet()) //for every node in graph
                        {
                            if(list_of_infr_graph.get(i).get(key1).neighbors.contains(key))
                                list_of_infr_graph.get(i).get(key1).neighbors.remove(key);
                        }
                    }
                }
            }
            
            //Step 2
            if(which_step.equals("Step2"))
            {
                if(list_of_infr_graph.get(i).isEmpty())
                    which_step = "Step3";
                else
                {
                    which_step = "Step1";
                    int vector = heuristic(icl.list_of_graphs.get(i), list_of_infr_graph.get(i));
                    if(gv.list_of_args.get(i)!= null && (!gv.list_of_args.get(i).contains(vector) || vector<4)) 
                    {//more arguments than 4 are already in the stack by caller
                        set_of_spilled_temps.add(vector);
                    }
                    else if(gv.list_of_args.get(i) == null)
                        set_of_spilled_temps.add(vector);
                    list_of_infr_graph.get(i).remove(vector);
                    for (Integer key1 : list_of_infr_graph.get(i).keySet()) //for every node in graph
                    {
                        if(list_of_infr_graph.get(i).get(key1).neighbors.contains(vector))
                            list_of_infr_graph.get(i).get(key1).neighbors.remove(vector);
                    }
                }
            }
        
            //Step 3
            if(which_step.equals("Step3"))
            {
                if(s.empty())
                    break;
                int vector = (int)s.pop();

                String register;
                register = choose_reg(initial_list_of_infr_graph.get(i).get(vector).neighbors, temp_to_reg);
                if(register!=null)
                    temp_to_reg.put(vector, register);
            }         
        }
        spilled_temps.add(set_of_spilled_temps);
        list_of_temp_to_reg.add(i, temp_to_reg);
        }
    }
    
    void interference_graph()
    {
        for(int k=0; k<icl.list_of_graphs.size(); k++)  //for every method
        {
            Map<Integer, InterferenceGraph> infr_graph = new HashMap();
            Map<Integer, InterferenceGraph> initial_infr_graph = new HashMap();
            for(int j=0; j<icl.list_of_graphs.get(k).size(); j++)   //for every instruction
            {
                for(Integer n: icl.list_of_graphs.get(k).get(j).in)
                {
                    if(!infr_graph.containsKey(n))
                    {
                        InterferenceGraph temp = new InterferenceGraph();
                        for(Integer m: icl.list_of_graphs.get(k).get(j).in)
                        {
                            if(m!=n)
                                temp.neighbors.add(m);
                        }
                        infr_graph.put(n, temp);
                        
                    }
                    else
                    {
                        for(Integer m: icl.list_of_graphs.get(k).get(j).in)
                        {
                            if(m!=n)
                                infr_graph.get(n).neighbors.add(m);
                        }
                    }
                    
                }
                for(Integer n: icl.list_of_graphs.get(k).get(j).out)
                {
                    if(!infr_graph.containsKey(n))
                    {
                        InterferenceGraph temp = new InterferenceGraph();
                        for(Integer m: icl.list_of_graphs.get(k).get(j).out)
                        {
                            if(m!=n)
                                temp.neighbors.add(m);
                        }
                        infr_graph.put(n, temp);
                    }
                    else
                    {
                        for(Integer m: icl.list_of_graphs.get(k).get(j).out)
                        {
                            if(m!=n)
                                infr_graph.get(n).neighbors.add(m);
                        }
                    }
                }
                
            }
            list_of_infr_graph.add(k, infr_graph);
            initial_infr_graph.putAll(infr_graph);
            initial_list_of_infr_graph.add(k, initial_infr_graph);
        }
    }
    
    public InterferenceGraphVisitor(Goal tree) {
        //CtrlFlowGraphVisitor gv = new CtrlFlowGraphVisitor();
        mytree = tree;
        tree.accept(gv, null);
        get_InterfaceClass(gv.icl);
    }

    public void get_InterfaceClass(Interface_Class icl_param)
    {
        icl = icl_param;
    }
    
    public String visit(Goal g, String _)
    {
        //System.out.println("Goal");
            
        g.f1.accept(this, null);
        icl.ctrl_flow_graph = icl.list_of_graphs.get(procedure_num);

        if(g.f3.present())
        {
            g.f3.accept(this, null);
        }        
        interference_graph();
        chaitin_algorithm();
        return null;
    }
    
    public String visit(Stmt s, String _)
    {
        //System.out.println("Stmt");
        s.f0.choice.accept(this, null);
        i++;
        return null;
    }
    
    public String visit(StmtExp se, String _)
    {
        //System.out.println("StmtExp");
        i=0;
        procedure_num++;
        se.f1.accept(this, null);
        {
        icl.ctrl_flow_graph = icl.list_of_graphs.get(procedure_num);
            gv.set_in_out(icl);
        }
        return null;
    }
    
    public String visit(HLoadStmt hl, String _)
    {
        //System.out.println("HLoadStmt");
        int temp = Integer.parseInt(hl.f1.accept(this, null));
        
        if(!icl.list_of_graphs.get(procedure_num).get(i).out.contains(temp))
        {
            icl.list_of_graphs.get(procedure_num).get(i).def.clear();
            icl.list_of_graphs.get(procedure_num).get(i).def.add(-1);
        }
        return null;
    }
            
    public String visit(MoveStmt m, String _)
    {
        //System.out.println("MoveStmt");        
        int temp = Integer.parseInt(m.f1.accept(this, null));
        if(!icl.list_of_graphs.get(procedure_num).get(i).out.contains(temp))
        {
            changes = true;
            icl.list_of_graphs.get(procedure_num).get(i).def.clear();
            icl.list_of_graphs.get(procedure_num).get(i).def.add(-1);
        }
        
        
        return null;
    }
    
    public String visit(Temp t, String from)
    {
        //System.out.println("Temp");
        return t.f1.f0.toString();
    }
}
