package polyglot.ext.jl5.types;

import polyglot.ext.jl5.ast.*;
import java.util.*;
import polyglot.types.*;

public interface JL5ParsedClassType extends ParsedClassType{
    void addEnumConstant(EnumInstance ei);
    List enumConstants();
    EnumInstance enumConstantNamed(String name);
}
