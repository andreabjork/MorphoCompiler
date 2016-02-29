/**
 * Created by andrea on 27.2.2016.
 */
import java.io.*;
import java.util.Vector;

public class NanoMorpho {
    private NanoMorphoLexer lexer;
    private Yytoken next_token;
    private String lexeme;
    // Inni í hverri fallsskilgreiningu inniheldur vars nöfnin
    // á viðföngunum í fallið (þ.e. leppunum eða breytunöfnunum
    // sem standa fyrir viðföngin), í sætum 1 og aftar.  Sæti
    // 0 inniheldur nafn fallsins sem verið er að skilgreina.
    String[] vars;


    // Eftirfarandi fastar standa fyrir allar þær
    // mögulegu gerðir af segðum sem milliþula
    // (intermediate code) getur innihaldið.
    // Þessar fjórar gerðir segða (ásamt þeim
    // möguleika að skrifa föll sem nota slíkar
    // segðir) duga reyndar til að hægt sé að
    // reikna hvað sem er reiknanlegt.
    enum CodeType {
        NAME, ASSIGN, CALL, RETURN, OP, LITERAL, IF, WHILE
    };


    // -------------
    // 		UTIL
    // -------------
    private String advance() {
        try {
            next_token = lexer.yylex();
        }
        catch (IOException e) {
            throw new Error(e);
        }
        if( next_token == null ) next_token = new Yytoken(NanoMorphoLexer.EOF,"EOF");
        String res = lexeme;
        lexeme = next_token.toString();
        return res;

    }
    // Checks if next_token.number matches the token
    private boolean matches(int token) {
        return (next_token.number == token);
    }

    // Checks if cursor is on tokenen with token 'token' and advances if it is.
    // Throws an error if not.
    private String assume(int token) {
        String res = lexeme;
        if(matches(token)) {
            advance();
        } else {
            expected(token);
        }
        return res;
    }

    private String tokenName( int token ) {
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

    private void expected( int token ) {
        expected(tokenName(token));
    }

    private void expected( String exp ) {
        System.err.println("Expected "+exp+", found "+next_token.string+" in line "+lexer.getLine()+", column "+lexer.getColumn());
        System.exit(1);
    }

    // Notkun: int i = n.varPos(name);
    // Fyrir:  n er NanoLisp þýðandi og er að þýða stofn einhvers
    //         falls.  name er nafnið á einhverju viðfangi í fallið.
    // Eftir:  i er staðsetning viðfangsins í viðfangarunu fallsins
    //         þar sem fyrsta viðfang er talið vera í sæti 0.
    int varPos( String name )
    {
        for( int i=1 ; i!=vars.length ; i++ )
            if( vars[i].equals(name) ) return i-1;
        throw new Error("Variable "+name+" is not defined");
    }

    // -----------------------------
    //    PARSER - INTERMEDIATE CODE
    // -----------------------------
    private Object[] program() {
        Vector<Object> collect = new Vector<>();
        collect.add(function());
        while( !matches(NanoMorphoLexer.EOF) ) collect.add(function());

        return collect.toArray();
    }

    private Object[] function() {
        // args is the arguments vector
        Vector<String> args = new Vector<String>();
        args.add(assume(NanoMorphoLexer.NAME));
        assume('(');
        if( matches(NanoMorphoLexer.NAME) ) {
            args.add(advance());
            while( matches(',') ) {
                args.add(advance());
                assume(NanoMorphoLexer.NAME);
            }
        }
        assume(')');
        assume('{');

        int varcount = 0;
        while( matches(NanoMorphoLexer.VAR) ) {
            decl();
            assume(';');
            varcount++;
        }

        // args to an array
        vars = new String[args.size()];
        args.toArray(vars);
        // define our collect object {fname, argcount, varcount, expr0 ... exprN}
        Vector<Object> collect = new Vector<>();
        collect.add(vars[0]); // function name
        collect.add(vars.length-1); // number of function arguments
        collect.add(varcount); // number of variable declarations

        collect.add(expr());
        assume(';');
        while( !matches('}') ) {
            collect.add(expr());
            assume(';');
        }
        advance();

        vars = null;
        return collect.toArray();
    }

    // Don't need to collect here because we only need the number of declarations???
    private void decl() {
        assume(NanoMorphoLexer.VAR);
        assume(NanoMorphoLexer.NAME);
        while( matches(',') ) {
            advance();
            assume(NanoMorphoLexer.NAME);
        }
    }

    private Object[] expr() {
        Vector<Object> collect = new Vector<>();
        collect.add(small_expr());
        while( matches(NanoMorphoLexer.OPERATOR) ) {
            advance();
            collect.add(small_expr());
        }

        return collect.toArray();
    }

    private Object[] small_expr() {
        // CodeType.NAME, CodeType.ASSIGN or {expr0, expr1, ..., exprN}, this might be CALL ?
        if( matches(NanoMorphoLexer.NAME) ) {
            advance();
            // return e = {CodeType.ASSIGN, name, expr()}
            if( matches('=') ) {
                advance();
                return new Object[]{CodeType.ASSIGN, varPos(lexeme), expr()};
            }
            // return e = {CodeType.NAME, name}
            if( !matches('(') ) return new Object[]{CodeType.NAME, varPos(lexeme)};

            advance();

            if( matches(')') ) {
                advance();
            }

            // e = {CodeType.CALL, fname, expr ... expr}
            // Since arguments are expressions, store them in their own vector:
            Vector<Object> args = new Vector<>();
            args.add(expr());
            while( matches(',') ) {
                advance();
                args.add(expr());
            }
            assume(')');
            return new Object[]{CodeType.CALL, lexeme, args};
        }

        // CodeType.RETURN
        if( matches(NanoMorphoLexer.RETURN) ) {
            advance();
            return new Object[]{CodeType.RETURN, expr()};
        }
        // CodeType.OP
        if( matches(NanoMorphoLexer.OPERATOR) ) {
            advance();
            return new Object[]{CodeType.OP, lexeme, small_expr()};
        }
        // CodeType.LITERAL
        if( matches(NanoMorphoLexer.LITERAL) ) {
            advance();
            return new Object[]{CodeType.LITERAL, lexeme};
        }

        // Need to check if this requires a specific intermediate
        if( matches('(') ) {
            advance();
            expr();
            assume(')');
        }

        // CodeType.IF
        if( matches(NanoMorphoLexer.IF) ) {
            Vector<Object> collect = new Vector<>();
            collect.add(CodeType.IF);
            advance();
            assume('(');
            collect.add(expr()); // condition
            assume(')');
            collect.add(body()); // 'then' expression
            while( matches(NanoMorphoLexer.ELSIF) ) {
                advance();
                assume('(');
                collect.add(expr()); // elif condition
                assume(')');
                collect.add(body()); // elif expression
            }
            if( matches(NanoMorphoLexer.ELSE) ) {
                advance();
                collect.add(body()); // 'else' expression
            }

            return collect.toArray();
        }

        // CodeType.WHILE
        if( matches(NanoMorphoLexer.WHILE) ) {
            Object cond, whileExpr;
            advance();
            assume('(');
            cond = expr();
            assume(')');
            whileExpr = body();
            return new Object[]{CodeType.WHILE, cond, whileExpr};
        }
        expected("expression");
        return null; // Fix this
    }

    // CodeType.BODY
    private Object[] body() {
        Vector<Object> collect = new Vector<>();

        assume('{');
        collect.add(expr());
        assume(';');
        while( !matches('}') ) {
            collect.add(expr());
            assume(';');
        }
        advance();

        return collect.toArray();
    }


    // -----------------
    //    FINAL CODE
    // -----------------

    // Use: emit(line);
    // Pre:  line er lína í lokaþulu.
    // Post:  Búið er að skrifa línuna á aðalúttak.
    private void emit( String line ) {
        System.out.println(line);
    }

   private void generateProgram(String name, Object[] p) {
        // p = {function0, function1, ..., functionN}
        emit("\""+name+".mexe\" = main in");
        emit("!{{");
        for( int i=0; i!=p.length; i++ ) generateFunction((Object[])p[i]);
        emit("}}*BASIS;");
    }

    private void generateFunction(Object[] f) {
        // f = {fname, argcount, varcount, expr0, expr1, ..., exprN}
        String fname = (String)f[0];
        int argcount = (Integer)f[1];
        int varcount = (Integer)f[2];
        emit("#\""+fname+"[f"+argcount+"]\" = ");
        emit("[");
        // Fetch the values from the var declarations and put them on the stack:
        // If there are n arguments to the function, the declarations must be
        // n+1, n+2, ... , n+numVarDeclarations
        for(int i=argcount; i<argcount+varcount; i++) emit("(Fetch "+i+")");
        for(int i=3; i!=f.length; i++) generateExpr((Object[])f[i]);
        emit("]");
    }


    static int nextLab = 1;

    // Notkun: int i = newLab();
    // Eftir:  i er jákvæð heiltala sem ekki hefur áður
    //         verið skilað úr þessu falli.  Tilgangurinn
    //         er að búa til nýtt merki (label), sem er
    //         ekki það sama og neitt annað merki.
    static int newLab()
    {
        return nextLab++;
    }

    // Needs to handle: NAME, ASSIGN, CALL, RETURN, OP, LITERAL, IF, WHILE
    private void generateExpr(Object[] e) {
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
                    (Push) -> Pushed to stack
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
                generateExpr((Object[])e[2]);
                emit("(Call \""+e[1]+"[f1]\" "+1);
                return;
            case IF:
                // e = {IF,cond,then,else}
                int labElse = newLab();
                int labEnd = newLab();
                generateJump((Object[])e[1],0,labElse); // check condition
                generateExpr((Object[])e[2]); // 'then' expr if condition was true (pos 0)
                emit("(Go _"+labEnd+")");
                emit("_"+labElse+":");
                generateExpr((Object[])e[3]); // 'else' expr if condition was false
                emit("_"+labEnd+":");
                return;
            case WHILE:
                // e = {WHILE, cond, body}
                int labStart = newLab();
                int labQuit = newLab();
                emit("(Go_"+labStart); // start of while loop
                generateJump((Object[])e[1], labStart, labQuit);
                emit("(Go_"+labQuit);
                return;
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
    private void generateJump( Object[] e, int labTrue, int labFalse ) {
        switch( (CodeType)e[0] ) {
            case LITERAL:
                String literal = (String)e[1];
                if( literal.equals("false") || literal.equals("null") )
                {
                    if( labFalse!=0 ) emit("(Go _"+labFalse+")");
                    return;
                }
                if( labTrue!=0 ) emit("(Go _"+labTrue+")");
                return;
            default:
                generateExpr(e);
                if( labTrue!=0 ) emit("(GoTrue _"+labTrue+")");
                if( labFalse!=0 ) emit("(GoFalse _"+labFalse+")");
        }
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
    private void generateJumpP( Object[] e, int labTrue, int labFalse ) {
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

    // Notkun: generateExpr(e);
    // Fyrir:  e er milliþula fyrir segð.
    // Eftir:  Þetta kall býr til lokaþulu sem er jafngild
    //         þulunni sem köllin
    //            generateExpr(e);
    //            emit("(Return)");
    //         framleiða.  Þulan er samt ekki endilega sú
    //         sama og þessi köll framleiða því tilgangurinn
    //         er að geta framleitt betri þulu.
    private void generateExprR( Object[] e ) {
        switch( (CodeType)e[0] )
        {
            case NAME:
                // e = {NAME,name}
                emit("(FetchR "+e[1]+")");
                return;
            case LITERAL:
                // e = {LITERAL,literal}
                emit("(MakeValR "+(String)e[1]+")");
                return;
            case IF:
                // e = {IF,cond,then,else}
                int labElse = newLab();
                generateJump((Object[])e[1],0,labElse);
                generateExprR((Object[])e[2]);
                emit("_"+labElse+":");
                generateExprR((Object[])e[3]);
                return;
            case CALL:
                // e = {CALL,name,args}
                Object[] args = (Object[])e[2];
                int i;
                for( i=0 ; i!=args.length ; i++ )
                    if( i==0 )
                        generateExpr((Object[])args[i]);
                    else
                        generateExprP((Object[])args[i]);
                emit("(CallR #\""+e[1]+"[f"+i+"]\" "+i+")");
                return;
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
    private void generateExprP( Object[] e ) {
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


    private void generateBody(Object[] intermediate) {
    }



    // -----------
    //    MAIN
    // -----------
     public void main( String args[] ) throws Exception {
        lexer = new NanoMorphoLexer(new FileReader(args[0]));
        advance();
        Object[] intermediate = program();
        if( !matches(NanoMorphoLexer.EOF) ) expected("EOF or function name");
        generateProgram("test", intermediate);
    }
}
