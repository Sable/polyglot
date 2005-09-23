package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface GenericConstructorCall extends ConstructorCall {

    List typeArguments();
    GenericConstructorCall typeArguments(List args);
}
