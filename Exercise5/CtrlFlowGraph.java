import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CtrlFlowGraph{
    public Set<Integer> prev = new HashSet<>();
    public Set<Integer> next = new HashSet<>();
    public Set<Integer> def = new HashSet<>();
    public Set<Integer> use = new HashSet<>();
    public Set<Integer> in = new HashSet<>();
    public Set<Integer> out = new HashSet<>();
}
