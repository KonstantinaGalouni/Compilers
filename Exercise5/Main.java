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
                String file = args[i].replaceFirst(".spg", ".kg");
                //System.out.println(file);
                out = new PrintWriter(file);
                
                SpigletParser parser = new SpigletParser(fis);
                Goal tree = parser.Goal();
                
                InterferenceGraphVisitor igv = new InterferenceGraphVisitor(tree);
                tree.accept(igv, null);
                
                KangaVisitor kv = new KangaVisitor(out, igv);
                tree.accept(kv, null);
            }
            catch(ParseException ex){
                System.out.println(ex.getMessage());
            }
            catch(FileNotFoundException ex){
                System.err.println(ex.getMessage());
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
