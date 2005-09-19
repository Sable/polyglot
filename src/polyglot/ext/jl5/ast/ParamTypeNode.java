package polyglot.ext.jl5.ast;

public interface ParamTypeNode extends BoundedTypeNode {

    ParamTypeNode id(String id);
    String id();
}
