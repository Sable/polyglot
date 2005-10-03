package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface ParamTypeNode extends TypeNode {

    ParamTypeNode id(String id);
    String id();

    List bounds();
    ParamTypeNode bounds(List l);
}
