package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface GenericCall extends Call {

    List typeArguments();
    GenericCall typeArguments(List args);
}
