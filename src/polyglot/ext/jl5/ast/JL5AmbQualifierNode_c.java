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
import polyglot.visit.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;

public class JL5AmbQualifierNode_c extends AmbQualifierNode_c implements JL5AmbQualifierNode {

    protected List typeArguments;
    
    public JL5AmbQualifierNode_c(Position pos, QualifierNode qual, String name, List typeArguments){
        super(pos, qual, name);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public JL5AmbQualifierNode typeArguments(List args){
        JL5AmbQualifierNode_c n = (JL5AmbQualifierNode_c) copy();
        n.typeArguments = args;
        return n;
    }

    public Node disambiguate(AmbiguityRemover sc) throws SemanticException {
        Node n = sc.nodeFactory().disamb().disambiguate(this, sc, position(), qual, name);
        if (n instanceof CanonicalTypeNode && ((CanonicalTypeNode)n).type() instanceof JL5ParsedClassType){
            ParameterizedType pt = new ParameterizedType_c((JL5ParsedClassType)((CanonicalTypeNode)n).type());
            ArrayList typeArgs = new ArrayList(typeArguments.size());
            for (Iterator it = typeArguments.iterator(); it.hasNext(); ){
                TypeNode tn = (TypeNode)it.next();
                Type t = tn.type();
                typeArgs.add(t);
            }
            pt.typeArguments(typeArgs);

            CanonicalTypeNode an = sc.nodeFactory().CanonicalTypeNode(n.position(), pt);
            return an;
        }
        else if (n instanceof QualifierNode){
            return n;
        }
        throw new SemanticException("Could not find type or package \"" +
                (qual == null ? name : qual.toString() + "." + name) +
                "\".", position());
    }
}
