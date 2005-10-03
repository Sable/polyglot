package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5ConstructorCall extends ConstructorCall {

    List typeArguments();
    JL5ConstructorCall typeArguments(List args);
}
