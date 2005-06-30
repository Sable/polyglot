package polyglot.ext.jl5.types;

import polyglot.ext.jl5.ast.*;
import java.util.*;

public interface ParsedClassType extends polyglot.types.ParsedClassType{
    void addEnumConstant(EnumConstant ec);
    List enumConstants();
}
