package polyglot.ext.jl5.ast;

import java.util.*;
import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.*;

/**
 * NodeFactory for jl5 extension.
 */
public interface JL5NodeFactory extends NodeFactory {
    // TODO: Declare any factory methods for new AST nodes.
    public ExtendedFor ExtendedFor(Position pos, List varDecls, Expr expr, Stmt stmt);
    public EnumConstantDecl EnumConstantDecl(Position pos, String name, List args, ClassBody body);
    public EnumConstantDecl EnumConstantDecl(Position pos, String name, List args);
    public ClassDecl JL5ClassDecl(Position pos, Flags flags, String name, List interfaces, ClassBody body);
    public JL5ClassBody JL5ClassBody(Position pos, List members);
    public JL5ConstructorDecl JL5ConstructorDecl(Position pos, Flags flags, String name, List formals, List throwTypes, Block body);
    public JL5Block JL5Block(Position pos, List statements);
    public JL5New JL5New(Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body);
    public JL5New JL5New(Position pos, TypeNode tn, List arguments);
    public JL5New JL5New(Position pos, TypeNode tn, List arguments, ClassBody body);
    public JL5New JL5New(Position pos, Expr qualifier, TypeNode tn, List arguments);

    public JL5Field JL5Field(Position pos, Receiver target, String name);

    public JL5Case JL5Case(Position pos, Expr expr);
}
