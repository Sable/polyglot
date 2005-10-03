package polyglot.ext.jl5.ast;

import polyglot.util.*;
import java.util.*;
import polyglot.visit.*;
import polyglot.ast.*;
import polyglot.ext.jl.ast.*;

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
