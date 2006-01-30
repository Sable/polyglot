/* Copyright (C) 2006 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

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
import polyglot.types.*;

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

    public JL5Call JL5Call(Position pos, Receiver target, String name, List args, List typeArgs){
        JL5Call n = new JL5Call_c(pos, target, name, args, typeArgs);
        return n;
    }

    public JL5New JL5New(Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body, List typeArgs){
        JL5New n = new JL5New_c(pos, qualifier, tn, arguments, body, typeArgs);
        return n;
    }
    
    public JL5New JL5New(Position pos, TypeNode tn, List arguments, ClassBody body, List typeArgs){
        JL5New n = new JL5New_c(pos, null, tn, arguments, body, typeArgs);
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
    public JL5CanonicalTypeNode JL5CanonicalTypeNode(Position pos, Type t){
        JL5CanonicalTypeNode n = new JL5CanonicalTypeNode_c(pos, t);
        return n;
    }
    public JL5Catch JL5Catch(Position pos, Formal formal, Block body){
        JL5Catch n = new JL5Catch_c(pos, formal, body);
        return n;
    }
    public JL5NewArray JL5NewArray(Position pos, TypeNode baseType, List dims, int addDims, ArrayInit init){
        JL5NewArray n = new JL5NewArray_c(pos, baseType, dims, addDims, init);
        return n;
    }
    public JL5Switch JL5Switch(Position pos, Expr expr, List elements){
        JL5Switch n = new JL5Switch_c(pos, expr, elements);
        return n;
    }
    public JL5If JL5If(Position pos, Expr cond, Stmt conseq, Stmt altern){
        JL5If n = new JL5If_c(pos, cond, conseq, altern);
        return n;
    }
    public JL5Conditional JL5Conditional(Position pos, Expr cond, Expr conseq, Expr altern){
        JL5Conditional n = new JL5Conditional_c(pos, cond, conseq, altern);
        return n;
    }
    
    public JL5Assert JL5Assert(Position pos, Expr cond, Expr errorMsg){
        JL5Assert n = new JL5Assert_c(pos, cond, errorMsg);
        return n;
    }
    public JL5Cast JL5Cast(Position pos, TypeNode castType, Expr expr){
        JL5Cast n = new JL5Cast_c(pos, castType, expr);
        return n;
    }
    public JL5Binary JL5Binary(Position pos, Expr left, Binary.Operator op, Expr right){
        JL5Binary n = new JL5Binary_c(pos, left, op, right);
        return n;
    }
    public JL5Unary JL5Unary(Position pos, Unary.Operator op, Expr expr){
        JL5Unary n = new JL5Unary_c(pos, op, expr);
        return n;
    }   
    public Assign JL5Assign(Position pos, Expr left, Assign.Operator op, Expr right){
        if (left instanceof Local){
            return JL5LocalAssign(pos, left, op, right);
        }
        else if (left instanceof Field){
            return JL5FieldAssign(pos, left, op, right);
        }
        else if (left instanceof ArrayAccess){
            return JL5ArrayAccessAssign(pos, left, op, right);
        }
        else {
            return JL5AmbAssign(pos, left, op, right);
        }
    }
    public JL5LocalAssign JL5LocalAssign(Position pos, Expr left, Assign.Operator op, Expr right){
        JL5LocalAssign n = new JL5LocalAssign_c(pos, (Local)left, op, right);
        return n;
    }
    public JL5FieldAssign JL5FieldAssign(Position pos, Expr left, Assign.Operator op, Expr right){
        JL5FieldAssign n = new JL5FieldAssign_c(pos, (Field)left, op, right);
        return n;
    }
    public JL5ArrayAccessAssign JL5ArrayAccessAssign(Position pos, Expr left, Assign.Operator op, Expr right){
        JL5ArrayAccessAssign n = new JL5ArrayAccessAssign_c(pos, (ArrayAccess)left, op, right);
        return n;
    }
    public JL5AmbAssign JL5AmbAssign(Position pos, Expr left, Assign.Operator op, Expr right){
        JL5AmbAssign n = new JL5AmbAssign_c(pos, left, op, right);
        return n;
    }
    public JL5Return JL5Return(Position pos, Expr expr){
        JL5Return n = new JL5Return_c(pos, expr);
        return n;
    }
    public JL5ArrayAccess JL5ArrayAccess(Position pos, Expr array, Expr index){
        JL5ArrayAccess n = new JL5ArrayAccess_c(pos, array, index);
        return n;
    }

    public JL5Let JL5Let(Position pos, LocalDecl localDecl, Expr beta){
        JL5Let n = new JL5Let_c(pos, localDecl, beta);
        return n;
    }
    
    public Binary.Operator getBinOpFromAssignOp(Assign.Operator op){
        if (op == Assign.ADD_ASSIGN) return Binary.ADD;
        if (op == Assign.BIT_OR_ASSIGN) return Binary.BIT_OR;
        if (op == Assign.BIT_AND_ASSIGN) return Binary.BIT_AND;
        if (op == Assign.BIT_XOR_ASSIGN) return Binary.BIT_XOR;
        if (op == Assign.DIV_ASSIGN) return Binary.DIV;
        if (op == Assign.MOD_ASSIGN) return Binary.MOD;
        if (op == Assign.MUL_ASSIGN) return Binary.MUL;
        if (op == Assign.SHL_ASSIGN) return Binary.SHL;
        if (op == Assign.SHR_ASSIGN) return Binary.SHR;
        if (op == Assign.SUB_ASSIGN) return Binary.SUB;
        if (op == Assign.USHR_ASSIGN) return Binary.USHR;
        else throw new RuntimeException("Unknown op: "+op); 
    }
    
    public Expr createUnboxed(Position pos, Expr orig, Type toType, TypeSystem typeSystem, Context context) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem;
        String mName = null;
        Type type = null;
        if (ts.equals(orig.type(), ts.IntegerWrapper())){
            mName = "intValue";
            type = ts.Int();
        }
        else if (ts.equals(orig.type(), ts.ShortWrapper())){
            mName = "shortValue";
            type = ts.Short();
        }
        else if (ts.equals(orig.type(), ts.ByteWrapper())){
            mName = "byteValue";
            type =ts.Byte();
        }
        else if (ts.equals(orig.type(), ts.CharacterWrapper())){
            mName = "charValue";
            type = ts.Char();
        }
        else if (ts.equals(orig.type(), ts.BooleanWrapper())){
            mName = "booleanValue";
            type = ts.Boolean();
        }
        else if (ts.equals(orig.type(), ts.LongWrapper())){
            mName = "longValue";
            type = ts.Long();
        }
        else if (ts.equals(orig.type(), ts.FloatWrapper())){
            mName = "floatValue";
            type = ts.Float();
        }
        else if (ts.equals(orig.type(), ts.DoubleWrapper())){
            mName = "doubleValue";
            type = ts.Double();
        }
        else {
            return orig;
        }
        JL5Call node = JL5Call(pos, orig, mName, new ArrayList(), new ArrayList());
        node = (JL5Call)node.type(type);
        return node.methodInstance(ts.findMethod(orig.type().toReference(), mName, new ArrayList(), context.currentClass()));
    }

    public Expr createBoxed(Position pos, Expr orig, Type toType, TypeSystem typeSystem, Context context) throws SemanticException {

        JL5TypeSystem ts = (JL5TypeSystem)typeSystem;
        ClassType container = null;
        Type argType = orig.type();
        
        if (ts.equals(orig.type(), ts.Int())){
            container = ts.IntegerWrapper();
            if (ts.equals(toType, ts.ByteWrapper())){
                argType = ts.Byte();
                container = ts.ByteWrapper();
            }
            else if (ts.equals(toType, ts.ShortWrapper())){
                argType = ts.Short();
                container = ts.ShortWrapper();
            }
            else if (ts.equals(toType, ts.CharacterWrapper())){
                argType = ts.Char();
                container = ts.CharacterWrapper();
            }
            //container = ts.IntegerWrapper();
        }
        else if (ts.equals(orig.type(), ts.Short())){
            container = ts.ShortWrapper();
        }
        else if (ts.equals(orig.type(), ts.Byte())){
            container = ts.ByteWrapper();
        }
        else if (ts.equals(orig.type(), ts.Char())){
            container = ts.CharacterWrapper();
        }
        else if (ts.equals(orig.type(), ts.Boolean())){
            container = ts.BooleanWrapper();
        }
        else if (ts.equals(orig.type(), ts.Long())){
            container = ts.LongWrapper();
        }
        else if (ts.equals(orig.type(), ts.Float())){
            container = ts.FloatWrapper();
        }
        else if (ts.equals(orig.type(), ts.Double())){
            container = ts.DoubleWrapper();
        }
        else {
            return orig;
        }
        
        ArrayList args = new ArrayList();
        ArrayList argTypes = new ArrayList();
        args.add(orig);
        argTypes.add(argType);
        
        JL5Call node = JL5Call(pos, CanonicalTypeNode(pos, container), "valueOf", args, new ArrayList());
        node = (JL5Call)node.type(container);
        return node.methodInstance(ts.findMethod(container, "valueOf", argTypes, context.currentClass()));
        
        /*JL5New node = JL5New(pos, null, CanonicalTypeNode(pos, container), args, null, new ArrayList());
        return node.constructorInstance(ts.findConstructor(container, argTypes, context.currentClass())).type(container);*/
    }

    
    // TODO:  Override factory methods for overriden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.
}
