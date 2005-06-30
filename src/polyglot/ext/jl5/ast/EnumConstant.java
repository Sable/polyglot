package polyglot.ext.jl5.ast;

import java.util.*;
import polyglot.ast.*;
import polyglot.types.*;
/**
 * An immutable representation of a Java language extended <code>for</code>
 * statement.  Contains a statement to be executed and an expression
 * to be tested indicating whether to reexecute the statement.
 */
public interface EnumConstant extends ClassMember
{    
    /** get args */
    List args();

    /** set args */
    EnumConstant args(List args);

    /** set name */
    EnumConstant name(String name);

    /** get name */
    String name();
    
    /** set body */
    EnumConstant body(ClassBody body);

    /** get body */
    ClassBody body();
}
