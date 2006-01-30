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
import java.util.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

public class JL5Call_c extends Call_c implements JL5Call, SimplifyVisit, BoxingVisit, UnboxingVisit, GenericCastInsertionVisit {

    protected List typeArguments;

    public JL5Call_c(Position pos, Receiver target, String name, List arguments, List typeArguments){
        super(pos, target, name, arguments);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public JL5Call typeArguments(List args){
        JL5Call_c n = (JL5Call_c) copy();
        n.typeArguments = args;
        return n;
    }

    public JL5Call_c reconstruct(Receiver target, List arguments, List typeArgs) {
        if (target != this.target || ! CollectionUtil.equals(arguments, this.arguments) || ! CollectionUtil.equals(typeArgs, this.typeArguments)){
            JL5Call_c n = (JL5Call_c)copy();
            n.target = target;
            n.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
            n.typeArguments = TypedList.copyAndCheck(typeArgs, TypeNode.class, false);
            return n;
        }
        return this;
    }
    
    public Node visitChildren(NodeVisitor v){
        Receiver target = (Receiver) visitChild(this.target, v);
        List arguments = visitList(this.arguments, v);
        List typeArgs = visitList(this.typeArguments, v);
        return reconstruct(target, arguments, typeArgs);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5Call_c n = null;
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        JL5Context c = (JL5Context)tc.context();
        // three cases for type inference for gen meths
        // explicit type args
        // infered from call args
        // infered from lhs of assign (matters later only)
        // else no context -> no inferring
        if (typeArguments() != null && !typeArguments.isEmpty()){
            List inferred = new ArrayList();
            for (Iterator it = typeArguments().iterator(); it.hasNext(); ){
                inferred.add(((TypeNode)it.next()).type());
            }
            List argTypes = new ArrayList(this.arguments().size());
            for (Iterator i = this.arguments().iterator(); i.hasNext(); ) {
                Expr e = (Expr) i.next();
                argTypes.add(e.type());
            }
            if (this.target() == null){
                n = (JL5Call_c)this.typeCheckNullTarget(tc, argTypes, inferred);
            }
            else {
                ReferenceType targetType = this.findTargetType();
                MethodInstance mi = ts.findGenericMethod((ClassType)targetType, this.name(), argTypes, c.currentClass(), inferred);
                
                boolean staticContext = (this.target instanceof TypeNode);

                if (staticContext && !mi.flags().isStatic()){
                    throw new SemanticException("Cannot call non-static method " + this.name+ " of " + targetType + " in static "+ "context.", this.position());
                }

                if (this.target instanceof Special && ((Special)this.target).kind() == Special.SUPER && mi.flags().isAbstract()){
                    throw new SemanticException("Cannot call an abstract method " +"of the super class", this.position()); 
                }

                n = (JL5Call_c)this.methodInstance(mi).type(mi.returnType());
                n.checkConsistency(c);
            }
             
        }
        else {
            n = (JL5Call_c)super.typeCheck(tc);
        }


        return checkTypeArguments(tc, n);
    }

    protected Node typeCheckNullTarget(TypeChecker tc, List argTypes, List inferredTypes) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)tc.nodeFactory();
        JL5Context c = (JL5Context)tc.context();

        // the target is null, and thus implicit
        // let's find the target, using the context, and
        // set the target appropriately, and then type check
        // the result
        MethodInstance mi =  c.findGenericMethod(this.name(), argTypes, inferredTypes);
        
        Receiver r;
        if (mi.flags().isStatic()) {
            r = nf.CanonicalTypeNode(position(), mi.container()).type(mi.container());
        } else {
            // The method is non-static, so we must prepend with "this", but we
            // need to determine if the "this" should be qualified.  Get the
            // enclosing class which brought the method into scope.  This is
            // different from mi.container().  mi.container() returns a super type
            // of the class we want.
            ClassType scope = c.findMethodScope(name);

            if (! ts.equals(scope, c.currentClass())) {
                r = nf.This(position(),
                            nf.CanonicalTypeNode(position(), scope)).type(scope);
            }
            else {
                r = nf.This(position()).type(scope);
            }
        }

        // we call typeCheck on the reciever too.
        r = (Receiver)r.typeCheck(tc);
        return this.targetImplicit(true).target(r).del().typeCheck(tc);
        
    }

    private Node checkTypeArguments(TypeChecker tc, JL5Call_c n) throws SemanticException {
       
        // can only call a method with type args if it was declared as generic
        if (!typeArguments.isEmpty() && !((JL5MethodInstance)n.methodInstance()).isGeneric()) {
            throw new SemanticException("Cannot call method: "+n.methodInstance().name()+" with type arguments", position());
        }
        
        if (!typeArguments().isEmpty() && typeArguments.size() != ((JL5MethodInstance)n.methodInstance()).typeVariables().size()){
            throw new SemanticException("Cannot call "+n.name()+" with wrong number of type arguments", position());
        }
        
        JL5MethodInstance mi = (JL5MethodInstance)n.methodInstance();
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        
        // wildcards are not allowed for type args for generic call
        for (int i = 0; i < typeArguments.size(); i++) {
            TypeNode correspondingArg = (TypeNode)typeArguments.get(i);
            if (correspondingArg instanceof BoundedTypeNode){
                throw new SemanticException("Wildcard argument not allowed here", correspondingArg.position());
            }
        }

        // type check call arguments
        if (target() != null && target().type() instanceof ParameterizedType){
            for (int i = 0; i < mi.formalTypes().size(); i++){
                Type t = (Type)mi.formalTypes().get(i);
                if (t instanceof IntersectionType){
                    Type other = ts.findRequiredType((IntersectionType)t, (ParameterizedType)target().type());
                    if (!ts.isImplicitCastValid(((Expr)arguments().get(i)).type(), other)){
                        throw new SemanticException("Found arg of type: "+((Expr)arguments().get(i)).type()+" expected: "+other, ((Expr)arguments().get(i)).position());
                    }
                }
            }
        

            // set return type
            if (mi.returnType() instanceof IntersectionType){
                Type other = ts.findRequiredType((IntersectionType)mi.returnType(), (ParameterizedType)target().type());
                return n.type(other);
            }
            if (mi.returnType() instanceof ParameterizedType){
                if (ts.equals(((ParameterizedType)mi.returnType()).baseType(), ((ParameterizedType)target.type()).baseType())){
                    return n.type((ParameterizedType)target().type());
                }
            }

        }
        return n.type(mi.returnType());
        
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        if (!targetImplicit){
            if (target instanceof Expr) {
                printSubExpr((Expr) target, w, tr);
                w.write(".");
            }
            else if (target != null) {
                print(target, w, tr);
                w.write(".");
            }
        }
        
        if (typeArguments.size()!=0){
            w.write("<");
            for (Iterator it = typeArguments.iterator(); it.hasNext(); ){
                print((TypeNode)it.next(), w, tr);
                if (it.hasNext()){
                    w.write(", ");
                }
            }
            w.write(">");
        }
       
        w.write(name + "(");
        w.begin(0);

        for(Iterator i = arguments.iterator(); i.hasNext();) {
            Expr e = (Expr) i.next();
            print(e, w, tr);
            if (i.hasNext()) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }
        w.end();
        w.write(")");
                        
    }

    public Node boxing(BoxingVisitor bv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)bv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)bv.nodeFactory();
        ArrayList newArgs = new ArrayList();
        for (int i = 0; i < methodInstance().formalTypes().size(); i++){
            Type t1 = (Type)methodInstance().formalTypes().get(i);
            Expr ex = (Expr)arguments().get(i);    
            if (ts.needsBoxing(t1, ex.type())){
                newArgs.add(nf.createBoxed(ex.position(), ex, t1, ts, bv.context()));
            }
            else {
                newArgs.add(ex);
            }
        }
        return arguments(newArgs);
    }
    
    public Node unboxing(UnboxingVisitor bv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)bv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)bv.nodeFactory();
        ArrayList newArgs = new ArrayList();
        for (int i = 0; i < methodInstance().formalTypes().size(); i++){
            Type t1 = (Type)methodInstance().formalTypes().get(i);
            Expr ex = (Expr)arguments().get(i);    
            if (ts.needsUnboxing(t1, ex.type())){
                newArgs.add(nf.createUnboxed(ex.position(), ex, t1, ts, bv.context()));
            }
            else {
                newArgs.add(ex);
            }
        }
        return arguments(newArgs);
    
    }
    
    public Node simplify(SimplifyVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();
    
        /*System.out.println("call: "+this+" type: "+type());
        System.out.println("mi ret type: "+methodInstance().returnType());
        if (methodInstance().returnType() instanceof IntersectionType){
            JL5Cast node = nf.JL5Cast(position(), type(), this);
            node = (JL5Cast)node.type(type());
            node = (JL5Cast)methodInstance(m
        }*/
        List newArgs = new ArrayList();
        for (int i = 0; i < methodInstance().formalTypes().size(); i++){
            Type t = (Type)methodInstance().formalTypes().get(i);
            if (t instanceof JL5ArrayType && ((JL5ArrayType)t).isVariable()){
                if (arguments().size() == i+1  && arguments().get(i) instanceof NullLit){
                    newArgs.add(arguments().get(i));
                }
                else if (arguments().size() == i+1 && arguments().get(i) instanceof NewArray && !(((NewArray)arguments().get(i)).baseType().type().isPrimitive() && ((ArrayType)t).base().isClass()) ){
                    // not sure about this 
                    newArgs.add(arguments().get(i));
                }
                else {
                    List elements = new ArrayList();
                    for (int j = i; j < arguments().size(); j++){
                        elements.add(arguments().get(j));
                    }
                    ArrayInit ai = nf.ArrayInit(position(), elements);
                    ai = (ArrayInit)ai.type(((JL5ArrayType)t).base());
                    /*List dims = new ArrayList();
                    dims.add(nf.IntLit(position(), IntLit.INT, elements.size()));*/
                    NewArray na = nf.JL5NewArray(position(), nf.CanonicalTypeNode(position(), ((JL5ArrayType)t).base()), Collections.EMPTY_LIST, 1, ai); 
                    na = (NewArray)na.type(t);
                    newArgs.add(na);
                }
            }
            else {
                newArgs.add(arguments().get(i));
            }
        }
        return arguments(newArgs);
    }

    public Node genCastIns(GenericCastInsertionVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();

        if (!ts.equals(methodInstance().returnType(), type())){
            Type castType = type();
            if (type() instanceof AnyType) {
                castType = ((AnyType)type).upperBound();
            }
            else if (type() instanceof AnySuperType){
                castType = ((AnySuperType)type).upperBound();
            }
            else if (type() instanceof AnySubType){
                castType = ((AnySubType)type).bound();
            }
            return nf.JL5Cast(position(), nf.CanonicalTypeNode(position(), castType), this).type(type());
        }
        return this;
    }
}
