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
    public JL5Formal JL5Formal(Position pos, FlagAnnotations flags, TypeNode type, String name, boolean variable);
    
    public JL5LocalDecl JL5LocalDecl(Position pos, FlagAnnotations flags, TypeNode type, String name, Expr init);
   
    public JL5Local JL5Local(Position pos, String name);

    public JL5PackageNode JL5PackageNode(Position pos, FlagAnnotations flags, Package package_);

    public ParamTypeNode ParamTypeNode(Position pos, List bounds, String id);
    
    public BoundedTypeNode BoundedTypeNode(Position pos, BoundedTypeNode.Kind kind, TypeNode bound);

    public AmbQualifierNode JL5AmbQualifierNode(Position pos, QualifierNode qual, String name, List args);
    
    public AmbTypeNode JL5AmbTypeNode(Position pos, QualifierNode qual, String name, List args);

    public ConstructorCall JL5ThisCall(Position pos, List args, List typeArgs);

    public ConstructorCall JL5ThisCall(Position pos, Expr outer, List args, List typeArgs);

    public ConstructorCall JL5SuperCall(Position pos, List args, List typeArgs);

    public ConstructorCall JL5SuperCall(Position pos, Expr outer, List args, List typeArgs);

    public Call JL5Call(Position pos, Receiver target, String name, List args, List typeArgs);

    public New JL5New(Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body, List typeArgs);
    
    public New JL5New(Position pos, TypeNode tn, List arguments, ClassBody body, List typeArgs);


    public JL5Instanceof JL5Instanceof(Position pos, Expr expr, TypeNode tn);

    public JL5Import JL5Import(Position pos, Import.Kind kind, String name);

    public JL5CanonicalTypeNode JL5CanonicalTypeNode(Position pos, Type t);

    public JL5Catch JL5Catch(Position pos, Formal formal, Block body);

    public JL5NewArray JL5NewArray(Position pos, TypeNode baseType, List dims, int addDims, ArrayInit init);
}

