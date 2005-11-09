package polyglot.ext.jl5.ast;

import java.util.*;
import polyglot.ast.*;
import polyglot.types.*;
/**
 * An immutable representation of a Java language extended <code>for</code>
 * statement.  Contains a statement to be executed and an expression
 * to be tested indicating whether to reexecute the statement.
 */
public interface ExtendedFor extends Loop 
{    
    /** Set the loop body */
    ExtendedFor body(Stmt body);
}
