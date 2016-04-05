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
%byaccj

%{

public int getLine() { return yyline; }
public int getColumn() { return yycolumn; }

%}

%{

public NanoMorphoParser yyparser;

public NanoMorphoLexer( java.io.Reader r, NanoMorphoParser yyparser )
{
	this(r);
	this.yyparser = yyparser;
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
	yyparser.yylval = new NanoMorphoParserVal(yytext());
	return yycharat(0);
}

{_STRING} | {_FLOAT} | {_CHAR} | {_INT} | null | true | false {
	yyparser.yylval = new NanoMorphoParserVal(yytext());
	return NanoMorphoParser.LITERAL;
}


{_OPERATOR} {
	yyparser.yylval = new NanoMorphoParserVal(yytext());
	return NanoMorphoParser.OPERATOR;
}

"if" {
	return NanoMorphoParser.IF;
}

"elsif" {
	return NanoMorphoParser.ELSIF;
}

"else" {
	return NanoMorphoParser.ELSE;
}

"while" {
	return NanoMorphoParser.WHILE;
}

"var" {
	return NanoMorphoParser.VAR;
}

"return" {
	return NanoMorphoParser.RETURN;
}

{_NAME} {
	yyparser.yylval = new NanoMorphoParserVal(yytext());
	return NanoMorphoParser.NAME;
}

";;".*$ {
}

[ \t\r\n\f] {
}

. {
	return NanoMorphoParser.YYERRCODE;
}
