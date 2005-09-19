package polyglot.ext.jl5.ast;

import polyglot.util.Enum;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import java.util.*;
import polyglot.visit.*;
import polyglot.ast.*;

public class BoundedTypeNode_c extends TypeNode_c implements BoundedTypeNode {


    protected BoundedTypeNode.Kind kind;
    protected List boundsList;

    public BoundedTypeNode_c(Position pos, BoundedTypeNode.Kind kind, List boundsList){
        super(pos);
        this.kind = kind;
        this.boundsList = boundsList;
    }
    
    public Kind kind(){
        return kind;        
    }

    public BoundedTypeNode kind(Kind kind){
        BoundedTypeNode_c n = (BoundedTypeNode_c) copy();
        n.kind = kind;
        return n;
    }
    
    public List boundsList(){
        return this.boundsList;
    }
    
    public BoundedTypeNode boundsList(List list){
        BoundedTypeNode_c n = (BoundedTypeNode_c) copy();
        n.boundsList = list;
        return n;
    }
   
    public BoundedTypeNode reconstruct(List bounds){
        if (!CollectionUtil.equals(bounds, boundsList)){
            BoundedTypeNode_c n = (BoundedTypeNode_c) copy();
            n.boundsList = bounds;
            return n;
        }
        return this;
    }
    
    public Node visitChildren(NodeVisitor v){
        List bounds = visitList(boundsList, v);
        return reconstruct(bounds);
    }
    
    public void prettyPrintBoundsList(CodeWriter w, PrettyPrinter tr){
        if (boundsList == null) return;
        for (Iterator it = boundsList.iterator(); it.hasNext(); ){
            TypeNode tn = (TypeNode)it.next();
            print(tn, w, tr);
            if (it.hasNext()){
                w.write(" & ");
            }
        }
    }
    
    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
    }
}
