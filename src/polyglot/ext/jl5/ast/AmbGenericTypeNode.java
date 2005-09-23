package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface AmbGenericTypeNode extends AmbTypeNode {

    List typeArguments();
    AmbGenericTypeNode typeArguments(List args);
}
