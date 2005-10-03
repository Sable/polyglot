package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5Call extends Call {

    List typeArguments();
    JL5Call typeArguments(List args);
}
