package polyglot.ext.jl5.visit;

import polyglot.visit.*;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.frontend.*;
import polyglot.util.*;
import polyglot.ext.jl5.ast.*;

public class LeftoverAmbiguityRemover extends AmbiguityRemover {

    public static class EnumSwitchKind extends Kind {
        protected EnumSwitchKind(String name){
            super(name);
        }
    }

    public static final Kind SWITCH_CASES = new EnumSwitchKind("disamb-switch-cases");

    public LeftoverAmbiguityRemover(Job job, TypeSystem ts, NodeFactory nf, Kind kind){
        super(job, ts, nf, kind);
    }

    public NodeVisitor enter(Node parent, Node n){
        //System.out.println("running leftover ambiguity remover"); 
        try {
            if (!(n instanceof JL5Case) || !(((JL5Case)n).expr() instanceof JL5AmbExpr)) return this;
            //System.out.println("have some JL5AmbExpr nodes"); 
            Type t = null;
            Switch sw = (Switch)parent;
            Expr sw_expr = sw.expr();
            if (sw_expr instanceof Local){
                t = ((Local)sw_expr).localInstance().type();
            }
            else if (sw_expr instanceof Field){
                t = ((Field)sw_expr).fieldInstance().type();
            }
            if (t != null){
                //System.out.println("found parent type: "+t); 
                Node newNode = nf.disamb().disambiguate((JL5AmbExpr)((JL5Case)n).expr(), this, n.position(), nf.CanonicalTypeNode(n.position(), t), ((JL5AmbExpr)((JL5Case)n).expr()).name());
                //System.out.println("newNode: "+newNode+" newNode class: "+newNode.getClass());
                ((JL5Case)n).expr((Expr)newNode);
                
            }
            return this;
            // do something here about things that are actually errors
        }
        catch (SemanticException e){
            Position position = e.position();
            if (position == null){
                position = n.position();
            }
            if (e.getMessage() != null){
                errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, e.getMessage(), position);
            }
            return this;
        }
    }
}
