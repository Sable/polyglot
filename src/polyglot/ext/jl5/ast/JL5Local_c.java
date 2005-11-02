package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;

public class JL5Local_c extends Local_c implements JL5Local{

    public JL5Local_c(Position pos, String name){
        super(pos, name);
    }

}
