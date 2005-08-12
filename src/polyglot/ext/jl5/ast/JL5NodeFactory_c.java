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
    public ExtendedFor ExtendedFor(Position pos, List varDecls, Expr expr, Stmt stmt){
        ExtendedFor n = new ExtendedFor_c(pos, varDecls, expr, stmt);
        return n;
    }
    public EnumConstantDecl EnumConstantDecl(Position pos, String name, List args, ClassBody body){
        EnumConstantDecl n = new EnumConstantDecl_c(pos, name, args, body);
        return n;
    }
    public EnumConstantDecl EnumConstantDecl(Position pos, String name, List args){
        EnumConstantDecl n = new EnumConstantDecl_c(pos, name, args, null);
        return n;
    }
    public ClassDecl JL5ClassDecl(Position pos, Flags flags, String name, List interfaces, ClassBody body){
        ClassDecl n = new JL5ClassDecl_c(pos, flags, name, null, interfaces, body);
        return n;
    }
    public JL5ClassBody JL5ClassBody(Position pos, List members){
        JL5ClassBody n = new JL5ClassBody_c(pos, members);
        return n;
    }
    public JL5ConstructorDecl JL5ConstructorDecl(Position pos, Flags flags, String name, List formals, List throwTypes, Block body){
        JL5ConstructorDecl n = new JL5ConstructorDecl_c(pos, flags, name, formals, throwTypes, body);
        return n;
    }
    public JL5Block JL5Block(Position pos, List statements){
        JL5Block n = new JL5AbstractBlock_c(pos, statements);
        return n;
    }
    public JL5New JL5New(Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body){
        JL5New n = new JL5New_c(pos, qualifier, tn, arguments, body);
        return n;
    }
    public JL5New JL5New(Position pos, TypeNode tn, List arguments){
        JL5New n = new JL5New_c(pos, null, tn, arguments, null);
        return n;
    }
    public JL5New JL5New(Position pos, TypeNode tn, List arguments, ClassBody body){
        JL5New n = new JL5New_c(pos, null, tn, arguments, body);
        return n;
    }
    public JL5New JL5New(Position pos, Expr qualifier, TypeNode tn, List arguments){
        JL5New n = new JL5New_c(pos, qualifier, tn, arguments, null);
        return n;
    }
    public JL5Field JL5Field(Position pos, Receiver target, String name){
        JL5Field n = new JL5Field_c(pos, target, name);
        return n;
    }

    public JL5Case JL5Case(Position pos, Expr expr){
        JL5Case n = new JL5Case_c(pos, expr);
        return n;
    }
    
    public Disamb disamb(){
        return new JL5Disamb_c();
    }
    // TODO:  Override factory methods for overriden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.
}
