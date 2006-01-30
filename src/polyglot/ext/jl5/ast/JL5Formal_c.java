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
import java.util.*;
import polyglot.ext.jl.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.types.*;

public class JL5Formal_c extends Formal_c implements JL5Formal, ApplicationCheck, SimplifyVisit {

    protected List annotations;
    protected List runtimeAnnotations;
    protected List classAnnotations;
    protected List sourceAnnotations;
    protected boolean variable = false;
    
    public JL5Formal_c(Position pos, FlagAnnotations flags, TypeNode type, String name){
        super(pos, flags.classicFlags(), type, name);
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, true);
        }
    }
    
    public JL5Formal_c(Position pos, FlagAnnotations flags, TypeNode type, String name, boolean variable){
        super(pos, flags.classicFlags(), type, name);
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, true);
        }
        this.variable = variable;
    }
    
    public List annotations(){
        return annotations;
    }
    
    public JL5Formal annotations(List annotations){
        JL5Formal_c n = (JL5Formal_c) copy();
        n.annotations = annotations;
        return n;
    }

    public boolean isVariable(){
        return variable;
    }
    
    protected Formal reconstruct(TypeNode type, List annotations){
        if (this.type() != type || !CollectionUtil.equals(annotations, this.annotations)){
            JL5Formal_c n = (JL5Formal_c)copy();
            n.type = type;
            n.annotations = annotations;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        TypeNode type = (TypeNode)visitChild(this.type(), v);
        List annots = visitList(this.annotations, v);
        return reconstruct(type, annots);
    }

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        if (isVariable()){
            ((JL5ArrayType)type().type()).setVariable();
        }
        return super.disambiguate(ar);
    }
    
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (!flags().clear(Flags.FINAL).equals(Flags.NONE)){
            throw new SemanticException("Modifier: "+flags().clearFinal()+" not allowed here.", position());
        }
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        ts.checkDuplicateAnnotations(annotations);
        return super.typeCheck(tc);
        
    }

    public Node applicationCheck(ApplicationChecker appCheck) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)appCheck.typeSystem();
        for( Iterator it = annotations.iterator(); it.hasNext(); ){
            AnnotationElem next = (AnnotationElem)it.next();
            ts.checkAnnotationApplicability(next, this);
        }
        return this;
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        if (annotations != null){
            for (Iterator it = annotations.iterator(); it.hasNext(); ){
                print((AnnotationElem)it.next(), w, tr);
            }
        }
        w.write(flags.translate());
        if (isVariable()){
            w.write(((ArrayType)type.type()).base().toString());
            //print(type, w, tr);
            w.write(" ...");
        }
        else {
            print(type, w, tr);
        }
        w.write(" ");
        w.write(name);
        
    }
    
    public Node simplify(SimplifyVisitor sv) throws SemanticException {
        runtimeAnnotations = new ArrayList();
        classAnnotations = new ArrayList();
        sourceAnnotations = new ArrayList();
        ((JL5TypeSystem)sv.typeSystem()).sortAnnotations(annotations, runtimeAnnotations, classAnnotations, sourceAnnotations);
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
