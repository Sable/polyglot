package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.*;

public class JL5ArrayType_c extends ArrayType_c implements JL5ArrayType {

    protected boolean  variable;

    public JL5ArrayType_c(TypeSystem ts, Position pos, Type base){
        super(ts, pos, base);
    }
    
    public void setVariable(){
        this.variable = true;
    }

    public boolean isVariable(){
        return this.variable;
    }
}
