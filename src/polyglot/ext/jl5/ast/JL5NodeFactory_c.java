package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.*;
import java.util.*;
import polyglot.ext.jl5.types.*;

/**
 * NodeFactory for jl5 extension.
 */
public class JL5NodeFactory_c extends NodeFactory_c implements JL5NodeFactory {
    // TODO:  Implement factory methods for new AST nodes.
    public ExtendedFor ExtendedFor(Position pos, List varDecls, Expr expr, Stmt stmt){
        ExtendedFor n = new ExtendedFor_c(pos, varDecls, expr, stmt);
        return n;
    }
    public EnumConstantDecl EnumConstantDecl(Position pos, FlagAnnotations flags, String name, List args, ClassBody body){
        EnumConstantDecl n = new EnumConstantDecl_c(pos, flags, name, args, body);
        return n;
    }
    public EnumConstantDecl EnumConstantDecl(Position pos, FlagAnnotations flags, String name, List args){
        EnumConstantDecl n = new EnumConstantDecl_c(pos, flags, name, args, null);
        return n;
    }
    public ClassDecl JL5ClassDecl(Position pos, FlagAnnotations flags, String name, TypeNode superType,  List interfaces, ClassBody body, List paramTypes ){
        ClassDecl n;
        if (paramTypes == null){
            n = new JL5ClassDecl_c(pos, flags, name, superType, interfaces, body);
        }
        else {
            n = new JL5ClassDecl_c(pos, flags, name, superType, interfaces, body, paramTypes);
        }
        return n;
    }
    public JL5ClassBody JL5ClassBody(Position pos, List members){
        JL5ClassBody n = new JL5ClassBody_c(pos, members);
        return n;
    }
    public JL5ConstructorDecl JL5ConstructorDecl(Position pos, FlagAnnotations flags, String name, List formals, List throwTypes, Block body, List typeParams){
        JL5ConstructorDecl n;
        if (typeParams == null){
            n = new JL5ConstructorDecl_c(pos, flags, name, formals, throwTypes, body);
        }
        else {
            n = new JL5ConstructorDecl_c(pos, flags, name, formals, throwTypes, body, typeParams);
        }
        return n;
    }
    public JL5Block JL5Block(Position pos, List statements){
        JL5Block n = new JL5AbstractBlock_c(pos, statements);
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

    public JL5AmbExpr JL5AmbExpr(Position pos, String name){
        JL5AmbExpr n = new JL5AmbExpr_c(pos, name);
        return n;
    }
    
    public Disamb disamb(){
        return new JL5Disamb_c();
    }
    
    public JL5MethodDecl JL5MethodDecl(Position pos, FlagAnnotations flags, TypeNode returnType, String name, List formals, List throwTypes, Block body, List typeParams){
        JL5MethodDecl n;
        if (typeParams == null){
            n = new JL5MethodDecl_c(pos, flags, returnType, name, formals, throwTypes, body);
        }
        else {
            n = new JL5MethodDecl_c(pos, flags, returnType, name, formals, throwTypes, body, typeParams);
        }
        return n;
    }
    
    public AnnotationElemDecl AnnotationElemDecl(Position pos, FlagAnnotations flags, TypeNode type, String name, Expr def){
        AnnotationElemDecl n = new AnnotationElemDecl_c(pos, flags, type, name, def);
        return n;
    }
    
    public NormalAnnotationElem NormalAnnotationElem(Position pos, TypeNode name, List elements){
        NormalAnnotationElem n = new NormalAnnotationElem_c(pos, name, elements);
        return n;
    }
    
    public MarkerAnnotationElem MarkerAnnotationElem(Position pos, TypeNode name){
        MarkerAnnotationElem n = new MarkerAnnotationElem_c(pos, name);
        return n;
    }
    
    public SingleElementAnnotationElem SingleElementAnnotationElem(Position pos, TypeNode name, Expr value){
        List l = new TypedList(new LinkedList(), ElementValuePair.class, false);
        l.add(ElementValuePair(pos, "value", value));
        SingleElementAnnotationElem n = new SingleElementAnnotationElem_c(pos, name, l);
        return n;
    }

   
    public ElementValuePair ElementValuePair(Position pos, String name, Expr value){
        ElementValuePair n = new ElementValuePair_c(pos, name, value);
        return n;
    }
    
    public JL5FieldDecl JL5FieldDecl(Position pos, FlagAnnotations flags, TypeNode type, String name, Expr init){
        JL5FieldDecl n = new JL5FieldDecl_c(pos, flags, type, name, init);
        return n;
    }
    public JL5Formal JL5Formal(Position pos, FlagAnnotations flags, TypeNode type, String name){
        JL5Formal n = new JL5Formal_c(pos, flags, type, name);
        return n;
    }
    public JL5Formal JL5Formal(Position pos, FlagAnnotations flags, TypeNode type, String name, boolean variable){
        JL5Formal n = new JL5Formal_c(pos, flags, type, name, variable);
        return n;
    }
    public JL5LocalDecl JL5LocalDecl(Position pos, FlagAnnotations flags, TypeNode type, String name, Expr init){
        JL5LocalDecl n = new JL5LocalDecl_c(pos, flags, type, name, init);
        return n;
    }
    public JL5Local JL5Local(Position pos, String name){
        JL5Local n = new JL5Local_c(pos, name);
        return n;
    }
    public JL5PackageNode JL5PackageNode(Position pos, FlagAnnotations fl, Package package_){
        JL5PackageNode n = new JL5PackageNode_c(pos, fl, package_);
        return n;
    }

    public ParamTypeNode ParamTypeNode(Position pos, List bounds, String id){
        ParamTypeNode n = new ParamTypeNode_c(pos, bounds, id);
        return n;
    }
    
    public BoundedTypeNode BoundedTypeNode(Position pos, BoundedTypeNode.Kind kind, TypeNode bound){
        BoundedTypeNode n = new BoundedTypeNode_c(pos, kind, bound);
        return n;
    }
    
    public AmbQualifierNode JL5AmbQualifierNode(Position pos, QualifierNode qual, String name, List args){
        AmbQualifierNode n = new JL5AmbQualifierNode_c(pos, qual, name, args);
        return n;
    }
    
    public AmbTypeNode JL5AmbTypeNode(Position pos, QualifierNode qual, String name, List args){
        AmbTypeNode n = new JL5AmbTypeNode_c(pos, qual, name, args);
        return n;
    }
    
    public ConstructorCall JL5ThisCall(Position pos, List args, List typeArgs){
        return JL5ThisCall(pos, null, args, typeArgs);
    }

    public ConstructorCall JL5ThisCall(Position pos, Expr outer, List args, List typeArgs){
        ConstructorCall n = new JL5ConstructorCall_c(pos, ConstructorCall.THIS, outer, args, typeArgs);
        return n;
    }

    public ConstructorCall JL5SuperCall(Position pos, List args, List typeArgs){
        return JL5SuperCall(pos, null, args, typeArgs);
    }

    public ConstructorCall JL5SuperCall(Position pos, Expr outer, List args, List typeArgs){
        ConstructorCall n = new JL5ConstructorCall_c(pos, ConstructorCall.SUPER, outer, args, typeArgs);
        return n;
    }

    public Call JL5Call(Position pos, Receiver target, String name, List args, List typeArgs){
        Call n = new JL5Call_c(pos, target, name, args, typeArgs);
        return n;
    }

    public New JL5New(Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body, List typeArgs){
        New n = new JL5New_c(pos, qualifier, tn, arguments, body, typeArgs);
        return n;
    }
    
    public New JL5New(Position pos, TypeNode tn, List arguments, ClassBody body, List typeArgs){
        New n = new JL5New_c(pos, null, tn, arguments, body, typeArgs);
        return n;
    }

    public JL5Instanceof JL5Instanceof(Position pos, Expr expr, TypeNode tn){
        JL5Instanceof n = new JL5Instanceof_c(pos, expr, tn);
        return n;
    }

    public JL5Import JL5Import(Position pos, Import.Kind kind, String name){
        JL5Import n = new JL5Import_c(pos, kind, name);
        return n;
    }
    // TODO:  Override factory methods for overriden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.
}
