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

import polyglot.util.*;
import java.util.*;
import polyglot.visit.*;
import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

public class ParamTypeNode_c extends TypeNode_c implements ParamTypeNode {
    
    protected String id;
    protected List bounds;
    
    public ParamTypeNode_c(Position pos, List bounds, String id){
        super(pos);
        this.id = id;
        this.bounds = bounds;
    }
    
    public ParamTypeNode id(String id){
        ParamTypeNode_c n = (ParamTypeNode_c) copy();
        n.id = id;
        return n;
    }
    
    public String id(){
        return this.id;
    }

    public ParamTypeNode bounds(List l){
        ParamTypeNode_c n = (ParamTypeNode_c) copy();
        n.bounds = l;
        return n;
    }

    public List bounds(){
        return bounds;
    }

    public ParamTypeNode reconstruct(List bounds){
        if (!CollectionUtil.equals(bounds, this.bounds)){
            ParamTypeNode_c n = (ParamTypeNode_c) copy();
            n.bounds = bounds;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        List bounds = visitList(this.bounds, v);
        return reconstruct(bounds);
    }
  
    
    
    public Context enterScope(Context c){
        c = ((JL5Context)c).pushTypeVariable((IntersectionType)type());
        return super.enterScope(c);
    }

    public void addDecls(Context c){
        ((JL5Context)c).addTypeVariable((IntersectionType)type());
    }
    // nothing needed for buildTypesEnter - not a code block like methods

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        // makes a new IntersectionType with a list of bounds which
        // are unknown types
        JL5TypeSystem ts = (JL5TypeSystem)tb.typeSystem();
        
        ArrayList typeList = new ArrayList(bounds.size());
        for (int i = 0; i < bounds.size(); i++){
            typeList.add(ts.unknownType(position()));
        }

        IntersectionType iType = ts.intersectionType(position(), id, typeList);

        return type(iType);
        
    }

    public NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException{
        if (ar.kind() == JL5AmbiguityRemover.TYPE_VARS){
            return ar.bypass(bounds);
        }
        else {
            return super.disambiguateEnter(ar);
        }
        
    }
    
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        // all of the children (bounds list) will have already been 
        // disambiguated and should there for be actual types
        JL5TypeSystem ts = (JL5TypeSystem)ar.typeSystem();
        
        ArrayList typeList = new ArrayList(bounds.size());
        for (Iterator it = bounds.iterator(); it.hasNext();){
            typeList.add(((TypeNode)it.next()).type());
        }

        ((IntersectionType)type()).bounds(typeList);
        return type(type());
    }
  
    public Node typeCheck(TypeChecker tc) throws SemanticException{
        //check no duplicate in extends list
        for (int i = 0; i < bounds.size(); i++){
            TypeNode ti = (TypeNode)bounds.get(i);
            for (int j = i+1; j < bounds.size(); j++){
                TypeNode tj = (TypeNode)bounds.get(j);
                if (tc.typeSystem().equals(ti.type(), tj.type())){
                    throw new SemanticException("Duplicate bound in type variable declaration", tj.position());
                }
            }
        }
        // check no prim arrays in bounds list
        for (int i = 0; i < bounds.size(); i++){
            TypeNode ti = (TypeNode)bounds.get(i);
            if (ti.type() instanceof ArrayType && ((ArrayType)ti.type()).base().isPrimitive()){
                throw new SemanticException("Unexpected type bound in type variable declaration", ti.position());
            
            }
        }

        // only first bound can be a class otherwise must be interfaces
        for (int i = 0; i < bounds.size(); i++){
            TypeNode tn = (TypeNode)bounds.get(i);
            if (i > 0 && !((ClassType)tn.type()).flags().isInterface()){
                throw new SemanticException("Interface expected here.", tn.position());
            }
        }
        
        return super.typeCheck(tc);
    }
    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        w.write(id);
        if (bounds() != null && !bounds().isEmpty()){
            w.write(" extends ");
            for (Iterator it = bounds.iterator(); it.hasNext(); ){
                TypeNode tn = (TypeNode)it.next();
                print(tn, w, tr);
                if (it.hasNext()){
                    w.write(" & ");
                }
            }
        }
    }
}
