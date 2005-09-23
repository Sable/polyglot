package polyglot.ext.jl5.ast;

import polyglot.ast.*;

public interface ParamTypeNode extends BoundedTypeNode {

    ParamTypeNode id(String id);
    String id();
}
