package polyglot.ext.jl5.ast;

import java.util.*;
import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.*;
import polyglot.ext.jl5.types.*;

/**
 * NodeFactory for jl5 extension.
 */
public interface JL5NodeFactory extends NodeFactory {
    // TODO: Declare any factory methods for new AST nodes.
    public ExtendedFor ExtendedFor(Position pos, List varDecls, Expr expr, Stmt stmt);
    public EnumConstantDecl EnumConstantDecl(Position pos, FlagAnnotations flags, String name, List args, ClassBody body);
    public EnumConstantDecl EnumConstantDecl(Position pos, FlagAnnotations flags, String name, List args);
    public ClassDecl JL5ClassDecl(Position pos, FlagAnnotations flags, String name, TypeNode superType, List interfaces, ClassBody body, List paramTypes);
    public JL5ClassBody JL5ClassBody(Position pos, List members);
    public JL5ConstructorDecl JL5ConstructorDecl(Position pos, FlagAnnotations flags, String name, List formals, List throwTypes, Block body, List typeParams);
    public JL5Block JL5Block(Position pos, List statements);
    public JL5New JL5New(Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body);
    public JL5New JL5New(Position pos, TypeNode tn, List arguments);
    public JL5New JL5New(Position pos, TypeNode tn, List arguments, ClassBody body);
    public JL5New JL5New(Position pos, Expr qualifier, TypeNode tn, List arguments);

    public JL5Field JL5Field(Position pos, Receiver target, String name);

    public JL5Case JL5Case(Position pos, Expr expr);

    /** 
     * this is for making it possibly to disambiguate case labels
     * which are enum constants until after type checking
     * of the switch expr
     */
    public JL5AmbExpr JL5AmbExpr(Position pos, String name);
    
    public JL5MethodDecl JL5MethodDecl(Position pos, FlagAnnotations flags, TypeNode returnType, String name, List formals, List throwTypes, Block body, List typeParams);


    public AnnotationElemDecl AnnotationElemDecl(Position pos, FlagAnnotations flags, TypeNode type, String name, Expr def);
    
    public NormalAnnotationElem NormalAnnotationElem(Position pos, TypeNode name, List elements);
    public MarkerAnnotationElem MarkerAnnotationElem(Position pos, TypeNode name);
    public SingleElementAnnotationElem SingleElementAnnotationElem(Position pos, TypeNode name, Expr value);

    public ElementValuePair ElementValuePair(Position pos, String name, Expr value);

    public JL5FieldDecl JL5FieldDecl(Position pos, FlagAnnotations flags, TypeNode type, String name, Expr init);
    
    public JL5Formal JL5Formal(Position pos, FlagAnnotations flags, TypeNode type, String name);
    
    public JL5LocalDecl JL5LocalDecl(Position pos, FlagAnnotations flags, TypeNode type, String name, Expr init);
    
    public JL5PackageNode JL5PackageNode(Position pos, FlagAnnotations flags, Package package_);

    public ParamTypeNode ParamTypeNode(Position pos, BoundedTypeNode.Kind kind, List bounds, String id);
    
    public BoundedTypeNode BoundedTypeNode(Position pos, BoundedTypeNode.Kind kind, List bounds);
    
}

