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
import polyglot.visit.*;
import java.util.*;

public class JL5Let_c extends Expr_c implements JL5Let {

    protected LocalDecl localDecl;
    protected Expr beta;
    
    public JL5Let_c(Position pos, LocalDecl localDecl, Expr beta){
        super(pos);
        this.localDecl = localDecl;
        this.beta = beta;
    }
    
    public LocalDecl localDecl(){
        return this.localDecl;
    }
 
    public Expr beta(){
        return this.beta;
    }

    public JL5Let beta(Expr e){
        JL5Let_c n = (JL5Let_c)copy();
        n.beta = e;
        return n;
    }
    
    public Node visitChildren(NodeVisitor v){
        LocalDecl localDecl = (LocalDecl)visitChild(this.localDecl, v);
        Expr beta = (Expr)visitChild(this.beta, v);
        return reconstruct(localDecl, beta);
    }

    protected JL5Let reconstruct(LocalDecl localDecl, Expr beta){
        if (localDecl != this.localDecl || beta != this.beta){
            JL5Let_c n = (JL5Let_c)copy();
            n.localDecl = localDecl;
            n.beta = beta;    
            return n;
        }
        return this;
    }

    public Term entry(){
        return this;
    }

    public List acceptCFG(CFGBuilder v, List succs){
        v.visitCFG(beta, this);
        return succs;
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        w.write("(");
        print(localDecl, w, tr);
        w.write(" ");
        print(beta, w, tr);
        w.write(")");
    }
}
