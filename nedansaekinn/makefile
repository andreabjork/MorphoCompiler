NanoMorphoLexer.class NanoMorphoParser.class NanoMorphoParserVal.class: NanoMorphoLexer.java NanoMorphoParser.java NanoMorphoParserVal.java
	java -cp /source -d bin NanoMorphoLexer.java NanoMorphoParser.java NanoMorphoParserVal.java
NanoMorphoLexer.java: source/nanomorpho.jflex
	java -jar jflex-1.6.0.jar nanomorpho.jflex
NanoMorphoParser.java NanoMorphoParserVal.java: source/nanomorpho.byaccj
	./byacc.exe -J -Jclass=NanoMorphoParser nanomorpho.byaccj


clean:
	rm -Rf *~ NanoLisp*.class NanoLispLexer.java NanoLispParser.java NanoLispParserVal.java 
