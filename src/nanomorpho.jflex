/**
	JFlex lesgreinir fyrir NanoMorpho.
	Höfundar:	Andrea Björk Björnsdóttir
				Björn Bjarnsteinsson
				Leó Jóhannsson
				Febrúar 2016
				
	Smíðað eftir beinagrind fyrir NanoLisp eftir Snorra Agnarsson

	Þennan lesgreini má þýða með skipununum
		java -jar JFlex.jar nanomorpho.jflex
		javac NanoMorphoLexer.java
	
	Getum líka linkað bin/jflex með
	% 		ln -s /path/to/jflex/bin/ /usr/bin/jflex
	
	og keyrt lesgreininn einfaldlega með
		jflex nanomorpho.jflex
		javac NanoMorphoLexer.java
	
 */
%%

%public
%class NanoMorphoLexer
%line
%column

%unicode

%{

public int getLine() { return yyline; }
public int getColumn() { return yycolumn; }

%}

%{

/*public NanoMorphoParser yyparser;

public NanoMorphoLexer( java.io.Reader r)
{
	this(r);
	this.yyparser = yyparser;
}*/

static public final int IF = 1;
static public final int NAME = 2;
static public final int LITERAL = 4;
static public final int VAR = 5;
static public final int WHILE = 6;
static public final int ELSIF = 7;
static public final int ELSE = 8;
static public final int RETURN = 9;
static public final int OPERATOR = 10;

static public final int ERR = -1;
static public final int EOF = -2;
static private Yytoken next_token;
static private NanoMorphoLexer lexer;

static private void advance()
	{
		try
		{
			next_token = lexer.yylex();
		}
		catch (Exception e)
		{
			throw new Error(e);
		}
		if( next_token == null )
		{
			next_token = new Yytoken(NanoMorphoLexer.EOF,"EOF");
		}
	}

static public void main( String args[] )
	{
		try {
		
			lexer = new NanoMorphoLexer(new java.io.FileReader(args[0]));
			java.io.BufferedWriter out = new java.io.BufferedWriter(new java.io.FileWriter("lexemes"));
			
			advance();
			
			while( next_token.number != NanoMorphoLexer.EOF) {
				out.write(next_token.number+": "+next_token);
				out.newLine();
				advance();
			}
			out.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

%}

  /* Reglulegar skilgreiningar */

  /* Regular definitions */

_DIGIT=[0-9]
_FLOAT={_DIGIT}+\.{_DIGIT}+([eE][+-]?{_DIGIT}+)?
_INT={_DIGIT}+
_STRING=\"([^\"\\]|\\b|\\t|\\n|\\f|\\r|\\\"|\\\'|\\\\|(\\[0-3][0-7][0-7])|\\[0-7][0-7]|\\[0-7])*\"
_CHAR=\'([^\'\\]|\\b|\\t|\\n|\\f|\\r|\\\"|\\\'|\\\\|(\\[0-3][0-7][0-7])|(\\[0-7][0-7])|(\\[0-7]))\'
_DELIM=[()\}\{,;=]
_OPERATOR=[\+\-*/!%&><\:\^\~&|?=]+
_NAME=([:letter:]|[]|{_DIGIT})+

%%

{_DELIM} {
	return new Yytoken(yycharat(0));
}

{_STRING} | {_FLOAT} | {_CHAR} | {_INT} | null | true | false {
	return new Yytoken(LITERAL, yytext()); //NanoMorphoParser.LITERAL;
}


{_OPERATOR} {
	return new Yytoken(OPERATOR, yytext());
}

"if" {
	return new Yytoken(IF, yytext()); //NanoMorphoParser.IF;
}

"elsif" {
	return new Yytoken(ELSIF, yytext()); //NanoMorphoParser.IF;
}

"else" {
	return new Yytoken(ELSE, yytext()); //NanoMorphoParser.IF;
}

"while" {
	return new Yytoken(WHILE, yytext()); //NanoMorphoParser.IF;
}

"var" {
	return new Yytoken(VAR, yytext()); //NanoMorphoParser.DEFINE;
}

"return" {
	return new Yytoken(RETURN, yytext()); //NanoMorphoParser.DEFINE;
}

{_NAME} {
	return new Yytoken(NAME,yytext()); //NanoMorphoParser.NAME;
}

";;".*$ {
}

[ \t\r\n\f] {
}

. {
	return new Yytoken(ERR, "Error"); //NanoMorphoParser.YYERRCODE;
}
