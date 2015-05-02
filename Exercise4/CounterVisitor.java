import syntaxtree.IntegerLiteral;
import syntaxtree.Label;
import syntaxtree.Temp;
import visitor.GJDepthFirst;

public class CounterVisitor extends GJDepthFirst<String, String>{
    public static int temp = 0;
    public static int label = 0;
    
    public String visit(Temp t, String _)
    {
        int temp_new;
        temp_new = Integer.parseInt(t.f1.accept(this, null));
        if(temp_new > temp)
            temp = temp_new;
        return null;
    }
    
    public String visit(Label l, String _)
    {
        int label_new;
        String substr;
        substr = l.f0.toString().substring(1);
        if(substr.startsWith("0") || substr.startsWith("1") || substr.startsWith("2") 
                || substr.startsWith("3") || substr.startsWith("4") 
                || substr.startsWith("5") || substr.startsWith("6") 
                || substr.startsWith("7") || substr.startsWith("8") || substr.startsWith("9"))
        {
            label_new = Integer.parseInt(substr);
            if(label_new > label)
                label = label_new;
        }
        return null;
    }
    
    public String visit(IntegerLiteral i, String _)
    {
        return i.f0.toString();
    }
}
