package polyglot.ext.jl5.parse;

import polyglot.ext.jl.parse.*;
import polyglot.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.util.*;
import polyglot.parse.*;
import polyglot.ext.jl5.ast.*;

public class JL5Name extends Name {

    public JL5NodeFactory nf;
    public JL5TypeSystem ts;
    
    public JL5Name(BaseParser parser, Position pos, String name){
        super(parser, pos, name);
        this.nf = (JL5NodeFactory)parser.nf;
        this.ts = (JL5TypeSystem)parser.ts;
    }
   
    public JL5Name(BaseParser parser, Position pos, Name prefix, String name){
        super(parser, pos, prefix, name);
        this.nf = (JL5NodeFactory)parser.nf;
        this.ts = (JL5TypeSystem)parser.ts;
    }
    
    public Expr toExpr(){
        if (prefix == null){
            return nf.AmbExpr(pos, name);
        }
        return nf.JL5Field(pos, prefix.toReceiver(), name);
    }
}
