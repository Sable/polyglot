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

import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.ast.*;
import java.util.*;
import polyglot.visit.*;

public class JL5AbstractBlock_c extends AbstractBlock_c implements JL5Block {
    public JL5AbstractBlock_c(Position pos, List statements){
        super(pos, statements);
        this.statements = TypedList.copyAndCheck(statements, Stmt.class, false);
    }

    public Block statements(List statements){
        JL5AbstractBlock_c n = (JL5AbstractBlock_c)copy();
        n.statements = TypedList.copyAndCheck(statements, Stmt.class, false);
        return n;
    }

    public AbstractBlock_c reconstruct(List statements){
        if (!CollectionUtil.equals(this.statements, statements)){
            JL5AbstractBlock_c n = (JL5AbstractBlock_c)copy();
            n.statements = TypedList.copyAndCheck(statements, Stmt.class, false);
            return n;
        }
        return this;
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("{");
        w.allowBreak(4," "); 
        super.prettyPrint(w, tr);
        w.allowBreak(0, " ");
        w.write("}");
    }
    
}
