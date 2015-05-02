import java.io.IOException;
import java.io.InputStream;

class Compilers_hw1 {

    private int lookaheadToken;

    private InputStream in;

    public Compilers_hw1(InputStream in) throws IOException {
	this.in = in;
	lookaheadToken = in.read();
    }

    private int evalDigit(int digit){
	return digit - '0';
    }
    private void consume(int symbol) throws IOException, ParseError {
	if (lookaheadToken != symbol)
        {
            //System.out.println("I am in consume");
	    throw new ParseError();
        }
	lookaheadToken = in.read();
    }

    private String Exp() throws IOException, ParseError {
        String after1 = Term();
        String after = Exp2(after1);
        if(after == null)
        {
            //System.out.println("I am in Exp");
            throw new ParseError();
        }
        else return after;
    }
    
    
    public String Term() throws IOException, ParseError {
        String after1 = Factor();
        String after = Term2(after1);
        
        if(after == null)
        {
            //System.out.println("I am in Term");
            throw new ParseError();
        }
        else
            return after;
    }
    
    
    public String Exp2(String before) throws IOException, ParseError {
        if(lookaheadToken == '+' || lookaheadToken == '-')
        {
            String op;
            if(lookaheadToken == '+')
                op = "+";
            else
                op = "-";
            consume(lookaheadToken);
            String rightOperand;
            if((rightOperand = Term()) != null)
            {
                String res;
                if((before.charAt(before.length() - 1)!= ')') && before.length() > 1)
                    res = before.substring(0, before.length()-2) + " " + "(" + op + " " + before.charAt(before.length() -1) + " " + rightOperand + ")"; //op is either token '+' or '-'  
                else
                    res = "(" + op + " " + before + " " + rightOperand + ")";
                return Exp2(res);
            }
            else
            {
                //System.out.println("I am in Exp2");
                throw new ParseError();
            }
        }
        return(before);
    }
    
	public String Term2(String before) throws IOException, ParseError {
    	if(lookaheadToken == '*' || lookaheadToken == '/')
		{
			String op;
			if(lookaheadToken == '*')
				op = "*";
			else
				op = "/";
			consume(lookaheadToken);
			String rightOperand;
			if((rightOperand = Factor()) != null)
			{
				String res;
				if((before.charAt(before.length()-1) != ')') && (before.length() > 1))
					res = before.substring(1, before.length()-2) + " " + "(" + op + " " + before.charAt(before.length() -1) + " " + rightOperand + ")"; //op is either token '+' or '-'
				else
					res = "(" + " " + op + " " + before + " " + rightOperand + " " + ")";
				return Term2(res);
			}
			else
			{
				//System.out.println("I am in Term2");
				throw new ParseError();
			}
		}
        return(before);
    }
    
    private String Factor() throws IOException, ParseError {
        if(lookaheadToken == '(')
        {
            consume('(');
            String after = Exp();
            if((after != null) && (lookaheadToken == ')'))
            {
                consume(')');
                return after;
            }
            else
            {
                //System.out.println("I am in Factor");
                throw new ParseError();
            }  
        }
        /* Match factor --> num */
        if(lookaheadToken >= 48 && lookaheadToken <= 57)
        {
            String after = Num();
            return after;
        }
        else
        {
            //System.out.println("I am in Factor - num");
            throw new ParseError();
        }
    }


    private String Num() throws IOException, ParseError {
        if(lookaheadToken >= 48 && lookaheadToken <= 57)
        {
            String after = Integer.toString(evalDigit(lookaheadToken));
            consume(lookaheadToken);
            return after;
        }
        else
        {
            throw new ParseError();
        }
    }
    
    
    public String parse() throws IOException, ParseError {
        String prefix = Exp();
        if (lookaheadToken != '\n' && lookaheadToken != -1)
        {
            //System.out.println("I am in parse");
	    throw new ParseError();
        }
        return prefix;
    }

    public static void main(String[] args) {
	try {
	    Compilers_hw1 parser;
            parser = new Compilers_hw1(System.in);
            System.out.println(parser.parse());
        }
	catch (IOException e) {
	    System.err.println(e.getMessage());
	}
	catch(ParseError err){
	    System.err.println(err.getMessage());
	}
    }
}
