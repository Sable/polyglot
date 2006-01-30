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
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import java.util.*;

public class ElementValuePair_c extends Expr_c implements ElementValuePair {

    protected String name;
    protected Expr value;
    
    public ElementValuePair_c(Position pos, String name, Expr value){
        super(pos);
        this.name = name;
        this.value = value;
    }

    public String name(){
        return name;
    }

    public ElementValuePair name(String name){
        if (!name.equals(this.name)){
            ElementValuePair_c n = (ElementValuePair_c)copy();
            n.name = name;
            return n;
        }
        return this;
    }

    public Expr value(){
        return value;
    }

    public ElementValuePair value(Expr value){
        if (!value.equals(this.value)){
            ElementValuePair_c n = (ElementValuePair_c)copy();
            n.value = value;
            return n;
        }
        return this;
    }

    protected Node reconstruct(Expr value){
        if (value != this.value){
            ElementValuePair_c n = (ElementValuePair_c) copy();
            n.value = value;
            return n;
        }
        return this;
    }
    
    public Node visitChildren(NodeVisitor v){
        Expr value = (Expr) visitChild(this.value, v);
        return reconstruct(value);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException{
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        ts.checkValueConstant(value);

        return this;
    }
    
    public void translate(CodeWriter w, Translator tr){
        w.write(name+"=");
        print(value, w, tr);
    }
    
    public Term entry() {
        return this;
    }
    
    public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFG(value, this);
        return succs;
    }
        
}
