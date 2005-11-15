package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.types.*;
import polyglot.util.*;

public class EnumInstance_c extends FieldInstance_c implements EnumInstance {

    /*protected ReferenceType container;

    protected EnumInstance_c(){
    }*/

    protected ParsedClassType anonType;

    public EnumInstance_c(TypeSystem ts, Position pos, ReferenceType container,  Flags f, String name, ParsedClassType anonType){
        super(ts, pos, container, f.set(JL5Flags.STATIC), container, name);
        this.anonType = anonType;
    }
    
    public ParsedClassType anonType(){
        return anonType;
    }
   
    /*public ReferenceType container(){
        return container;
    }
    
    public EnumInstance flags(Flags flags);
        if (!flags.equals(this.flags)){
            EnumInstance_c n = (EnumInstance_c) copy();
            n.flags = flags;
            return n;
        }
        return this;
    }

    public EnumInstance name(String name);
        if ((name != null && !name.equals(this.name)) || (name == null && name != this.name)){
            EnumInstance_c n = (EnumInstance_c) copy();
            n.name = name;
            return n;
        }
        return this;
    }

    EnumInstance type(Type type);
        if (this.container != container){
            EnumInstance_c n = (EnumInstance_c) copy();
            n.container = container;
            return n;
        }
        return this;
    }

    public EnumInstance container(ReferenceType container){
        if (this.container != container){
            EnumInstance_c n = (EnumInstance_c) copy();
            n.container = container;
            return n;
        }
        return this;
    }*/

}

