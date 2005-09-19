package polyglot.ext.jl5.ast;

import polyglot.util.*;
import java.util.*;
import polyglot.visit.*;

public class ParamTypeNode_c extends BoundedTypeNode_c implements ParamTypeNode {
    protected String id;

    public ParamTypeNode_c(Position pos, BoundedTypeNode.Kind kind, List boundsList, String id){
        super(pos, kind, boundsList);
        this.id = id;
    }
    
    public ParamTypeNode id(String id){
        ParamTypeNode_c n = (ParamTypeNode_c) copy();
        n.id = id;
        return n;
    }
    
    public String id(){
        return this.id;
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        w.write(id);
        if (boundsList() != null && !boundsList().isEmpty()){
            w.write(" extends ");
            prettyPrintBoundsList(w, tr);
        }
    }
}
