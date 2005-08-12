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
