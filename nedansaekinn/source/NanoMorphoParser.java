//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "nanomorpho.byaccj"
	import java.io.*;
	import java.util.*;
//#line 20 "NanoMorphoParser.java"




public class NanoMorphoParser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class NanoMorphoParserVal is defined in NanoMorphoParserVal.java


String   yytext;//user variable to return contextual strings
NanoMorphoParserVal yyval; //used to return semantic vals from action routines
NanoMorphoParserVal yylval;//the 'lval' (result) I got from yylex()
NanoMorphoParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new NanoMorphoParserVal[YYSTACKSIZE];
  yyval=new NanoMorphoParserVal();
  yylval=new NanoMorphoParserVal();
  valptr=-1;
}
void val_push(NanoMorphoParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
NanoMorphoParserVal val_pop()
{
  if (valptr<0)
    return new NanoMorphoParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
NanoMorphoParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new NanoMorphoParserVal();
  return valstk[ptr];
}
final NanoMorphoParserVal dup_yyval(NanoMorphoParserVal val)
{
  NanoMorphoParserVal dup = new NanoMorphoParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short RETURN=257;
public final static short OPERATOR=258;
public final static short AND=259;
public final static short OR=260;
public final static short NOT=261;
public final static short LITERAL=262;
public final static short NAME=263;
public final static short IF=264;
public final static short ELSE=265;
public final static short ELSIF=266;
public final static short WHILE=267;
public final static short VAR=268;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,   19,    2,    6,    6,    7,    7,   14,
   14,   14,   15,   15,   16,   16,    4,    4,    5,    5,
    5,    5,    5,    8,    8,    8,    8,    8,    8,    8,
    8,    8,    9,   10,   11,   18,   17,   12,   13,   13,
   13,    3,
};
final static short yylen[] = {                            2,
    1,    2,    1,    0,    9,    0,    1,    3,    1,    0,
    3,    1,    0,    3,    3,    2,    0,    3,    3,    1,
    1,    1,    1,    1,    4,    3,    2,    2,    1,    3,
    1,    5,    3,    2,    3,    1,    1,    6,    0,    6,
    2,    3,
};
final static short yydefred[] = {                         4,
    0,    0,    3,    0,    2,   37,    0,    0,   12,    0,
    0,    0,   13,   11,    0,    0,    0,    0,   16,    0,
   36,    0,   29,    0,    0,    0,    0,    5,    0,   20,
   21,   22,   23,   31,    0,    0,   14,    0,   34,    0,
    0,    0,    0,    0,    0,    0,    0,   18,   28,   15,
    0,    0,    0,    0,    0,    0,   30,   19,   33,   35,
   25,    0,    0,    0,    0,   17,    0,   32,    0,    0,
    0,   38,   42,   41,    0,    0,    0,    0,   40,
};
final static short yydgoto[] = {                          1,
    2,    3,   67,   17,   29,   53,   54,   30,   31,   32,
   33,   34,   72,   10,   15,   18,    7,   35,    4,
};
final static short yysindex[] = {                         0,
    0,    0,    0, -256,    0,    0,  -28, -245,    0,  -30,
  -84, -218,    0,    0, -219, -213,  -40,  -39,    0,  -16,
    0,  -16,    0,  -36,   13,   16,  -16,    0,  -57,    0,
    0,    0,    0,    0,   -8, -209,    0, -195,    0,  -16,
  -16,  -16,  -16,   11,   -8,  -16,  -16,    0,    0,    0,
 -195, -195,   18,   23,   14,   17,    0,    0,    0,    0,
    0,  -16,  -63,  -63, -195,    0, -236,    0,  -32,  -63,
   28,    0,    0,    0,  -16,   21,  -63, -236,    0,
};
final static short yyrindex[] = {                         0,
    0,    1,    0,    0,    0,    0,    0,  -18,    0,    0,
    0,    0,    0,    0,  -24,    0,    0,    0,    0,    0,
    0,    0,    0,    2,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  -38,    0,    0,
   30,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  -31,  -10,    0,   31,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    3,    0,    7,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    7,    0,
};
final static short yygindex[] = {                         0,
    0,   67,  -37,    8,   -5,    0,    0,  -26,    0,    0,
    0,    0,   -3,    0,    0,    0,    0,    0,    0,
};
final static int YYTABLESIZE=281;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         27,
    1,   48,   27,   41,   36,   27,    6,   27,   49,   26,
   11,    8,   26,   12,   38,   17,   39,    9,   58,   37,
   27,   44,   10,   27,   40,   10,   68,   26,   70,   71,
    9,   27,   74,    9,   51,   52,   55,   56,   13,   78,
   59,   60,   24,    8,   14,   24,    8,   39,   16,   19,
   39,   57,   42,   50,   63,   43,   65,   64,   61,   66,
   24,   77,   45,   46,   47,   39,   62,   75,    5,   76,
    6,    7,    0,   69,   79,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   28,    0,    0,    0,    0,    0,
    0,    0,   73,    0,    0,    0,    0,    0,    0,    0,
   17,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   45,   46,   47,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   20,   21,    0,    0,
   22,   23,   24,   25,   20,   21,   26,    0,   22,   23,
   24,   25,   17,   17,   26,    0,   17,   17,   17,   17,
   20,   21,   17,    0,   22,   23,   24,   25,   20,   21,
   26,    0,    0,   23,   24,   25,    0,    0,   26,   24,
   24,   24,    0,    4,   39,   39,   39,    0,   45,   46,
   47,   45,   46,   47,   45,   46,   47,    0,   45,   46,
   47,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
    0,   59,   41,   40,   44,   44,  263,   40,   35,   41,
   41,   40,   44,   44,   20,   40,   22,  263,   45,   59,
   59,   27,   41,   40,   61,   44,   64,   59,  265,  266,
   41,   40,   70,   44,   40,   41,   42,   43,  123,   77,
   46,   47,   41,   41,  263,   44,   44,   41,  268,  263,
   44,   41,   40,  263,   41,   40,   62,   41,   41,  123,
   59,   41,  258,  259,  260,   59,   44,   40,    2,   75,
   41,   41,   -1,   66,   78,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  125,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  125,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  125,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  258,  259,  260,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  257,  258,   -1,   -1,
  261,  262,  263,  264,  257,  258,  267,   -1,  261,  262,
  263,  264,  257,  258,  267,   -1,  261,  262,  263,  264,
  257,  258,  267,   -1,  261,  262,  263,  264,  257,  258,
  267,   -1,   -1,  262,  263,  264,   -1,   -1,  267,  258,
  259,  260,   -1,  263,  258,  259,  260,   -1,  258,  259,
  260,  258,  259,  260,  258,  259,  260,   -1,  258,  259,
  260,
};
}
final static short YYFINAL=1;
final static short YYMAXTOKEN=268;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'",null,null,"','",
null,null,null,null,null,null,null,null,null,null,null,null,null,null,"';'",
null,"'='",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"RETURN","OPERATOR","AND","OR","NOT",
"LITERAL","NAME","IF","ELSE","ELSIF","WHILE","VAR",
};
final static String yyrule[] = {
"$accept : start",
"start : program",
"program : program function",
"program : function",
"$$1 :",
"function : $$1 fname '(' ids ')' '{' decls exprs '}'",
"args :",
"args : nonEmptyArgs",
"nonEmptyArgs : nonEmptyArgs ',' expr",
"nonEmptyArgs : expr",
"ids :",
"ids : ids ',' NAME",
"ids : NAME",
"decls :",
"decls : decls decl ';'",
"decl : decl ',' NAME",
"decl : VAR NAME",
"exprs :",
"exprs : exprs expr ';'",
"expr : expr OPERATOR smallexpr",
"expr : smallexpr",
"expr : andexpr",
"expr : notexpr",
"expr : orexpr",
"smallexpr : NAME",
"smallexpr : NAME '(' args ')'",
"smallexpr : NAME '=' expr",
"smallexpr : RETURN expr",
"smallexpr : op smallexpr",
"smallexpr : LITERAL",
"smallexpr : '(' expr ')'",
"smallexpr : ifexpr",
"smallexpr : WHILE '(' expr ')' body",
"andexpr : expr AND expr",
"notexpr : NOT expr",
"orexpr : expr OR expr",
"op : OPERATOR",
"fname : NAME",
"ifexpr : IF '(' expr ')' body elsebody",
"elsebody :",
"elsebody : ELSIF '(' expr ')' body elsebody",
"elsebody : ELSE body",
"body : '{' exprs '}'",
};

//#line 139 "nanomorpho.byaccj"
	
    // Eftirfarandi fastar standa fyrir allar þær
    // mögulegu gerðir af segðum sem milliþula
    // (intermediate code) getur innihaldið.
    enum CodeType {
        NAME, ASSIGN, CALL, RETURN, OP, LITERAL, IF, WHILE, ELSE, PRIOR, AND, OR, NOT
    };


	private int varCount;
	private HashMap<String,Integer> varTable;
    static private String name;
    private static NanoMorphoLexer lexer;
    private static String lexeme;
    private static PrintWriter writer;
    // Inni í hverri fallsskilgreiningu inniheldur vars nöfnin
    // á viðföngunum í fallið (þ.e. leppunum eða breytunöfnunum
    // sem standa fyrir viðföngin), í sætum 1 og aftar.  Sæti
    // 0 inniheldur nafn fallsins sem verið er að skilgreina.
    private static String[] vars;

	private void addVar( String name )
	{
		if( varTable.get(name) != null )
			yyerror("Variable already exists");
		varTable.put(name,varCount++);
	}

	private int findVar( String name )
	{
		Integer res = varTable.get(name);
		if( res == null )
			yyerror("Variable does not exist");
		return res;
	}

	int last_token_read,line,column;

	private int yylex()
	{
		int yyl_return = -1;
		try
		{
			yylval = null;
			last_token_read = yyl_return = lexer.yylex();
			line = lexer.getLine()+1;
			column = lexer.getColumn()+1;
			if( yylval==null )
				yylval = new NanoMorphoParserVal(NanoMorphoParser.yyname[yyl_return]);
			//System.out.println("Token: "+MorphoParser.yyname[yyl_return]+" Lexeme: "+yylval.sval);
		}
		catch (IOException e)
		{
			System.err.println("IO error: "+e);
		}
		return yyl_return;
	}

	public void yyerror( String error )
	{
		System.out.println("Error:  "+error);
		System.out.println("Token:  "+NanoMorphoParser.yyname[last_token_read]);
		System.out.println("Line:   "+line);
		System.out.println("Column: "+column);
		System.exit(1);
	}

	public NanoMorphoParser( Reader r )
	{
		lexer = new NanoMorphoLexer(r,this);
	}

	public static void main( String args[] )
	  	throws IOException
	{
		NanoMorphoParser yyparser = new NanoMorphoParser(new FileReader(args[0]));
		name = args[0].substring(0,args[0].lastIndexOf('.'));

		writer = new PrintWriter(name+".masm", "UTF-8");    
        yyparser.yyparse();
        writer.close();
		
	}

	// Use: emit(line);
    // Pre:  line er lína í lokaþulu.
    // Post:  Búið er að skrifa línuna á aðalúttak.
    private static void emit( String line ) {
        writer.println(line);
    }

   private static void generateProgram(String name, Object[] p) {
        // p = {function0, function1, ..., functionN}
        emit("\""+name+".mexe\" = main in");
        emit("!{{");
        for( int i=0; i!=p.length; i++ ) generateFunction((Object[])p[i]);
        emit("}}*BASIS;");
    }

    private static void generateFunction(Object[] f) {
        // f = {fname, argcount, varcount, expr0, expr1, ..., exprN}
        String fname = (String)f[0];
        int argcount = (Integer)f[1];
        int varcount = (Integer)f[2];
        emit("#\""+fname+"[f"+argcount+"]\" = ");
        emit("[");
        // Fetch the values from the var declarations and put them on the stack:
        // If there are n arguments to the function, the declarations must be
        // n+1, n+2, ... , n+numVarDeclarations
        for(int i=argcount; i<argcount+varcount; i++) {
            emit("(MakeVal null)");
            emit("(Push)");
        }
        for(int i=3; i!=f.length; i++) {
        	generateExpr((Object[])f[i]);
        }
        emit("(Return)");
        emit("];");
    }


    static int nextLab = 1;

    // Notkun: int i = newLab();
    // Eftir:  i er jákvæð heiltala sem ekki hefur áður
    //         verið skilað úr þessu falli.  Tilgangurinn
    //         er að búa til nýtt merki (label), sem er
    //         ekki það sama og neitt annað merki.
    static int newLab() {
        return nextLab++;
    }

    private static void generateExpr(Object[] e) {
        // e = {expr} eða e = {expr, op, expr, op, expr, ... , op, expr}
        // generateSmallExpr()
        // (Push)
        // generateSmallExpr();
        // (Call #opf[2] 2)
        // (Push) // Setja gildið í accumulator
        // generateSmallExpr();
        // (Call #opf[2] 2)
        // ...
        // (Call #opf[2] 2) 
        generateSmallExpr((Object[])e[0]); // Alltaf fyrir fyrsta expr
        if(e.length == 1) return;
        for(int i=1; i!=e.length-1; i++) {
            // Put our last expr on the stack
            emit("(Push)");
            String op = e[i].toString();
            generateSmallExpr((Object[])e[i+1]);
            emit("(Call #\""+op+"[f"+2+"]\" "+2+")");
        }
    }

    // Needs to handle: NAME, ASSIGN, CALL, RETURN, OP, LITERAL, IF, WHILE
    private static void generateSmallExpr(Object[] e) {
        // e = {CodeType, ...}
        switch( (CodeType)e[0] ) {
            case NAME:
                // e = {NAME,nameLoc}
                /* CODE:
                    (Fetch nameLoc)
                 */
                emit("(Fetch "+e[1]+")");
                return;
            case LITERAL:
                // e = {LITERAL,literal}
                /* CODE:
                    (MakeVal literal)
                 */
                emit("(MakeVal "+(String)e[1]+")");
                return;
            case ASSIGN:
                // e = {ASSIGN, argloc, expr()}
                /* CODE:
                    Code(expr()) -> stored in ac
                    (Store argloc)
                 */
                generateExpr((Object[])e[2]);
                emit("(Store "+e[1]+")");
                return;
            case CALL:
                // e = {CALL,name,args}
                /* CODE:
                    Code(args[0]) -> Stored in ac
                    (Push)  -> Pushed to stack
                    Code(args[1]) -> Stored in ac
                    (Push)-> Pushed to stack
                    .
                    .
                    .
                    Code(args[args.length-1])
                    (Push)
                    (Call #"name[fargs.length]" args.length)
                 */
                Object[] args = (Object[])e[2];
                int i;
                for( i=0 ; i!=args.length ; i++ )
                    if( i==0 )
                        generateExpr((Object[])args[i]);
                    else
                        generateExprP((Object[])args[i]);
                emit("(Call #\""+e[1]+"[f"+i+"]\" "+i+")");
                return;
            case RETURN:
                // e = {RETURN, expr}
                /* CODE
                Code(expr())
                (Return)
                 */ 
                generateExpr((Object[])e[1]);
                emit("(Return)");
                return;
            case OP:
                // e = {OP, OPsymbol, expr}
                /* CODE
                Code(expr()) -> ac
                (Call "OPsymbol[f1]" 1)
                 */
                generateSmallExpr((Object[])e[2]);
                emit("(Call \""+e[1]+"[f1]\" "+1);
                return;
            case AND: 
            	// e = {AND, e1, e2}
            	int labFalse = newLab();
            	generateExpr((Object[])e[1]);
            	emit("(GoFalse _"+labFalse+")");
            	generateExpr((Object[])e[2]);
            	emit("_"+labFalse+":");
            	return;
            case OR:
            	// e = {OR, e1, e2}
            	int labTrue = newLab();
            	generateExpr((Object[])e[1]);
            	emit("(GoTrue _"+labTrue+")");
            	generateExpr((Object[])e[2]);
            	emit("_"+labTrue+":");
            	return;
            case NOT:
            	// e = {NOT, e}
            	generateExpr((Object[])e[1]);
            	emit("(Not)");
            	return;
            case PRIOR:
                // e = {PRIOR, expr}
                generateExpr((Object[])e[1]);
                return;
            case IF:
                // e = {IF,cond,then,else}
                // Viljum að then expr sé annað if else expr ef um elsif er að ræða
                /* CODE
                        Code(cond)
                        (GoFalse _L1)
                        Code(then)
                        (Go _L2)
                   _L1:
                        Code(else)
                   _L2:

                 */
                int labElse = newLab();
                int labEnd = newLab();
                generateJump((Object[])e[1],0,labElse); // check condition
                generateBody((Object[])e[2]); // 'then' expr if condition was true (pos 0)
                emit("(Go _"+labEnd+")");
                emit("_"+labElse+":");
                generateSmallExpr((Object[])e[3]); // 'else' expr if condition was false.note this wasn't small expr!
                emit("_"+labEnd+":");
                return;
            case ELSE:
                generateBody((Object[])e[1]);
                return;
            case WHILE:
                // e = {WHILE, cond, body}
                /* CODE:
                    _L1:
                        Code(cond)
                        (GoFalse _L2)
                        Code(body)
                        (Go _L1)
                    _L2:
                 */
                int labStart = newLab();
                int labQuit = newLab();
                emit("_"+labStart+":"); // start of while loop
                generateJump((Object[])e[1], 0, labQuit);
                generateBody((Object[])e[2]);
                emit("(Go _"+labStart+")");
                emit("_"+labQuit+":");
                return;
        }
    }

    private static void generateBody(Object[] e) {
        // e = {expr0, expr1, ..., exprN}
        if(e == null) return;
        for(int i=0; i<e.length; i++) {
            generateExpr((Object[])e[i]);
        }
    }


    // Notkun: generateJump(e,labTrue,labTrue);
    // Fyrir:  e er milliþula fyrir segð, labTrue og
    //         labFalse eru heiltölur sem standa fyrir
    //         merki eða eru núll.
    // Eftir:  Búið er að skrifa lokaþulu fyrir segðina
    //         á aðalúttak.  Lokaþulan veldur stökki til
    //         merkisins labTrue ef segðina skilar sönnu,
    //         annars stökki til labFalse.  Ef annað merkið
    //         er núll þá er það jafngilt merki sem er rétt
    //         fyrir aftan þulu segðarinnar.
    private static void generateJump( Object[] e, int labTrue, int labFalse ) {
        generateExpr(e);
        if( labTrue!=0 ) emit("(GoTrue _"+labTrue+")");
        if( labFalse!=0 ) emit("(GoFalse _"+labFalse+")");
    }

    // Notkun: generateJumpP(e,labTrue,labFalse);
    // Fyrir:  e er milliþula fyrir segð, labTrue og
    //         labFalse eru heiltölur sem standa fyrir
    //         merki eða eru núll.
    // Eftir:  Þetta kall býr til lokaþulu sem er jafngild
    //         þulunni sem köllin
    //            emit("(Push)");
    //            generateJump(e,labTrue,labFalse);
    //         framleiða.  Þulan er samt ekki endilega sú
    //         sama og þessi köll framleiða því tilgangurinn
    //         er að geta framleitt betri þulu.
    private static void generateJumpP( Object[] e, int labTrue, int labFalse ) {
        switch( (CodeType)e[0] )
        {
            case LITERAL:
                String literal = (String)e[1];
                emit("(Push)");
                if( literal.equals("false") || literal.equals("null") )
                {
                    if( labFalse!=0 ) emit("(Go _"+labFalse+")");
                    return;
                }
                if( labTrue!=0 ) emit("(Go _"+labTrue+")");
                return;
            default:
                generateExprP(e);
                if( labTrue!=0 ) emit("(GoTrue _"+labTrue+")");
                if( labFalse!=0 ) emit("(GoFalse _"+labFalse+")");
        }
    }

    // Notkun: generateExprP(e);
    // Fyrir:  e er milliþula fyrir segð.
    // Eftir:  Þetta kall býr til lokaþulu sem er jafngild
    //         þulunni sem köllin
    //            emit("(Push)");
    //            generateExpr(e);
    //         framleiða.  Þulan er samt ekki endilega sú
    //         sama og þessi köll framleiða því tilgangurinn
    //         er að geta framleitt betri þulu.
    private static void generateExprP( Object[] e ) {
        switch( (CodeType)e[0] )
        {
            case NAME:
                // e = {NAME,name}
                emit("(FetchP "+e[1]+")");
                return;
            case LITERAL:
                // e = {LITERAL,literal}
                emit("(MakeValP "+(String)e[1]+")");
                return;
            case IF:
                // e = {IF,cond,then,else}
                int labElse = newLab();
                int labEnd = newLab();
                generateJumpP((Object[])e[1],0,labElse);
                generateExpr((Object[])e[2]);
                emit("(Go _"+labEnd+")");
                emit("_"+labElse+":");
                generateExpr((Object[])e[3]);
                emit("_"+labEnd+":");
                return;
            case CALL:
                // e = {CALL,name,args}
                Object[] args = (Object[])e[2];
                int i;
                for( i=0 ; i!=args.length ; i++ ) generateExprP((Object[])args[i]);
                if( i==0 ) emit("(Push)");
                emit("(Call #\""+e[1]+"[f"+i+"]\" "+i+")");
                return;
        }
    }

//#line 685 "NanoMorphoParser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 25 "nanomorpho.byaccj"
{ generateProgram(name,((Vector<Object>)(val_peek(0).obj)).toArray()); }
break;
case 2:
//#line 29 "nanomorpho.byaccj"
{ ((Vector<Object>)(val_peek(1).obj)).add(val_peek(0).obj); yyval.obj=val_peek(1).obj; }
break;
case 3:
//#line 30 "nanomorpho.byaccj"
{ yyval.obj=new Vector<Object>(); ((Vector<Object>)(yyval.obj)).add(val_peek(0).obj); }
break;
case 4:
//#line 34 "nanomorpho.byaccj"
{
			varCount = 0;
			varTable = new HashMap<String,Integer>();
		}
break;
case 5:
//#line 39 "nanomorpho.byaccj"
{
			/* Function intermediate is e = {fname, argcount, varcount, expr0, expr1, ... , exprN}*/
			Vector<Object> funcIm = new Vector<Object>();
			funcIm.add(val_peek(7).sval); /* Add fname*/
			funcIm.add(val_peek(5).ival); /* Add ids (number of arguments)*/
			funcIm.add(val_peek(2).ival); /* Add decls*/

            Object[] allExprs = (((Vector<Object>)(val_peek(1).obj)).toArray());
			for(Object expr : allExprs) funcIm.add(expr);
			yyval.obj=funcIm.toArray();
		}
break;
case 6:
//#line 53 "nanomorpho.byaccj"
{yyval.obj = new Vector<Object>();}
break;
case 7:
//#line 54 "nanomorpho.byaccj"
{yyval.obj=val_peek(0).obj;}
break;
case 8:
//#line 58 "nanomorpho.byaccj"
{ ((Vector<Object>)(val_peek(2).obj)).add(((Vector<Object>)(val_peek(0).obj)).toArray()); yyval.obj=val_peek(2).obj; }
break;
case 9:
//#line 59 "nanomorpho.byaccj"
{ yyval.obj=new Vector<Object>(); ((Vector<Object>)(yyval.obj)).add(((Vector<Object>)(val_peek(0).obj)).toArray());}
break;
case 10:
//#line 63 "nanomorpho.byaccj"
{ yyval.ival=0; }
break;
case 11:
//#line 64 "nanomorpho.byaccj"
{ addVar(val_peek(0).sval); yyval.ival=val_peek(2).ival+1; }
break;
case 12:
//#line 65 "nanomorpho.byaccj"
{addVar(val_peek(0).sval); yyval.ival=yyval.ival+1;}
break;
case 13:
//#line 69 "nanomorpho.byaccj"
{ yyval.ival=0;}
break;
case 14:
//#line 70 "nanomorpho.byaccj"
{ yyval.ival=val_peek(2).ival+val_peek(1).ival; }
break;
case 15:
//#line 74 "nanomorpho.byaccj"
{ addVar(val_peek(0).sval); yyval.ival=val_peek(2).ival+1; }
break;
case 16:
//#line 75 "nanomorpho.byaccj"
{addVar(val_peek(0).sval); yyval.ival=1;}
break;
case 17:
//#line 79 "nanomorpho.byaccj"
{yyval.obj=new Vector<Object>();}
break;
case 18:
//#line 80 "nanomorpho.byaccj"
{ ((Vector<Object>)(val_peek(2).obj)).add(((Vector<Object>)(val_peek(1).obj)).toArray()); yyval.obj=val_peek(2).obj;}
break;
case 19:
//#line 84 "nanomorpho.byaccj"
{ ((Vector<Object>)(val_peek(2).obj)).add(val_peek(1).sval); ((Vector<Object>)(val_peek(2).obj)).add(val_peek(0).obj); yyval.obj=val_peek(2).obj;}
break;
case 20:
//#line 85 "nanomorpho.byaccj"
{ yyval.obj=new Vector<Object>(); ((Vector<Object>)(yyval.obj)).add(val_peek(0).obj);}
break;
case 21:
//#line 86 "nanomorpho.byaccj"
{ yyval.obj=new Vector<Object>(); ((Vector<Object>)(yyval.obj)).add(val_peek(0).obj);}
break;
case 22:
//#line 87 "nanomorpho.byaccj"
{ yyval.obj=new Vector<Object>(); ((Vector<Object>)(yyval.obj)).add(val_peek(0).obj);}
break;
case 23:
//#line 88 "nanomorpho.byaccj"
{ yyval.obj=new Vector<Object>(); ((Vector<Object>)(yyval.obj)).add(val_peek(0).obj);}
break;
case 24:
//#line 93 "nanomorpho.byaccj"
{ yyval.obj = new Object[]{CodeType.NAME, findVar(val_peek(0).sval)};}
break;
case 25:
//#line 94 "nanomorpho.byaccj"
{ yyval.obj = new Object[]{CodeType.CALL, val_peek(3).sval, ((Vector<Object>)(val_peek(1).obj)).toArray()};}
break;
case 26:
//#line 95 "nanomorpho.byaccj"
{ yyval.obj = new Object[]{CodeType.ASSIGN, findVar(val_peek(2).sval), ((Vector<Object>)(val_peek(0).obj)).toArray()};}
break;
case 27:
//#line 96 "nanomorpho.byaccj"
{ yyval.obj = new Object[]{CodeType.RETURN, ((Vector<Object>)(val_peek(0).obj)).toArray()};}
break;
case 28:
//#line 97 "nanomorpho.byaccj"
{ yyval.obj = new Object[]{CodeType.OP, val_peek(1).sval, val_peek(0).obj};}
break;
case 29:
//#line 98 "nanomorpho.byaccj"
{ yyval.obj = new Object[]{CodeType.LITERAL, val_peek(0).sval};}
break;
case 30:
//#line 99 "nanomorpho.byaccj"
{ yyval.obj = new Object[]{CodeType.PRIOR, ((Vector<Object>)(val_peek(1).obj)).toArray()};}
break;
case 31:
//#line 100 "nanomorpho.byaccj"
{ yyval.obj = val_peek(0).obj;}
break;
case 32:
//#line 101 "nanomorpho.byaccj"
{yyval.obj = new Object[]{CodeType.WHILE, ((Vector<Object>)(val_peek(2).obj)).toArray(), val_peek(0).obj};}
break;
case 33:
//#line 105 "nanomorpho.byaccj"
{ yyval.obj = new Object[]{CodeType.AND, ((Vector<Object>)(val_peek(2).obj)).toArray(), ((Vector<Object>)(val_peek(0).obj)).toArray()};}
break;
case 34:
//#line 109 "nanomorpho.byaccj"
{ yyval.obj = new Object[]{CodeType.NOT, ((Vector<Object>)(val_peek(0).obj)).toArray()};}
break;
case 35:
//#line 113 "nanomorpho.byaccj"
{ yyval.obj = new Object[]{CodeType.OR, ((Vector<Object>)(val_peek(2).obj)).toArray(), ((Vector<Object>)(val_peek(0).obj)).toArray()};}
break;
case 36:
//#line 117 "nanomorpho.byaccj"
{yyval.sval = val_peek(0).sval;}
break;
case 37:
//#line 121 "nanomorpho.byaccj"
{yyval.sval = val_peek(0).sval;}
break;
case 38:
//#line 125 "nanomorpho.byaccj"
{yyval.obj = new Object[]{CodeType.IF, ((Vector<Object>)(val_peek(3).obj)).toArray(), val_peek(1).obj, val_peek(0).obj};}
break;
case 39:
//#line 129 "nanomorpho.byaccj"
{yyval.obj = new Object[]{CodeType.ELSE, null};}
break;
case 40:
//#line 130 "nanomorpho.byaccj"
{yyval.obj = new Object[]{CodeType.IF, ((Vector<Object>)(val_peek(3).obj)).toArray(), val_peek(1).obj, val_peek(0).obj};}
break;
case 41:
//#line 131 "nanomorpho.byaccj"
{yyval.obj = new Object[]{CodeType.ELSE, val_peek(0).obj};}
break;
case 42:
//#line 135 "nanomorpho.byaccj"
{yyval.obj = ((Vector<Object>)(val_peek(1).obj)).toArray();}
break;
//#line 1015 "NanoMorphoParser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public NanoMorphoParser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public NanoMorphoParser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
