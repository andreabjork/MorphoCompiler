nanomorpho: NanoMorpho.class NanoMorphoLexer.class
NanoMorpho.class: NanoMorpho.java
	javac -source 1.7 -target 1.7 NanoMorpho.java
NanoMorphoLexer.class: NanoMorphoLexer.java
	javac -source 1.7 -target 1.7 NanoMorphoLexer.java
NanoMorphoLexer.java: nanomorpho.jflex
	java -jar jflex-1.6.0.jar nanomorpho.jflex
testfile.mexe: testfile.masm morpho.jar
	java -jar morpho.jar -c testfile.masm
testfile.masm: NanoLisp.class testfile
	java NanoMorpho testfile
zip:
	rm -Rf ../nanomorpho.zip nanomorpho.zip
	cd .. ;\
	zip nanomorpho	nanomorpho/NanoMorpho.java nanomorpho/makefile \
			nanomorpho/nanomorpho.jflex nanomorpho/testfile \
			nanolisp/jflex-1.6.0.jar nanolisp/morpho.jar \
	mv nanomorpho.zip nanomorpho
clean:
	rm -Rf *~ NanoMorpho*.class NanoMorphoLexer.java testfile.masm testfile.mexe
