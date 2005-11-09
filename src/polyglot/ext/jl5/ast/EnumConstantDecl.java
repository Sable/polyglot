package polyglot.ext.jl5.ast;

import java.util.*;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;

/**
 * An immutable representation of a Java language extended <code>for</code>
 * statement.  Contains a statement to be executed and an expression
 * to be tested indicating whether to reexecute the statement.
 */
public interface EnumConstantDecl extends ClassMember
{    
    /** get args */
    List args();

    /** set args */
    EnumConstantDecl args(List args);

    /** set name */
    EnumConstantDecl name(String name);

    /** get name */
    String name();
    
    /** set body */
    EnumConstantDecl body(ClassBody body);

    /** get body */
    ClassBody body();

    EnumInstance enumInstance();

    EnumConstantDecl enumInstance(EnumInstance ei);

    List annotations();
}
