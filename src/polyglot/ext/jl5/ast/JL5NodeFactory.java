package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.*;
import java.util.*;

/**
 * NodeFactory for jl5 extension.
 */
public interface JL5NodeFactory extends NodeFactory {
    // TODO: Declare any factory methods for new AST nodes.
    public ExtendedFor ExtendedFor(Position pos, Flags flags, TypeNode type, String name, Expr expr, Stmt stmt);
    public EnumConstant EnumConstant(Position pos, String name, List args, ClassBody body);
    public EnumConstant EnumConstant(Position pos, String name, List args);
    public ClassDecl ClassDecl(Position pos, Flags flags, String name, List interfaces, ClassBody body);
    public EnumBody EnumBody(Position pos, List members);
}
