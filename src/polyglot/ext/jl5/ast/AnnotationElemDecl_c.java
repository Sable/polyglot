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
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import java.util.*;
import polyglot.ext.jl5.types.*;

public class AnnotationElemDecl_c extends Term_c implements AnnotationElemDecl {

    protected TypeNode type;
    protected Flags flags;
    protected Expr defaultVal;
    protected String name;
    protected AnnotationElemInstance ai;
    
    public AnnotationElemDecl_c(Position pos, FlagAnnotations flags, TypeNode type, String name, Expr defaultVal){
        super(pos);
        this.type = type;
        this.flags = flags.classicFlags();
        this.defaultVal = defaultVal;
        this.name = name;
    }
    
    public AnnotationElemDecl type(TypeNode type){
        if (!type.equals(this.type)){ 
            AnnotationElemDecl_c n = (AnnotationElemDecl_c) copy();
            n.type = type;
            return n;
        }
        return this;
    }
    
    public TypeNode type(){
        return type;
    }
    
    public AnnotationElemDecl flags(Flags flags){
        if (!flags.equals(this.flags)){
            AnnotationElemDecl_c n = (AnnotationElemDecl_c) copy();
            n.flags = flags;
            return n;
        }
        return this;
    }
    
    public Flags flags(){
        return flags;
    }

    public AnnotationElemDecl defaultVal(Expr def){
        if (!def.equals(this.defaultVal)){
            AnnotationElemDecl_c n = (AnnotationElemDecl_c) copy();
            n.defaultVal = def;
            return n;
        }
        return this;
    }
    
    public Expr defaultVal(){
        return defaultVal;
    }

    public AnnotationElemDecl name(String name){
        if (!name.equals(this.name)){
            AnnotationElemDecl_c n = (AnnotationElemDecl_c) copy();
            n.name = name;
            return n;
        }
        return this;
    }
    
    public String name(){
        return name;
    }

    public AnnotationElemDecl annotationElemInstance(AnnotationElemInstance ai){
        AnnotationElemDecl_c n = (AnnotationElemDecl_c) copy();
        n.ai = ai;
        return n;
    }

    public AnnotationElemInstance annotationElemInstance(){
        return ai;
    }
    
    protected AnnotationElemDecl_c reconstruct(TypeNode type, Expr defaultVal) {
        if (!type.equals(this.type) || this.defaultVal != defaultVal){
            AnnotationElemDecl_c n = (AnnotationElemDecl_c) copy();
            n.type = type;
            n.defaultVal = defaultVal;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        TypeNode type = (TypeNode) visitChild( this.type, v);
        Expr defVal = (Expr)visitChild(this.defaultVal, v);
        return reconstruct(type, defVal);
    }

    public NodeVisitor addMembersEnter(AddMemberVisitor am) {
        JL5ParsedClassType ct = (JL5ParsedClassType)am.context().currentClassScope();

        ct.addAnnotationElem(this.ai);
        return am.bypassChildren(this);
    }

    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        // this may not be neccessary - I think this is for scopes for
        // symbol checking? - in fields and meths there many anon inner 
        // classes and thus a scope is needed - but in annots there 
        // cannot be ???
        return tb.pushCode();
    }

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tb.typeSystem();

        AnnotationElemDecl n = this;
        // why send Object as container ??
        AnnotationElemInstance ai = ts.annotationElemInstance(n.position(), ts.Object(), JL5Flags.NONE, ts.unknownType(position()), n.name(), defaultVal == null ? false: true);

        return n.annotationElemInstance(ai);
    }

    public NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException {
        if (ar.kind() == AmbiguityRemover.SUPER){
            return ar.bypassChildren(this);
        }
        else if (ar.kind() == AmbiguityRemover.SIGNATURES){
            if (defaultVal != null){
                return ar.bypass(defaultVal);
            }
        }
        return ar;
    }

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        if (ar.kind() == AmbiguityRemover.SIGNATURES){
            Context c = ar.context();
            JL5TypeSystem ts = (JL5TypeSystem)ar.typeSystem();

            JL5ParsedClassType ct = (JL5ParsedClassType)c.currentClassScope();

            Flags f = flags;

            f = f.Public().Abstract();

            AnnotationElemInstance ai = ts.annotationElemInstance(position(), ct, f, type().type(), name(), defaultVal == null ? false : true);
            return flags(f).annotationElemInstance(ai);
        }

        return this;
    }
    
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        
        // check type - must be one of primitive, String, Class, 
        // enum, annotation or array or one of these
        if (!ts.isValidAnnotationValueType(type().type())){
            throw new SemanticException("The type: "+this.type()+" for the annotation element declaration "+this.name()+" must be a primitive, String, Class, enum type, annotation type or an array of one of these.", type().position());
        }
  
        // an annotation element cannot have the same type as the 
        // type it is declared in - direct
        // also need to check indirect cycles
        if (type().type().equals(tc.context().currentClass())){
            throw new SemanticException("Cyclic annotation element type: "+type(), type().position());
        }

        // check default value matches type
        if (defaultVal != null){
            if (defaultVal instanceof ArrayInit){
                ((ArrayInit)defaultVal).typeCheckElements(type.type());
            }
            else {
                boolean intConversion = false;
                if (! ts.isImplicitCastValid(defaultVal.type(), type.type()) &&
                    ! ts.equals(defaultVal.type(), type.type()) &&
                    ! ts.numericConversionValid(type.type(), defaultVal.constantValue()) &&
                    ! ts.isBaseCastValid(defaultVal.type(), type.type()) &&
                    ! ts.numericConversionBaseValid(type.type(), defaultVal.constantValue())){
                    throw new SemanticException("The type of the default value: "+defaultVal+" does not match the annotation element type: "+type.type()+" .", defaultVal.position());
                }
            }
        }

        if (flags.contains(Flags.NATIVE) ){
            throw new SemanticException("Modifier native is not allowed here", position());
        }
        if (flags.contains(Flags.PRIVATE) ){
            throw new SemanticException("Modifier private is not allowed here", position());
        }

        if (defaultVal != null) ts.checkValueConstant(defaultVal);
        return this;
    }
    
    
    public List acceptCFG(CFGBuilder v, List succs){
        if (defaultVal != null) {
            v.visitCFG(defaultVal, this);
        }
        return succs;
    }

    public Term entry() {
        return defaultVal != null ? defaultVal.entry() : this;
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.begin(0);
        
        Flags f = flags();
        f = f.clearPublic();
        f = f.clearAbstract();
        
        w.write(f.translate());
        print(type, w, tr);
        w.write(" "+name+"( )");
        if (defaultVal != null){
            w.write(" default ");
            print(defaultVal, w, tr);
        }
        w.write(";");
        w.end();
    }
}
