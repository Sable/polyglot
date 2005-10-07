package polyglot.ext.jl5.types;

import java.util.*;
import polyglot.types.*;

public interface ParameterizedType extends JL5ParsedClassType {

    List typeArguments();
    void typeArguments(List args);

}
