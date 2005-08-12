package polyglot.ext.jl5.types;

import polyglot.types.*;
import polyglot.util.*;

public class JL5NoMemberException extends NoMemberException {

    public static final int ENUM_CONSTANT = 4;

    public JL5NoMemberException(int kind, String s){
        super(kind, s);
    }

    public JL5NoMemberException(int kind, String s, Position pos){
        super(kind, s, pos);
    }
    
    public String getKindStr(){
        switch(getKind()){
            case ENUM_CONSTANT:
                return "enum constant";
            default:
                return super.getKindStr();
        }
    }
}
