package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface GenericNew extends JL5New {

    List typeArguments();
    GenericNew typeArguments(List args);
    
}
