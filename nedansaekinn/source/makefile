NanoMorphoLexer.class NanoMorphoParser.class NanoMorphoParserVal.class: NanoMorphoLexer.java NanoMorphoParser.java NanoMorphoParserVal.java
	javac -d ../bin NanoMorphoLexer.java NanoMorphoParser.java NanoMorphoParserVal.java
NanoMorphoLexer.java: nanomorpho.jflex
	java -jar jflex-1.6.0.jar nanomorpho.jflex
NanoMorphoParser.java NanoMorphoParserVal.java: nanomorpho.byaccj
	./yacc.linux -J -Jclass=NanoMorphoParser nanomorpho.byaccj


clean:
	rm -Rf *~ NanoLisp*.class NanoLispLexer.java NanoLispParser.java NanoLispParserVal.java 
