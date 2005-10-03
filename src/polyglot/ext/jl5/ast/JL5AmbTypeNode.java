package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5AmbTypeNode extends AmbTypeNode {

    List typeArguments();
    JL5AmbTypeNode typeArguments(List args);
}
