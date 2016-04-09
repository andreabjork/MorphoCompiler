//#############################################
//## file: NanoMorphoParser.java
//## Generated by Byacc/j
//#############################################
/**
 * BYACC/J Semantic Value for parser: NanoMorphoParser
 * This class provides some of the functionality
 * of the yacc/C 'union' directive
 */
public class NanoMorphoParserVal
{
/**
 * integer value of this 'union'
 */
public int ival;

/**
 * double value of this 'union'
 */
public double dval;

/**
 * string value of this 'union'
 */
public String sval;

/**
 * object value of this 'union'
 */
public Object obj;

//#############################################
//## C O N S T R U C T O R S
//#############################################
/**
 * Initialize me without a value
 */
public NanoMorphoParserVal()
{
}
/**
 * Initialize me as an int
 */
public NanoMorphoParserVal(int val)
{
  ival=val;
}

/**
 * Initialize me as a double
 */
public NanoMorphoParserVal(double val)
{
  dval=val;
}

/**
 * Initialize me as a string
 */
public NanoMorphoParserVal(String val)
{
  sval=val;
}

/**
 * Initialize me as an Object
 */
public NanoMorphoParserVal(Object val)
{
  obj=val;
}
}//end class

//#############################################
//## E N D    O F    F I L E
//#############################################