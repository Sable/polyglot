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
public class JL5NodeFactory_c extends NodeFactory_c implements JL5NodeFactory {
    // TODO:  Implement factory methods for new AST nodes.
    public ExtendedFor ExtendedFor(Position pos, Flags flags, TypeNode type, String name, Expr expr, Stmt stmt){
        ExtendedFor n = new ExtendedFor_c(pos, flags, type, name, expr, stmt);
        return n;
    }
    public EnumConstant EnumConstant(Position pos, String name, List args, ClassBody body){
        EnumConstant n = new EnumConstant_c(pos, name, args, body);
        return n;
    }
    public EnumConstant EnumConstant(Position pos, String name, List args){
        EnumConstant n = new EnumConstant_c(pos, name, args, null);
        return n;
    }
    public ClassDecl ClassDecl(Position pos, Flags flags, String name, List interfaces, ClassBody body){
        ClassDecl n = new ClassDecl_c(pos, flags, name, null, interfaces, body);
        return n;
    }
    public EnumBody EnumBody(Position pos, List members){
        EnumBody n = new EnumBody_c(pos, members);
        return n;
    }
    
    // TODO:  Override factory methods for overriden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.
}
