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
import polyglot.ext.jl5.types.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import java.util.*;

public class AnnotationElem_c extends Expr_c implements AnnotationElem {

    protected TypeNode typeName;
    
    public AnnotationElem_c(Position pos, TypeNode typeName){
        super(pos);
        this.typeName = typeName;
    }

    public TypeNode typeName(){
        return typeName;
    }

    public AnnotationElem typeName(TypeNode typeName){
        if (!typeName.equals(this.typeName)){
            AnnotationElem_c n = (AnnotationElem_c) copy();
            n.typeName = typeName;
            return n;
        }
        return this;
    }

    protected AnnotationElem_c reconstruct(TypeNode typeName){
        if (!typeName.equals(this.typeName)){
            AnnotationElem_c n = (AnnotationElem_c) copy();
            n.typeName = typeName;
            return n;
        }
        return this;
    }
    
    public Node visitChildren(NodeVisitor v){
        TypeNode tn = (TypeNode)visitChild(this.typeName, v);
        return reconstruct(tn);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        // only make annotation elements out of annotation types
        if (!typeName.type().isClass() || !JL5Flags.isAnnotationModifier(((JL5ParsedClassType)typeName.type()).flags())){
            throw new SemanticException("Annotation: "+typeName+" must be an annotation type, ", position());
                    
        }
        return type(typeName.type());
    }
   
    public void translate(CodeWriter w, Translator tr){
        w.write("@");
        print(typeName, w, tr);
    }
    
    public Term entry() {
        return this;
    }
    
    public List acceptCFG(CFGBuilder v, List succs) {
        return succs;
    }

    public boolean isConstant(){
        return true;
    }

    public String toString(){
        return "Annotation Type: "+typeName();
    }
        
}
