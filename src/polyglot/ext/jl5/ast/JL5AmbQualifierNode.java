package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5AmbQualifierNode extends AmbQualifierNode {

    List typeArguments();
    JL5AmbQualifierNode typeArguments(List args);
}
