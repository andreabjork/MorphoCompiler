import java.io.*;

/*
 * Keyrist með
 * java NanoMorphoParser LexerTest debug=true fyrir útprentanir á
 * útleiðslu
 * 
 * Parserinn er byggður á þessari mállýsingu sem er unnin upp úr
 * málriti fyrir NanoMorpho.
 * 
P	->	FP'
P'	->	FP' | e
F	->	N ( R ) { S T }
R	->	NR' | e
R'	->	,NR' | e
S	->	D;S | e
T	->	X;T'
T'	->	X;T' | e
D	->	'var' R
X	->	YX'
X'	->	OYX' | e
Y	->	OY | E
E	->	NM | L | ( X ) | I | 'return' X | 'while' ( X ) B
M	->	= X | ( T ) | e
I	->	'if'(X)BUV
U	->	'elsif' ( X ) B U | e
V	->	'else' B | e
B	->	{ T }
N	-> 	<NAME>
O	->	<OPNAME>
L	->	<LITERAL>
*/

public class NanoMorphoParserOLD {
    static private NanoMorphoLexer lexer;
    static private Yytoken next_token;
    static private boolean debug = false;

    // -------------
    // 		UTIL
    // -------------
    static private void advance() {
        try {
            next_token = lexer.yylex();
        } catch (IOException e) {
            throw new Error(e);
        }
        if( next_token == null ) {
            next_token = new Yytoken(NanoMorphoLexer.EOF,"EOF");
        }
    }

    // Checks if next_token.number matches the id
    static private boolean matches(int id) {
        return (next_token.number == id);
    }

    // Checks if cursor is on token with id 'id' and advances if it is.
    // Throws an error if not.
    static private void assume(int id) {
        if(matches(id)) {
            advance();
        } else {
            throw new Error("Parsing error: Expected "+id+", found "+next_token.toString());
        }
    }

    // -------------
    //    PARSER
    // -------------

    // program
    // P	->	FP'
    static private void p() {
        if(debug)System.out.println("P -> FP'");
        f();
        pp();
    }

    //	P'	->	FP' | e
    static private void pp() {
        if(next_token.number == NanoMorphoLexer.EOF) {
            if(debug) System.out.println("P' -> e");
            return;
        }
        if(debug) System.out.println("P' -> FP'");
        f();
        pp();
    }

    // function
    //F	->	N ( R ) { S T }
    static private void f() {
        if(debug) System.out.println("F -> N(R){ST}");
        assume(NanoMorphoLexer.NAME);
        assume((int)'(');
        r();
        assume((int)')');
        assume((int)'{');
        s();
        t();
        assume((int)'}');
    }

    //R	->	NR' | e
    static private void r() {
        if(matches((int)')') || matches((int)';')) {
            if(debug) System.out.println("R -> e");
            return;
        }

        if(debug) System.out.println("R -> NR'");
        assume(NanoMorphoLexer.NAME);
        rp();

    }

    //R'	->	,NR' | e
    static private void rp() {
        if(matches((int)')') || matches((int)';')) {
            if(debug) System.out.println("R' -> e");
            return;
        }

        if(debug) System.out.println("R' -> ,NR'");
        assume((int)',');
        assume(NanoMorphoLexer.NAME);
        rp();
    }


    //// Runa af 0 eða fleiri declaration með ; á eftir
    //S	->	D;S | e
    static private void s() {
        if(matches(NanoMorphoLexer.VAR)) {
            if(debug) System.out.println("S -> D;S");
            d();
            assume((int)';');
            s();
            return;
        }
        if(debug) System.out.println("S -> e");
    }

    //// Runa af 1 eða fleiri expr með ; á eftir
    //T	->	X;T'
    static private void t() {
        if(debug)System.out.println("T -> X;T'");
        x();
        assume((int)';');
        tp();
    }


    //T'	->	X;T' | e
    static private void tp() {
        if(matches((int)'}') || matches((int)')')) {
            if(debug) System.out.println("T' -> e");
            return;
        }
        if(debug)System.out.println("T' -> X;T'");
        x();
        assume((int)';');
        tp();
    }


    //// Declaration
    //D	->	'var' R
    static private void d() {
        if(debug)System.out.println("D -> var R");
        assume(NanoMorphoLexer.VAR);
        r();
    }


    // Expression X
    //X	-> YX'
    static private void x() {
        if(debug)System.out.println("X -> YX'");
        y();
        xp();
    }


    // X' -> OYX' | e
    static private void xp() {
        if(matches((int)')') || matches((int)';')) {
            if(debug) System.out.println("X' -> e");
            return;
        }

        if(debug)System.out.println("X' -> OYX'");
        assume(NanoMorphoLexer.OPERATOR);
        y();
        xp();
    }


    // Y -> OY | E
    static private void y() {
        if(matches(NanoMorphoLexer.OPERATOR)) {
            if(debug)System.out.println("Y -> OY");
            advance();
            y();
        } else {
            if(debug)System.out.println("Y -> E");
            e();
        }
    }

    //E	->	NM | L | ( X ) | I | 'return' X | 'while' ( X ) B
    static private void e() {
        // E -> NM
        if(matches(NanoMorphoLexer.NAME)) {
            if(debug)System.out.println("E -> NM");
            advance();
            m();
            return;
        }
        // E -> L
        if(matches(NanoMorphoLexer.LITERAL)) {
            if(debug)System.out.println("E -> L");
            advance();
            return;
        }
        // E -> (X)
        if(matches((int)'(')) {
            if(debug)System.out.println("E -> (X)");
            advance();
            x();
            assume((int)')');
            return;
        }
        // E -> I
        if(matches(NanoMorphoLexer.IF)) {
            if(debug)System.out.println("E -> I");
            i();
            return;
        }
        // E -> 'return' X
        if(matches(NanoMorphoLexer.RETURN)) {
            if(debug)System.out.println("E -> return X");
            advance();
            x();
            return;
        }
        // E -> 'while' ( X ) B
        if(matches(NanoMorphoLexer.WHILE)) {
            if(debug)System.out.println("E -> 'while' (X)B");
            advance();
            assume((int)('('));
            x();
            assume((int)(')'));
            b();
            return;
        }

        throw new Error("Parsing error: Illegal expression "+next_token);

    }


    //M	->	= X | ( T ) | e
    static private void m() {
        if(matches((int)'=')) {
            if(debug)System.out.println("M -> = X");
            advance();
            x();
            return;
        }
        if(matches((int)('('))) {
            if(debug)System.out.println("M -> ( T ) ");
            advance();
            t();
            assume((int)(')'));
            return;
        }

        if(debug) System.out.println("M -> e");
    }


    //// IFEXPR
    //I	->	'if'(X)BUV
    static private void i() {
        if(debug)System.out.println("I -> 'if'(X)BUV");
        assume(NanoMorphoLexer.IF);
        assume((int)'(');
        x();
        assume((int)')');
        b();
        u();
        v();
    }


    //U	->	'elsif' ( X ) B U | e
    static private void u() {
        if(matches(NanoMorphoLexer.ELSIF)) {
            if(debug)System.out.println("U -> elsif (X) B U");
            advance();
            assume((int)'(');
            x();
            assume((int)')');
            b();
            u();
        } else {
            if(debug)System.out.println("U -> e");
        }
    }


    //V	->	'else' B | e
    static private void v() {
        if(matches(NanoMorphoLexer.ELSE)) {
            if(debug)System.out.println("V -> else B");
            advance();
            b();
        } else {
            if(debug)System.out.println("V -> e");
        }
    }

    // Body
    //B	->	{ T }
    static private void b() {
        if(debug)System.out.println("B -> {T}");
        assume((int)'{');
        t();
        assume((int)'}');
    }

    static public void main( String args[] )
    {
        if(args.length > 1) {
            debug = args[1].equals("debug=true");
        }
        try {
            lexer = new NanoMorphoLexer(new FileReader(args[0]));
            advance();
            p();
            if( next_token.number != NanoMorphoLexer.EOF ) {
                throw new Error("Expected EOF, found "+next_token);
            }
        } catch (IOException e) {e.printStackTrace();}
    }
}