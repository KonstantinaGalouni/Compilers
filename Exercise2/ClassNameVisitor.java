import java.util.HashSet;
import java.util.Set;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class ClassNameVisitor extends GJDepthFirst<Set, Integer>{
    public Set<String> allClasses = new HashSet<>(); 
    
    public Set visit(Goal g, Integer _)
    {   
        g.f0.accept(this, null);
        if(g.f1.present())
            g.f1.accept(this, null);    
        return allClasses;
     }
    
    public Set visit(MainClass m, Integer _)
    {
        allClasses.add(m.f1.f0.toString());
        return allClasses;
    }
    
    public Set visit(ClassDeclaration c, Integer _)
    {
        if(allClasses.contains(c.f1.f0.toString()))
        {
            System.out.println("ClassNameVisitor: ClassDeclaration");
            throw new SemException();
        }
        allClasses.add(c.f1.f0.toString());
        return allClasses;
    }
    
    public Set visit(ClassExtendsDeclaration c, Integer _)
    {
        if(allClasses.contains(c.f1.f0.toString()))
        {
            System.out.println("ClassNameVisitor: ClassExtendsDeclaration");
            throw new SemException();
        }
        if(allClasses.contains(c.f3.f0.toString()) == false)
        {
            System.out.println("ClassNameVisitor: ClassExtendsDeclaration");
            throw new SemException();
        }
        allClasses.add(c.f1.f0.toString());
        return allClasses;
    }
}
