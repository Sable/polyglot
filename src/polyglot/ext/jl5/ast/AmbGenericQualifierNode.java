package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface AmbGenericQualifierNode extends AmbQualifierNode {

    List typeArguments();
    AmbGenericQualifierNode typeArguments(List args);
}
