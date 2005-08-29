package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;

public class EnumConstant_c extends Field_c implements EnumConstant{

    public EnumConstant_c(Position pos, Receiver target, String name){
        super(pos, target, name);
    }
    
    /*protected Receiver
    EnumInstance enumInstance();

    EnumConstant enumConstant(EnumInstance ei);

    Receiver target();

    EnumConstant target(Receiver target);

    boolean isTargetImplicit();

    EnumConstant targetImplicit(boolean implicit);

    String name();

    EnumConstant name(String name);*/

    public boolean isConstant(){
        return true;
    }
}
