import syntaxtree.*;
import java.io.*;

class Main {
    public static void main (String [] args){
        if(args.length < 1){
            System.err.println("Usage: java Temporary <inputFile>");
            System.exit(1);
        }
        FileInputStream fis = null;
        for(int i=0; i<args.length; i++)
        {
            try{
                fis = new FileInputStream(args[i]);
                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal tree = parser.Goal();
                
                ClassNameVisitor cn = new ClassNameVisitor();
                tree.accept(cn, 0);
            
                FieldDeclVisitor fd = new FieldDeclVisitor();
                fd.field_decl_checker(cn.allClasses);
                tree.accept(fd, null);
            
                CheckVisitor cv = new CheckVisitor();
                cv.get_InterfaceClass(fd.icl);
                tree.accept(cv, null);
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
                }
                catch(IOException ex){
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
}
