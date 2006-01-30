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
import polyglot.util.*;
import java.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

public class JL5MethodDecl_c extends MethodDecl_c implements JL5MethodDecl, ApplicationCheck, SimplifyVisit {

    protected boolean compilerGenerated;
    protected List annotations;
    protected List runtimeAnnotations;
    protected List classAnnotations;
    protected List sourceAnnotations;
    protected List paramTypes;
    
    public JL5MethodDecl_c(Position pos, FlagAnnotations flags, TypeNode returnType, String name, List formals, List throwTypes, Block body){
        super(pos, flags.classicFlags(), returnType, name, formals, throwTypes, body);
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, true);
        }
       
        this.paramTypes = new TypedList(new LinkedList(), TypeNode.class, false);
    }
    
    public JL5MethodDecl_c(Position pos, FlagAnnotations flags, TypeNode returnType, String name, List formals, List throwTypes, Block body, List paramTypes){
        super(pos, flags.classicFlags(), returnType, name, formals, throwTypes, body);
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, true);
        }
        this.paramTypes = paramTypes;
    }
    
    
    public boolean isGeneric(){
        if (!paramTypes.isEmpty()) return true;
        return false;
    }
    
    public boolean isCompilerGenerated(){
        return compilerGenerated;
    }

    public JL5MethodDecl setCompilerGenerated(boolean val){
        JL5MethodDecl_c n = (JL5MethodDecl_c) copy();
        n.compilerGenerated = val;
        return n;
    }

    public List annotations(){
        return this.annotations;
    }

    public JL5MethodDecl annotations(List annotations){
        JL5MethodDecl_c n = (JL5MethodDecl_c) copy();
        n.annotations = annotations;
        return n;    
    }
    
    public List paramTypes(){
        return this.paramTypes;
    }

    public JL5MethodDecl paramTypes(List paramTypes){
        JL5MethodDecl_c n = (JL5MethodDecl_c) copy();
        n.paramTypes = paramTypes;
        return n;
    }
           
    protected MethodDecl_c reconstruct(TypeNode returnType, List formals, List throwTypes, Block body, List annotations, List paramTypes){
        if (returnType != this.returnType || ! CollectionUtil.equals(formals, this.formals) || ! CollectionUtil.equals(throwTypes, this.throwTypes) || body != this.body || !CollectionUtil.equals(annotations, this.annotations) || !CollectionUtil.equals(paramTypes, this.paramTypes)) {
            JL5MethodDecl_c n = (JL5MethodDecl_c) copy();
            n.returnType = returnType;
            n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
            n.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
            n.body = body;
            n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, true);
            n.paramTypes = paramTypes;
            return n;
        }
        return this;
                                                            
    }

    public NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException {
        if (ar.kind() == JL5AmbiguityRemover.TYPE_VARS) {
            return ar.bypass(formals).bypass(returnType).bypass(throwTypes).bypass(body);
        }
        else {
            return super.disambiguateEnter(ar);
        }
    }

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        if (ar.kind() == AmbiguityRemover.SIGNATURES) {
            Context c = ar.context();
            TypeSystem ts = ar.typeSystem();
            ParsedClassType ct = c.currentClassScope();
            JL5MethodInstance mi = (JL5MethodInstance)makeMethodInstance(ct, ts);
            List pTypes = new ArrayList();
            for (Iterator it = paramTypes.iterator(); it.hasNext(); ){
                pTypes.add(((ParamTypeNode)it.next()).type());
            }
            mi.typeVariables(pTypes);
            return flags(mi.flags()).methodInstance(mi);
         }
         return this;
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        // check no duplicate annotations used
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        ts.checkDuplicateAnnotations(annotations);
   
        // check throws clauses are not parameterized
        for (Iterator it = throwTypes.iterator(); it.hasNext(); ){
            TypeNode tn = (TypeNode)it.next();
            Type next = tn.type();
            if (next instanceof ParameterizedType){
                throw new SemanticException("Cannot use parameterized type "+next+" in a throws clause", tn.position());
            }
        }
        
        // check at most last formal is variable
        for (int i = 0; i < formals.size(); i++){
            JL5Formal f = (JL5Formal)formals.get(i);
            if (i != formals.size()-1 && f.isVariable()){
                throw new SemanticException("Only last formal can be variable in method declaration.", f.position());
            }
        }

        // repeat super class type checking so it can be specialized
        // to handle inner enum classes which indeed do have
        // static methods
        if (tc.context().currentClass().flags().isInterface()) {
            if (flags().isProtected() || flags().isPrivate()) {
                throw new SemanticException("Interface methods must be public.",
                                            position());
            }
        }

        try {
            ts.checkMethodFlags(flags());
        }
        catch (SemanticException e) {
            throw new SemanticException(e.getMessage(), position());
        }

	    if (body == null && ! (flags().isAbstract() || flags().isNative())) {
	        throw new SemanticException("Missing method body.", position());
	    }

	    if (body != null && flags().isAbstract()) {
	        throw new SemanticException(
		    "An abstract method cannot have a body.", position());
	    }

	    if (body != null && flags().isNative()) {
	        throw new SemanticException(
		    "A native method cannot have a body.", position());
	    }

        for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
            TypeNode tn = (TypeNode) i.next();
            Type t = tn.type();
            if (! t.isThrowable()) {
                throw new SemanticException("Type \"" + t +
                    "\" is not a subclass of \"" + ts.Throwable() + "\".",
                    tn.position());
            }
        }

        // check that inner classes do not declare static methods
        // unless class is enum
        if (flags().isStatic() && !JL5Flags.isEnumModifier(methodInstance().container().toClass().flags()) && 
              methodInstance().container().toClass().isInnerClass()) {
            // it's a static method in an inner class.
            throw new SemanticException("Inner classes cannot declare " + 
                    "static methods.", this.position());             
        }

        overrideMethodCheck(tc);

        return this;
    }

    public Node applicationCheck(ApplicationChecker appCheck) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)appCheck.typeSystem();
        for( Iterator it = annotations.iterator(); it.hasNext(); ){
            AnnotationElem next = (AnnotationElem)it.next();
            ts.checkAnnotationApplicability(next, this);
        }
        return this;         
    }
    
    public Node visitChildren(NodeVisitor v){
        List annotations = visitList(this.annotations, v);
        List paramTypes = visitList(this.paramTypes, v);
        List formals = visitList(this.formals, v);
        TypeNode returnType = (TypeNode) visitChild(this.returnType, v);
        List throwTypes = visitList(this.throwTypes, v);
        Block body = (Block) visitChild(this.body, v);
        return reconstruct(returnType, formals, throwTypes, body, annotations, paramTypes);
    }
    
    public void translate(CodeWriter w, Translator tr){
        if (isCompilerGenerated()) return;
        
        for (Iterator it = annotations.iterator(); it.hasNext(); ){
            print((AnnotationElem)it.next(), w, tr);
        }

        super.translate(w, tr);
    }

    public void prettyPrintHeader(Flags flags, CodeWriter w, PrettyPrinter tr) {
        w.begin(0);
        w.write(flags.translate());

        if ((paramTypes != null) && !paramTypes.isEmpty()){
            w.write("<");
            for (Iterator it = paramTypes.iterator(); it.hasNext(); ){
                ParamTypeNode next = (ParamTypeNode)it.next();
                print(next, w, tr);
                if (it.hasNext()){
                    w.write(", ");
                }
            }
            w.write("> ");
        }
        
        print(returnType, w, tr);
        w.write(" " + name + "(");
        w.begin(0);

        for (Iterator i = formals.iterator(); i.hasNext(); ) {
            Formal f = (Formal) i.next();
            print(f, w, tr);

            if (i.hasNext()) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }

        w.end();
        w.write(")");

        if (! throwTypes().isEmpty()) {
            w.allowBreak(6);
            w.write("throws ");

            for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
                TypeNode tn = (TypeNode) i.next();
                print(tn, w, tr);

                if (i.hasNext()) {
                    w.write(",");
                    w.allowBreak(4, " ");
                }
            }
        }

        w.end();
    }


    public Node simplify(SimplifyVisitor sv) throws SemanticException {
        runtimeAnnotations = new ArrayList();
        classAnnotations = new ArrayList();
        sourceAnnotations = new ArrayList();
        ((JL5TypeSystem)sv.typeSystem()).sortAnnotations(annotations, runtimeAnnotations, classAnnotations, sourceAnnotations);

        /*JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();
        
        ArrayList newFormals = new ArrayList();
        ArrayList newFormalTypes = new ArrayList();
        for (Iterator it = formals().iterator(); it.hasNext(); ){
            JL5Formal formal = (JL5Formal)it.next();
            if (formal.type().type() instanceof ParameterizedType){
                newFormalTypes.add(((ParameterizedType)formal.type().type()).baseType());
                FlagAnnotations fl = new FlagAnnotations();
                fl.classicFlags(formal.flags());
                fl.annotations(formal.annotations());
                JL5Formal newFormal = nf.JL5Formal(formal.position(), fl, nf.CanonicalTypeNode(formal.type().position(), ((ParameterizedType)formal.type().type()).baseType()), formal.name());
                newFormal = (JL5Formal)newFormal.localInstance(ts.localInstance(formal.localInstance().position(), formal.localInstance().flags(), ((ParameterizedType)formal.type().type()).baseType(), formal.name()));
                newFormals.add(newFormal);
            }
            else {
                newFormalTypes.add(formal.type().type());
                newFormals.add(formal);
            }
        }
        Type newReturnType = methodInstance().returnType();
        if (newReturnType instanceof ParameterizedType){
            newReturnType = ((ParameterizedType)newReturnType).baseType();
        }
        
        JL5MethodInstance mi = (JL5MethodInstance)ts.methodInstance(methodInstance().position(), methodInstance().container(), methodInstance().flags(), newReturnType, methodInstance().name(), newFormalTypes, methodInstance().throwTypes());
       
        FlagAnnotations mfl = new FlagAnnotations();
        mfl.classicFlags(flags());
        mfl.annotations(annotations());
        JL5MethodDecl md = nf.JL5MethodDecl(position(), mfl, nf.CanonicalTypeNode(returnType().position(), newReturnType), name(), newFormals, throwTypes(), body(), paramTypes());
        md = (JL5MethodDecl)md.methodInstance(mi);
        return md;*/
        return this;
    }

    public List runtimeAnnotations(){
        return runtimeAnnotations;
    }
    public List classAnnotations(){
        return classAnnotations;
    }
    public List sourceAnnotations(){
        return sourceAnnotations;
    }
}
