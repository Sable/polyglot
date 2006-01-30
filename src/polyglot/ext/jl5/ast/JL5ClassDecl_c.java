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

import java.util.*;

import polyglot.ast.*;
import polyglot.main.Report;
import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import polyglot.ext.jl.ast.*;

/**
 * A <code>ClassDecl</code> is the definition of a class, abstract class,
 * or interface. It may be a public or other top-level class, or an inner
 * named class, or an anonymous class.
 */
public class JL5ClassDecl_c extends ClassDecl_c implements JL5ClassDecl, ApplicationCheck, SimplifyVisit {
    protected List annotations;
    protected List runtimeAnnotations;
    protected List classAnnotations;
    protected List sourceAnnotations;
    protected List paramTypes;

    public JL5ClassDecl_c(Position pos, FlagAnnotations flags, String name, TypeNode superClass, List interfaces, ClassBody body) {
	    super(pos, flags.classicFlags(), name, superClass, interfaces, body);
        if (flags.annotations() != null){
            this.annotations = TypedList.copyAndCheck(flags.annotations(), AnnotationElem.class, false);
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, false);
        }
        
    }

    public JL5ClassDecl_c(Position pos, FlagAnnotations fl, String name, TypeNode superType, List interfaces, ClassBody body, List paramTypes){

        super(pos, fl.classicFlags(), name, superType, interfaces, body);
        if (fl.annotations() != null){
            this.annotations = TypedList.copyAndCheck(fl.annotations(), AnnotationElem.class, false);
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, false);
        }
        this.paramTypes = paramTypes;
    }

    public List annotations(){
        return this.annotations;
    }
    
    public JL5ClassDecl annotations(List annotations){
        if (annotations != null){
            JL5ClassDecl_c n = (JL5ClassDecl_c) copy();
            n.annotations = annotations;
            return n;
        }
        return this;
    }
    
    public List paramTypes(){
        return this.paramTypes;
    }
    
    public JL5ClassDecl paramTypes(List types){
        JL5ClassDecl_c n = (JL5ClassDecl_c) copy();
        n.paramTypes = types;
        return n;
    }
    
    protected ClassDecl reconstruct(TypeNode superClass, List interfaces, ClassBody body, List annotations, List paramTypes){
        if (superClass != this.superClass || !CollectionUtil.equals(interfaces, this.interfaces) || body != this.body || !CollectionUtil.equals(annotations, this.annotations) || !CollectionUtil.equals(paramTypes, this.paramTypes)){
            JL5ClassDecl_c n = (JL5ClassDecl_c) copy();
            n.superClass = superClass;
            n.interfaces = TypedList.copyAndCheck(interfaces, TypeNode.class, false);
            n.body = body;
            n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, false);
            n.paramTypes = paramTypes;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        List annots = visitList(this.annotations, v); 
        List paramTypes = visitList(this.paramTypes, v);
        TypeNode superClass = (TypeNode) visitChild(this.superClass, v);
        List interfaces = visitList(this.interfaces, v);
        ClassBody body = (ClassBody) visitChild(this.body, v);
        return reconstruct(superClass, interfaces, body, annots, paramTypes);
    }

    public Context enterScope(Node child, Context c){
        return super.enterScope(child, c);
    }
    
    protected void disambiguateSuperType(AmbiguityRemover ar) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)ar.typeSystem();
        if (JL5Flags.isAnnotationModifier(flags())){
            this.type.superType(ts.Annotation());
        }
        else {
            super.disambiguateSuperType(ar);
        }
    }

    // still need this - will cause an extra disamb pass which will permit
    // the type variables to fully disambigute themselves
    // before they may be needed as args in superClass or interfaces
    public NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException {
        if (ar.kind() == JL5AmbiguityRemover.TYPE_VARS) {
            NodeVisitor nv = ar.bypass(superClass).bypass(interfaces);
            return nv;
        }
        else {
            return super.disambiguateEnter(ar);
        }
    }       

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        Node n = super.disambiguate(ar);
        addTypeParameters();
        return n;
    }
    
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (JL5Flags.isEnumModifier(flags()) && flags().isAbstract()){
            throw new SemanticException("Enum types cannot have abstract modifier", this.position());
        }
        if (JL5Flags.isEnumModifier(flags()) && flags().isPrivate() && !type().isInnerClass()){
            throw new SemanticException("Enum types cannot have explicit private modifier", this.position());
        }
        if (JL5Flags.isEnumModifier(flags()) && flags().isFinal()){
            throw new SemanticException("Enum types cannot have explicit final modifier", this.position());
        }
        if (JL5Flags.isAnnotationModifier(flags()) && flags().isPrivate()){
            throw new SemanticException("Annotation types cannot have explicit private modifier", this.position());
        }

        if (type().superType() != null && JL5Flags.isEnumModifier(type().superType().toClass().flags())){
            throw new SemanticException("Cannot extend enum type", position());
        }

        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        ts.checkDuplicateAnnotations(annotations);
        
        if (ts.equals(ts.Object(), type()) && !paramTypes.isEmpty()){
            throw new SemanticException("Type: "+type()+" cannot declare type variables.", position());
        }
        
        // check not extending java.lang.Throwable (or any of its subclasses)
        // with a generic class
        if (type().superType() != null && ts.isSubtype(type().superType(), ts.Throwable()) && !paramTypes.isEmpty()){
            throw new SemanticException("Cannot subclass java.lang.Throwable or any of its subtypes with a generic class", superClass().position());
        }
       
        // check duplicate type variable decls
        for (int i = 0; i < paramTypes.size(); i++){
            TypeNode ti = (TypeNode)paramTypes.get(i);
            for (int j = i+1; j <paramTypes.size(); j++){
                TypeNode tj = (TypeNode)paramTypes.get(j);
                if (ts.equals(ti.type(), tj.type())){
                    throw new SemanticException("Duplicate type variable declaration.", tj.position());
                }
            }
        }
        
        // set up ct with annots
        JL5ParsedClassType ct = (JL5ParsedClassType)type();
        ct.annotations(this.annotations);

        if (JL5Flags.isEnumModifier(flags())){
            for(Iterator it = type().constructors().iterator(); it.hasNext(); ){
                ConstructorInstance ci = (ConstructorInstance)it.next();
                if (!ci.flags().clear(Flags.PRIVATE).equals(Flags.NONE)){
                    throw new SemanticException("Modifier "+ci.flags().clear(Flags.PRIVATE)+" not allowed here", ci.position());
                }
            }
        }
        
        checkSuperTypeTypeArgs(tc); 
        
        return super.typeCheck(tc);    
    }
  
    private void checkSuperTypeTypeArgs(TypeChecker tc) throws SemanticException {
        List allInterfaces = new ArrayList();
        allInterfaces.addAll(type().interfaces());
        if (((ParsedClassType)type()).superType() != null){
            allInterfaces.addAll(((ParsedClassType)((ParsedClassType)type()).superType()).interfaces());
        }
    
        for (int i = 0; i < allInterfaces.size(); i++){
            Type next = (Type)allInterfaces.get(i);
            for (int j = i+1; j < allInterfaces.size(); j++){
                Type other = (Type)allInterfaces.get(j);
                if (next instanceof ParameterizedType && other instanceof ParameterizedType){
                    if (tc.typeSystem().equals(((ParameterizedType)next).baseType(), ((ParameterizedType)other).baseType()) && !tc.typeSystem().equals(next, other)){
                        throw new SemanticException(((ParameterizedType)next).baseType()+" cannot be inherited with different type arguments.", position());
                    }
                }
                else if (next instanceof ParameterizedType){
                    if (tc.typeSystem().equals(((ParameterizedType)next).baseType(), other)){
                        throw new SemanticException(((ParameterizedType)next).baseType()+" cannot be inherited with different type arguments.", position());
                    }
                }
                else if (other instanceof ParameterizedType){
                    if (tc.typeSystem().equals(((ParameterizedType)other).baseType(), next)){
                        throw new SemanticException(((ParameterizedType)other).baseType()+" cannot be inherited with different type arguments.", position());
                    }
                }
            }
        }
    }
    public Node applicationCheck(ApplicationChecker appCheck) throws SemanticException {
        
        // check proper used of predefined annotations
        JL5TypeSystem ts = (JL5TypeSystem)appCheck.typeSystem();
        for( Iterator it = annotations.iterator(); it.hasNext(); ){
            AnnotationElem next = (AnnotationElem)it.next();
            ts.checkAnnotationApplicability(next, this);
        }

        // check annotation circularity
        if (JL5Flags.isAnnotationModifier(flags())){
            JL5ParsedClassType ct = (JL5ParsedClassType)type();
            for (Iterator it = ct.annotationElems().iterator(); it.hasNext(); ){
                AnnotationElemInstance ai = (AnnotationElemInstance)it.next();
                if (ai.type() instanceof ClassType && ((ClassType)((ClassType)ai.type()).superType()).fullName().equals("java.lang.annotation.Annotation")){
                    JL5ParsedClassType other = (JL5ParsedClassType)ai.type();
                    for (Iterator otherIt = other.annotationElems().iterator(); otherIt.hasNext(); ){
                        AnnotationElemInstance aj = (AnnotationElemInstance)otherIt.next();
                        if (aj.type().equals(ct)) {
                            throw new SemanticException("cyclic annotation element type", aj.position());
                        }
                    }
                }
            }
        }
        return this;    
    }
    
    public Node addMembers(AddMemberVisitor tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();
        NodeFactory nf = tc.nodeFactory();
        JL5ClassDecl n = (JL5ClassDecl)addGenEnumMethods(ts, nf);
        //addTypeParameters();
        return n.addDefaultConstructorIfNeeded(ts, nf);
    }

    public Node addDefaultConstructorIfNeeded(TypeSystem ts, NodeFactory nf){
        return super.addDefaultConstructorIfNeeded(ts, nf);
    }
    
    protected void addTypeParameters(){
        for (Iterator it = paramTypes.iterator(); it.hasNext(); ){
            ((JL5ParsedClassType)this.type()).addTypeVariable((IntersectionType)((ParamTypeNode)it.next()).type());
        }
    }
    
    protected Node addGenEnumMethods(TypeSystem ts, NodeFactory nf){
        if (JL5Flags.isEnumModifier(type.flags())){
            
            JL5ClassBody newBody = (JL5ClassBody)body();
            // add values method
            FlagAnnotations vmFlags = new FlagAnnotations();
            vmFlags.classicFlags(Flags.PUBLIC.set(Flags.STATIC.set(Flags.FINAL)));
            Block valuesB = nf.Block(position());
            valuesB = valuesB.append(nf.Return(position(), nf.NullLit(position())));
            JL5MethodDecl valuesMeth = ((JL5NodeFactory)nf).JL5MethodDecl(position(), vmFlags, nf.CanonicalTypeNode(position(), ts.arrayOf(this.type())), "values", Collections.EMPTY_LIST, Collections.EMPTY_LIST, valuesB, null);
            
            valuesMeth = valuesMeth.setCompilerGenerated(true);
            
            JL5MethodInstance mi = (JL5MethodInstance)ts.methodInstance(position(), this.type(), JL5Flags.PUBLIC.set(JL5Flags.STATIC).set(JL5Flags.FINAL), ts.arrayOf(this.type()), "values", Collections.EMPTY_LIST, Collections.EMPTY_LIST);
            
            mi = mi.setCompilerGenerated(true);
            this.type.addMethod(mi);
            valuesMeth = (JL5MethodDecl)valuesMeth.methodInstance(mi);
            newBody = (JL5ClassBody)newBody.addMember(valuesMeth);

            // add valueOf method
            ArrayList formals = new ArrayList();
            FlagAnnotations fl = new FlagAnnotations();
            fl.classicFlags(JL5Flags.NONE);
            fl.annotations(new ArrayList());
            JL5Formal f1 = ((JL5NodeFactory)nf).JL5Formal(position(), fl, nf.CanonicalTypeNode(position(), ts.String()), "arg1");
            f1 = (JL5Formal)f1.localInstance(ts.localInstance(position(), JL5Flags.NONE, ts.String(), "arg1"));
            formals.add(f1);
            
            FlagAnnotations voFlags = new FlagAnnotations();
            voFlags.classicFlags(Flags.PUBLIC.set(Flags.STATIC));
            
            Block valueOfB = nf.Block(position());
            valueOfB = valueOfB.append(nf.Return(position(), nf.NullLit(position())));
            
            JL5MethodDecl valueOfMeth = ((JL5NodeFactory)nf).JL5MethodDecl(position(), voFlags, nf.CanonicalTypeNode(position(), this.type()), "valueOf", formals, Collections.EMPTY_LIST, valueOfB, null);
            
            valueOfMeth = valueOfMeth.setCompilerGenerated(true);
            
            ArrayList formalTypes = new ArrayList();
            formalTypes.add(ts.String());
            
            JL5MethodInstance mi2 = (JL5MethodInstance)ts.methodInstance(position(), this.type(), JL5Flags.PUBLIC.set(JL5Flags.STATIC), this.type(), "valueOf", formalTypes, Collections.EMPTY_LIST);
            
            mi2 = mi2.setCompilerGenerated(true);
            this.type.addMethod(mi2);
            valueOfMeth = (JL5MethodDecl)valueOfMeth.methodInstance(mi2);
            newBody = (JL5ClassBody)newBody.addMember(valueOfMeth);

            return body(newBody);
        }
        return this;
    }
    
    protected Node addDefaultConstructor(TypeSystem ts, NodeFactory nf) {
        ConstructorInstance ci = ts.defaultConstructor(position(), this.type);
        this.type.addConstructor(ci);
        Block block = null;
        if (this.type.superType() instanceof ClassType && !JL5Flags.isEnumModifier(flags())) {
            ConstructorInstance sci = ts.defaultConstructor(position(),
                                                (ClassType) this.type.superType());
            ConstructorCall cc = nf.SuperCall(position(), 
                                              Collections.EMPTY_LIST);
            cc = cc.constructorInstance(sci);
            block = nf.Block(position(), cc);
        }
        else {
            block = nf.Block(position());
        }
        
        ConstructorDecl cd;
        FlagAnnotations fl = new FlagAnnotations();
        fl.annotations(new ArrayList());
        if (!JL5Flags.isEnumModifier(flags())){
            fl.classicFlags(Flags.PUBLIC);
            cd = ((JL5NodeFactory)nf).JL5ConstructorDecl(position(), fl,
                                                name, Collections.EMPTY_LIST,
                                                Collections.EMPTY_LIST,
                                                block, new ArrayList());
        }
        else {
            fl.classicFlags(Flags.PRIVATE);
            /*ArrayList formalTypes = new ArrayList();
            FlagAnnotations fa = new FlagAnnotations();
            fa.classicFlags(Flags.NONE);
            fa.annotations(new ArrayList());
            formalTypes.add(((JL5NodeFactory)nf).JL5Formal(position(), fa, nf.CanonicalTypeNode(position(), ts.String()), "arg0"));
            formalTypes.add(((JL5NodeFactory)nf).JL5Formal(position(), fa, nf.CanonicalTypeNode(position(), ts.Int()), "arg1"));*/
            cd = ((JL5NodeFactory)nf).JL5ConstructorDecl(position(), fl,
                                                name, Collections.EMPTY_LIST,
                                                Collections.EMPTY_LIST,
                                                block, new ArrayList());
        }
        cd = (ConstructorDecl) cd.constructorInstance(ci);
        return body(body.addMember(cd));
    }

    /*protected boolean defaultConstructorNeeded(){
        if (JL5Flags.isEnumModifier(flags())) return false;
        return super.defaultConstructorNeeded();
    }*/

    public void prettyPrintModifiers(CodeWriter w, PrettyPrinter tr){
        for (Iterator it = annotations.iterator(); it.hasNext(); ){
            print((AnnotationElem)it.next(), w, tr);
        }
        if (flags.isInterface()) {
            if (JL5Flags.isAnnotationModifier(flags)){
                w.write(JL5Flags.clearAnnotationModifier(flags).clearInterface().clearAbstract().translate());
                w.write("@");
            }
            else{
                w.write(flags.clearInterface().clearAbstract().translate());
            }
        }
        else {
            w.write(flags.translate());
        }

        if (flags.isInterface()) {
            w.write("interface ");
        }
        else if (JL5Flags.isEnumModifier(flags)){
        }
        else {
            w.write("class ");
        }
    }
    
    
    public void prettyPrintName(CodeWriter w, PrettyPrinter tr) {
        w.write(name);
    }
    
    public void prettyPrintHeaderRest(CodeWriter w, PrettyPrinter tr) {
        if (superClass() != null && !JL5Flags.isEnumModifier(type.flags())) {
            w.write(" extends ");
            print(superClass(), w, tr);
        }

        if (! interfaces.isEmpty() && !JL5Flags.isAnnotationModifier(type.flags())) {
            if (flags.isInterface()) {
                w.write(" extends ");
            }
            else {
                w.write(" implements ");
            }

            for (Iterator i = interfaces().iterator(); i.hasNext(); ) {
                TypeNode tn = (TypeNode) i.next();
                print(tn, w, tr);

                if (i.hasNext()) {
                    w.write (", ");
                }
            }
        }

        w.write(" {");
    }
        
    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
        prettyPrintModifiers(w, tr);
        prettyPrintName(w, tr);
        if (paramTypes != null && !paramTypes.isEmpty()){
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
        prettyPrintHeaderRest(w, tr);

    }

    public Node simplify(SimplifyVisitor sv) throws SemanticException {
        runtimeAnnotations = new ArrayList();
        classAnnotations = new ArrayList();
        sourceAnnotations = new ArrayList();
        ((JL5TypeSystem)sv.typeSystem()).sortAnnotations(annotations, runtimeAnnotations, classAnnotations, sourceAnnotations);
        if (JL5Flags.isEnumModifier(flags())){
            return simplifyEnumType(sv);
        }
        return this;
    }

    public Node simplifyEnumType(SimplifyVisitor sv) throws SemanticException{
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();
       
        int count = 0;
      
        
        // create $VALUES field
        FlagAnnotations fl = new FlagAnnotations();
        fl.classicFlags(Flags.PRIVATE.Static().Final());
        fl.annotations(new ArrayList());
        JL5FieldDecl fd = nf.JL5FieldDecl(position(), fl, nf.CanonicalTypeNode(position(), ts.arrayOf(this.type())), "$VALUES", null);
        fd = fd.setCompilerGenerated(true);
        FieldInstance fi = ts.fieldInstance(position(), this.type(), fl.classicFlags(), ts.arrayOf(this.type()), "$VALUES");
        fd = (JL5FieldDecl)fd.fieldInstance(fi);
        fd = (JL5FieldDecl)fd.simplify(sv);
        ArrayList valuesInit = new ArrayList();
        //body(body().addMember(fd));
        
        // fill in gen methods
        ArrayList newMembers = new ArrayList();
        ArrayList enumConstants = new ArrayList();
        for (Iterator it = body().members().iterator(); it.hasNext(); ){
            ClassMember next = (ClassMember)it.next();
            if (next instanceof MethodDecl && ((MethodDecl)next).name().equals("values")){
                Block block = nf.Block(position());
                JL5Field field = nf.JL5Field(position(), nf.CanonicalTypeNode(position(), this.type()), "$VALUES");
                field = (JL5Field)field.type(ts.arrayOf(this.type()));
                field = (JL5Field)field.fieldInstance(fi);
                JL5Call call = (JL5Call)nf.JL5Call(position(), field, "clone", Collections.EMPTY_LIST, Collections.EMPTY_LIST);
                call = (JL5Call)call.methodInstance(ts.findMethod(ts.Object(), "clone", Collections.EMPTY_LIST, this.type()));
                call = (JL5Call)call.type(ts.Object());
                Cast cast = nf.Cast(position(), nf.CanonicalTypeNode(position(), ts.arrayOf(this.type())), call);
                cast = (Cast)cast.type(ts.arrayOf(this.type));
                Return ret = nf.Return(position(), cast);
                block = block.append(ret);
                MethodDecl md = (MethodDecl)((MethodDecl)next).body(block);
                newMembers.add(md);
            }
            else if (next instanceof MethodDecl && ((MethodDecl)next).name().equals("valueOf")){
                Block block = nf.Block(position());
                ArrayList args = new ArrayList();
                ArrayList argTypes = new ArrayList();
                ClassLit cl = nf.ClassLit(position(), nf.CanonicalTypeNode(position(), this.type()));
                cl = (ClassLit)cl.type(ts.Class());
                args.add(cl);
                argTypes.add(ts.Class());
                Formal formal = (Formal)((MethodDecl)next).formals().get(0);
                Local local = nf.Local(formal.position(), formal.name());
                local = local.localInstance(formal.localInstance());
                local = (Local)local.type(local.localInstance().type());
                args.add(local);
                argTypes.add(ts.String());
                JL5Call call = (JL5Call)nf.JL5Call(position(), nf.CanonicalTypeNode(position(), ts.Enum()), "valueOf", args, Collections.EMPTY_LIST);
                MethodInstance genMi = ts.findMethod(ts.Enum(), "valueOf", argTypes, this.type());
                genMi = ts.methodInstance(genMi.position(), genMi.container(), genMi.flags(), this.type(), genMi.name(), genMi.formalTypes(), genMi.throwTypes());
                call = (JL5Call)call.methodInstance(genMi);
                call = (JL5Call)call.type(this.type());
                Cast cast = nf.Cast(position(), nf.CanonicalTypeNode(position(), this.type()), call);
                cast = (Cast)cast.type(this.type());
                Return ret = nf.Return(position(), cast);
                block = block.append(ret);
                newMembers.add(((MethodDecl)next).body(block));
            }
            // fix up constructor
            else if (next instanceof JL5ConstructorDecl){
                JL5ConstructorDecl old = (JL5ConstructorDecl)next;
                List formals = new ArrayList();
                formals.addAll(old.formals());
                FlagAnnotations fa = new FlagAnnotations();
                fa.classicFlags(Flags.NONE);
                fa.annotations(new ArrayList());
                Formal arg1 = nf.JL5Formal(position(), fa, nf.CanonicalTypeNode(position(), ts.String()), "arg1");
                arg1 = arg1.localInstance(ts.localInstance(position(), fa.classicFlags(), ts.String(), "arg1"));
                Formal arg2 = nf.JL5Formal(position(), fa, nf.CanonicalTypeNode(position(), ts.Int()), "arg2");
                arg2 = arg2.localInstance(ts.localInstance(position(), fa.classicFlags(), ts.Int(), "arg2"));
                formals.add(0, arg2);
                formals.add(0, arg1);
                FlagAnnotations fo = new FlagAnnotations();
                fo.classicFlags(old.flags());
                fo.annotations(old.annotations());

                // add super call
                ArrayList superList = new ArrayList();
                Local l1 = nf.Local(arg1.position(), arg1.name());
                l1 = l1.localInstance(arg1.localInstance());
                l1 = (Local)l1.type(l1.localInstance().type());
                superList.add(l1);
                Local l2 = nf.Local(arg2.position(), arg2.name());
                l2 = l2.localInstance(arg2.localInstance());
                l2 = (Local)l2.type(l2.localInstance().type());
                superList.add(l2);
                
                ConstructorCall cc = nf.SuperCall(position(), superList);
                ArrayList argTypes = new ArrayList();
                argTypes.add(ts.String());
                argTypes.add(ts.Int());
                cc = cc.constructorInstance(ts.findConstructor(ts.Enum(), argTypes, this.type()));

                Block newBody = old.body().prepend(cc);
                
                JL5ConstructorDecl cd = nf.JL5ConstructorDecl(old.position(), fo, old.name(), formals, old.throwTypes(), newBody, old.paramTypes());
                cd = cd.setCompilerGenerated(true);

                
                List formalTypes = new ArrayList();
                formalTypes.addAll(old.constructorInstance().formalTypes());
                formalTypes.add(0, ts.Int());
                formalTypes.add(0, ts.String());
                JL5ConstructorInstance ci = (JL5ConstructorInstance)ts.constructorInstance(old.constructorInstance().position(), this.type(), old.constructorInstance().flags(), formalTypes, old.constructorInstance().throwTypes());
                this.type.addConstructor(ci);
                
                cd = (JL5ConstructorDecl)cd.constructorInstance(ci);
                cd = (JL5ConstructorDecl)cd.simplify(sv);
                newMembers.add(cd);
            }
            else if (next instanceof EnumConstantDecl){
                enumConstants.add(next);
            }
            else {
                newMembers.add(next);
            }
        }
       
        for (Iterator it = enumConstants.iterator(); it.hasNext(); ){
            ClassMember next = (ClassMember)it.next();
            if (next instanceof EnumConstantDecl){    
                EnumConstantDecl ecd = (EnumConstantDecl)next;
                FlagAnnotations fn = new FlagAnnotations();
                fn.classicFlags(JL5Flags.setEnumModifier(JL5Flags.NONE).Public().Static().Final());
                fn.annotations(ecd.annotations());

                ArrayList initArgs = new ArrayList();
                ArrayList initArgTypes = new ArrayList();
                
                Expr arg0 = nf.StringLit(position(), ecd.name());
                arg0 = arg0.type(ts.String());
                Expr arg1 = nf.IntLit(position(), IntLit.INT, count++);
                arg1 = arg1.type(ts.Int());
                initArgs.add(arg0);
                initArgTypes.add(ts.String());
                initArgs.add(arg1);
                initArgTypes.add(ts.Int());
                for (Iterator at = ecd.args().iterator(); at.hasNext(); ){
                    Expr n = (Expr)at.next();
                    initArgTypes.add(n.type());
                    initArgs.add(n);
                }
                //initArgs.addAll(ecd.args());
                JL5New initNew = (JL5New)nf.JL5New(position(), nf.CanonicalTypeNode(position(), this.type()), initArgs, ecd.body(), null);
                initNew = (JL5New)initNew.anonType(ecd.anonType());
                initNew = (JL5New)initNew.type(ecd.anonType());
                initNew = (JL5New)initNew.constructorInstance(ts.findConstructor(this.type(), initArgTypes, this.type()));
                JL5FieldDecl newField = nf.JL5FieldDecl(ecd.position(), fn, nf.CanonicalTypeNode(position(), this.type()), ecd.name(), initNew);
                FieldInstance newFi = ts.fieldInstance(position(), this.type(), fn.classicFlags(), this.type(), ecd.name());
                newField = (JL5FieldDecl)newField.fieldInstance(newFi);
                newField = (JL5FieldDecl)newField.simplify(sv);
                newMembers.add(newField);

                // create field to init $VALUES array
                JL5Field field = nf.JL5Field(position(), nf.CanonicalTypeNode(position(), this.type()), ecd.name());
                field = (JL5Field)field.type(this.type());
                field = (JL5Field)field.fieldInstance(newFi);
                valuesInit.add(field);
            }
        }
        
        fd = (JL5FieldDecl)fd.init(nf.ArrayInit(position(), valuesInit));
        newMembers.add(fd);
        
        boolean hasAbstractMem = false;
        for (Iterator it = enumConstants.iterator(); it.hasNext(); ){
            EnumConstantDecl ed = (EnumConstantDecl)it.next();
            if (ed.body() != null){
                hasAbstractMem = true;
                break;
            }
        }
        
        if (hasAbstractMem){
            return body(body.members(newMembers));
        }
        else {
            return body(body.members(newMembers)).flags(this.flags().Final());      
        }
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
