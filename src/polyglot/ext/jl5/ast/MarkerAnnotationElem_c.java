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
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.ast.*;
import polyglot.types.*;

public class MarkerAnnotationElem_c extends NormalAnnotationElem_c implements MarkerAnnotationElem {

    public MarkerAnnotationElem_c(Position pos, TypeNode typeName){
        super(pos, typeName, new TypedList(new LinkedList(), ElementValuePair.class, false));
    }

    public Node visitChildren(NodeVisitor v){
        return super.visitChildren(v);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        return super.typeCheck(tc);
    }
    
    public void translate(CodeWriter w, Translator tr){
        super.translate(w, tr);
    }
}
