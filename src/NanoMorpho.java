/**
 * Created by andrea on 27.2.2016.
 * TODO's
 * - Passa að breytur geti ekki verið teknar inn tvisvar  -> args.add
 * - Breyta Push - MakeVal í MakeValP, Push-Return í ReturnP, CallR o.s.frv.
 * - Bæta við &&, || o.þ.h
 * - Skipta í NanoMorphoParser og Lokaþuluklasa
 * - Gera Makefile
 *
 *
 * Ath með recursion í expr+expr, smallexpr og expr, etc.
 */
import java.io.*;
import java.util.Vector;

public class NanoMorpho {
    private static NanoMorphoLexer lexer;
    private static Yytoken next_token;
    private static String lexeme;
    // Inni í hverri fallsskilgreiningu inniheldur vars nöfnin
    // á viðföngunum í fallið (þ.e. leppunum eða breytunöfnunum
    // sem standa fyrir viðföngin), í sætum 1 og aftar.  Sæti
    // 0 inniheldur nafn fallsins sem verið er að skilgreina.
    private static String[] vars;


    // Eftirfarandi fastar standa fyrir allar þær
    // mögulegu gerðir af segðum sem milliþula
    // (intermediate code) getur innihaldið.
    enum CodeType {
        NAME, ASSIGN, CALL, RETURN, OP, LITERAL, IF, WHILE, ELSE
    };


    
    // ----------------------------------------------------------------------------------
    //                                  UTIL FUNCTIONS
    // ----------------------------------------------------------------------------------
    private static String advance() {
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
    private static boolean matches(int token) {
        return (next_token.number == token);
    }

    // Checks if cursor is on token 'token' and advances if it is.
    // Throws an error if not.
    private static String assume(int token) {
        String res = lexeme;
        if(matches(token)) {
            advance();
        } else {
            expected(token);
        }
        return res;
    }

    private static String tokenName( int token ) {
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

    private static void expected( int token ) {
        expected(tokenName(token));
    }

    private static void expected( String exp ) {
        System.err.println("Expected "+exp+", found "+next_token.string+" in line "+lexer.getLine()+", column "+lexer.getColumn());
        System.exit(1);
    }

    // Notkun: int i = n.varPos(name);
    // Fyrir:  n er NanoLisp þýðandi og er að þýða stofn einhvers
    //         falls.  name er nafnið á einhverju viðfangi í fallið.
    // Eftir:  i er staðsetning viðfangsins í viðfangarunu fallsins
    //         þar sem fyrsta viðfang er talið vera í sæti 0.
    // TODO: Remember to make it check if we have that variable before
    private static int varPos( String name )
    {
        for( int i=1 ; i!=vars.length ; i++ )
            if( vars[i].equals(name) ) return i-1;
        throw new Error("Variable "+name+" is not defined");
    }

    
    // ----------------------------------------------------------------------------------
    //                            PARSER - INTERMEDIATE CODE
    // ----------------------------------------------------------------------------------
    private static Object[] program() {
        Vector<Object> collect = new Vector<>();
        collect.add(function());
        while( !matches(NanoMorphoLexer.EOF) ) collect.add(function());

        return collect.toArray();
    }

    private static Object[] function() {
        // args is the arguments vector
        Vector<String> args = new Vector<String>();
        args.add(assume(NanoMorphoLexer.NAME));
        assume('(');
        if( matches(NanoMorphoLexer.NAME) ) {
            args.add(advance());
            while( matches(',') ) {
                advance();
                args.add(assume(NanoMorphoLexer.NAME));
            }
        }
        assume(')');
        assume('{');

        int argCount = args.size()-1;
        while( matches(NanoMorphoLexer.VAR) ) {
            decl(args);
            assume(';');
        }

        // args to an array
        vars = new String[args.size()];
        args.toArray(vars);
        // define our collect object {fname, argcount, varcount, expr0 ... exprN}
        Vector<Object> collect = new Vector<>();
        collect.add(vars[0]); // function name
        collect.add(argCount); // number of function arguments
        collect.add(vars.length-1-argCount); // number of variable declarations

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
    private static void decl(Vector<String> args) {
        assume(NanoMorphoLexer.VAR);
        args.add(assume(NanoMorphoLexer.NAME));
        while( matches(',') ) {
            advance();
            args.add(assume(NanoMorphoLexer.NAME));
        }
    }

    private static Object[] expr() {
        Vector<Object> collect = new Vector<>();
        collect.add(small_expr());
        while( matches(NanoMorphoLexer.OPERATOR) ) {
            collect.add(lexeme); 
            advance();
            collect.add(small_expr());
        }

        return collect.toArray();
    }

    private static Object[] small_expr() {
        // CodeType.NAME, CodeType.ASSIGN or {expr0, expr1, ..., exprN}, this might be CALL ?
        if( matches(NanoMorphoLexer.NAME) ) {
            String lexName = lexeme;
            advance();
            // return e = {CodeType.ASSIGN, name, expr()}
            if( matches('=') ) {
                advance();
                return new Object[]{CodeType.ASSIGN, varPos(lexName), expr()};
            }
            // return e = {CodeType.NAME, name}
            if( !matches('(') ) return new Object[]{CodeType.NAME, varPos(lexName)};

            advance();

            Vector<Object> args = new Vector<>();
            if( matches(')') ) {
                advance();
                return new Object[]{CodeType.CALL, lexName, args.toArray()};
            }

            // e = {CodeType.CALL, fname, expr ... expr}
            // Since arguments are expressions, store them in their own vector:
            args.add(expr());
            while( matches(',') ) {
                advance();
                args.add(expr());
            }
            assume(')');
            return new Object[]{CodeType.CALL, lexName, args.toArray()};
        }

        // CodeType.RETURN
        if( matches(NanoMorphoLexer.RETURN) ) {
            advance();
            return new Object[]{CodeType.RETURN, expr()};
        }
        // CodeType.OP
        if( matches(NanoMorphoLexer.OPERATOR) ) {
            String lexName = lexeme;
            advance();
            return new Object[]{CodeType.OP, lexName, small_expr()};
        }
        // CodeType.LITERAL
        if( matches(NanoMorphoLexer.LITERAL) ) {
            String lexName = lexeme;
            advance();
            return new Object[]{CodeType.LITERAL, lexName};
        }

        // Need to check if this requires a specific intermediate
        if( matches('(') ) {
            advance();
            expr();
            assume(')');
        }

        // CodeType.IF
        if( matches(NanoMorphoLexer.IF) ) {
            // Note: If - elsif ... - elsif - else should become If - else (if - else (if - else ... ) ) )
            Vector<Object> collect = new Vector<>();
            collect.add(CodeType.IF);
            advance();
            assume('(');
            collect.add(expr()); // condition
            assume(')');
            
            collect.add(body()); // 'then' expression

            if(matches(NanoMorphoLexer.ELSIF) || matches(NanoMorphoLexer.ELSE))
                collect.add(elseBody());
            /*Object elseBody:    
            while( matches(NanoMorphoLexer.ELSIF) ) {
                advance();
                assume('(');
                Object cond = expr(); // elif condition
                assume(')');
                Object thenBody = body(); // elif expression
                elseBody = new Object[]{CodeType.IF, cond, thenBody, elseBody};
            }
            if( matches(NanoMorphoLexer.ELSE) ) {
                advance();
                Object elseBody = body(); // 'else' expression
            }*/

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

    private static Object[] elseBody() {
        if(matches(NanoMorphoLexer.ELSE)) {
            advance();
            return new Object[]{CodeType.ELSE, body()};
        } else if(matches(NanoMorphoLexer.ELSIF)) {
            advance();
            assume('(');
            Object cond = expr();
            assume(')');
            Object thenBody = body();
            return new Object[]{CodeType.IF, cond, thenBody, elseBody()};
        } else return null;
    }

    // CodeType.BODY
    private static Object[] body() {
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


    // ----------------------------------------------------------------------------------
    //                                     FINAL CODE
    // ----------------------------------------------------------------------------------

    // Use: emit(line);
    // Pre:  line er lína í lokaþulu.
    // Post:  Búið er að skrifa línuna á aðalúttak.
    private static void emit( String line ) {
        System.out.println(line);
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
        for(int i=3; i!=f.length; i++) generateExpr((Object[])f[i]);
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
        /*switch( (CodeType)e[0] ) {
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
        }*/
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

    // Notkun: generateExpr(e);
    // Fyrir:  e er milliþula fyrir segð.
    // Eftir:  Þetta kall býr til lokaþulu sem er jafngild
    //         þulunni sem köllin
    //            generateExpr(e);
    //            emit("(Return)");
    //         framleiða.  Þulan er samt ekki endilega sú
    //         sama og þessi köll framleiða því tilgangurinn
    //         er að geta framleitt betri þulu.
    private static void generateExprR( Object[] e ) {
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



    
    // ----------------------------------------------------------------------------------
    //                                       MAIN
    // ----------------------------------------------------------------------------------
    // Prófað með því að pípa úttakinu í test.masm skrá og compilea með
    // java -jar morpho.jar -c test.masm
    // og þá verður til test.mexe sem má keyra með java -jar morpho.jar test
     public static void main( String args[] ) throws Exception {
        lexer = new NanoMorphoLexer(new FileReader(args[0]));
        advance();
        Object[] intermediate = program();
        if( !matches(NanoMorphoLexer.EOF) ) expected("EOF or function name");
        generateProgram("test", intermediate);
    }
}
