import syntaxtree.*;
import java.io.*;

public class Main {
    public static void main (String [] args){
        if(args.length < 1){
            System.err.println("Usage: java Temporary <inputFile>");
            System.exit(1);
        }
        FileInputStream fis = null;
        PrintWriter out = null;
        for(int i=0; i<args.length; i++)
        {
            try{
                fis = new FileInputStream(args[i]);
                String file = args[i].replaceFirst(".java", ".pg");
                out = new PrintWriter(file);
                
                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal tree = parser.Goal();
                
                ClassNameVisitor cn = new ClassNameVisitor();
                tree.accept(cn, 0);
            
                MyVisitor mv = new MyVisitor( );
                mv.field_decl_checker(cn.allClasses);
                tree.accept(mv, null);
                
                PigletVisitor pv = new PigletVisitor(out);
                pv.get_InterfaceClass(mv.icl);
                tree.accept(pv, null);
            }
            catch(ParseException ex){
                System.out.println(ex.getMessage());
            }
            catch(FileNotFoundException ex){
                System.err.println(ex.getMessage());
            }
            catch(SemException ex)
            { 
                System.out.println(ex.getMessage()+": "+args[i]);
            }
            finally{
            
                try{
                    if(fis != null) fis.close();
                    if(out != null) out.close();
                }
                catch(IOException ex){
                    System.err.println(ex.getMessage());
                }
            }
        }
    }    
}
