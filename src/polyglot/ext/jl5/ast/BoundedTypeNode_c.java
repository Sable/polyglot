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

import polyglot.util.Enum;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import java.util.*;
import polyglot.visit.*;
import polyglot.ast.*;
import polyglot.ext.jl5.types.*;

public class BoundedTypeNode_c extends TypeNode_c implements BoundedTypeNode {


    protected BoundedTypeNode.Kind kind;
    protected TypeNode bound;

    public BoundedTypeNode_c(Position pos, BoundedTypeNode.Kind kind, TypeNode bound){
        super(pos);
        this.kind = kind;
        this.bound = bound;
    }
    
    public Kind kind(){
        return kind;        
    }

    public BoundedTypeNode kind(Kind kind){
        BoundedTypeNode_c n = (BoundedTypeNode_c) copy();
        n.kind = kind;
        return n;
    }
    
    public TypeNode bound(){
        return this.bound;
    }
    
    public BoundedTypeNode bound(TypeNode bound){
        BoundedTypeNode_c n = (BoundedTypeNode_c) copy();
        n.bound = bound;
        return n;
    }
   
    public BoundedTypeNode reconstruct(TypeNode bound){
        if (bound != this.bound){
            BoundedTypeNode_c n = (BoundedTypeNode_c) copy();
            n.bound = bound;
            return n;
        }
        return this;
    }
    
    public Node visitChildren(NodeVisitor v){
        if (bound != null){
            TypeNode bound = (TypeNode)visitChild(this.bound, v);
            return reconstruct(bound);
        }
        return this;
    }
   
    public Node disambiguate(AmbiguityRemover v){
        JL5TypeSystem ts = (JL5TypeSystem)v.typeSystem();
        if (bound == null ) {
            return this.type(ts.anyType());
        }
        else if (kind == BoundedTypeNode.SUPER) {
            return this.type(ts.anySuperType(bound.type()));
        }
        else if (kind == BoundedTypeNode.EXTENDS){
            return this.type(ts.anySubType(bound.type()));
        }
        return this;    
    }
    
    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        w.write("?");
        if (bound != null){
            w.write(kind.toString());
            w.write(" ");
            print(bound, w, tr);
        }
    }
}
