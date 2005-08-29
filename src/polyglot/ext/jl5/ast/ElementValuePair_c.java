package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
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
        if (!this.value.isConstant() || value == null || value instanceof NullLit){
            throw new SemanticException("Annotation attribute value must be constant", value.position());
        }
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
