package polyglot.ext.jl5.types;

import java.util.*;
import polyglot.ext.jl5.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.frontend.*;
import polyglot.ext.jl.types.*;

public class JL5ParsedClassType_c extends ParsedClassType_c implements JL5ParsedClassType{
    
    protected List enumConstants;
   
    public JL5ParsedClassType_c( TypeSystem ts, LazyClassInitializer init, Source fromSource){
        super(ts, init, fromSource);
    }
    
    public void addEnumConstant(EnumConstant ec){
        enumConstants().add(ec);
    }

    public List enumConstants(){
        if (enumConstants == null){
            enumConstants = new TypedList(new LinkedList(), EnumConstant.class, false);
            //init.initEnumConstants(this);
            freeInit();
        }
        return enumConstants;
    }

    protected boolean initialized(){
        return super.initialized() && this.enumConstants != null;
    }
}
