/**
 * Created by andrea on 27.2.2016.
 */
import java.io.*;

public class NanoMorphoParser {
    static private NanoMorphoLexer lexer;
    static private Yytoken next_token;

    // -------------
    // 		UTIL
    // -------------
    static private void advance() {
        try {
            next_token = lexer.yylex();
        }
        catch (IOException e) {
            throw new Error(e);
        }
        if( next_token == null ) next_token = new Yytoken(NanoMorphoLexer.EOF,"EOF");
    }
    // Checks if next_token.number matches the token
    static private boolean matches(int token) {
        return (next_token.number == token);
    }
    
    // Checks if cursor is on tokenen with token 'token' and advances if it is.
    // Throws an error if not.
    static private void assume(int token) {
        if(matches(token)) {
            advance();
        } else {
            expected(token);
        }
    }

    static String tokenName( int token ) {
        switch(token) {
            case NanoMorphoLexer.IF:		return "if";
            case NanoMorphoLexer.NAME:		return "name";
            case NanoMorphoLexer.LITERAL:	return "literal";
            case NanoMorphoLexer.VAR:		return "var";
            case NanoMorphoLexer.WHILE:		return "while";
            case NanoMorphoLexer.ELSE:		return "else";
            case NanoMorphoLexer.ELSIF:		return "elsif";
            case NanoMorphoLexer.RETURN:	return "return";
            case NanoMorphoLexer.OPERATOR:	return "operation";
            case NanoMorphoLexer.EOF:		return "EOF";
            case NanoMorphoLexer.ERR:		return "unrecognized lexeme";
        }
        return "'"+(char)token+"'";
    }

    static void expected( int token ) {
        expected(tokenName(token));
    }

    static void expected( String exp ) {
        System.err.println("Expected "+exp+", found "+next_token.string+" in line "+lexer.getLine()+", column "+lexer.getColumn());
        System.exit(1);
    }
    
    // -------------
    //    PARSER
    // -------------

    static void program() {
        function();
        while( !matches(NanoMorphoLexer.EOF) ) function();
    }

    static void function() {
        assume(NanoMorphoLexer.NAME);
        assume('(');
        if( matches(NanoMorphoLexer.NAME) ) {
            advance();
            while( matches(',') ) {
                advance();
                assume(NanoMorphoLexer.NAME);
            }
        }
        assume(')');
        assume('{');
        while( matches(NanoMorphoLexer.VAR) ) {
            decl();
            assume(';');
        }
        expr();
        assume(';');
        while( !matches('}') ) {
            expr();
            assume(';');
        }
        advance();
    }

    static void decl() {
        assume(NanoMorphoLexer.VAR);
        assume(NanoMorphoLexer.NAME);
        while( matches(',') ) {
            advance();
            assume(NanoMorphoLexer.NAME);
        }
    }

    static void expr() {
        small_expr();
        while( matches(NanoMorphoLexer.OPERATOR) ) {
            advance();
            small_expr();
        }
    }

    static void small_expr() {
        if( matches(NanoMorphoLexer.NAME) ) {
            advance();
            if( matches('=') ) {
                advance();
                expr();
                return;
            }
            if( !matches('(') ) return;
            advance();
            if( matches(')') ) {
                advance();
                return;
            }
            expr();
            while( matches(',') ) {
                advance();
                expr();
            }
            assume(')');
            return;
        }
        if( matches(NanoMorphoLexer.RETURN) ) {
            advance();
            expr();
            return;
        }
        if( matches(NanoMorphoLexer.OPERATOR) ) {
            advance();
            small_expr();
            return;
        }
        if( matches(NanoMorphoLexer.LITERAL) ) {
            advance();
            return;
        }
        if( matches('(') ) {
            advance();
            expr();
            assume(')');
            return;
        }
        if( matches(NanoMorphoLexer.IF) ) {
            advance();
            assume('(');
            expr();
            assume(')');
            body();
            while( matches(NanoMorphoLexer.ELSIF) ) {
                advance();
                assume('(');
                expr();
                assume(')');
                body();
            }
            if( matches(NanoMorphoLexer.ELSE) ) {
                advance();
                body();
            }
            return;
        }
        if( matches(NanoMorphoLexer.WHILE) ) {
            advance();
            assume('(');
            expr();
            assume(')');
            body();
            return;
        }
        expected("expression");
    }

    static void body() {
        assume('{');
        expr();
        assume(';');
        while( !matches('}') ) {
            expr();
            assume(';');
        }
        advance();
    }

    static public void main( String args[] ) throws Exception {
        lexer = new NanoMorphoLexer(new FileReader(args[0]));
        advance();
        program();
        if( !matches(NanoMorphoLexer.EOF) ) expected("EOF or function name");
    }
}