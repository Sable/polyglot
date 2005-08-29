package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5LocalDecl extends LocalDecl {

    List annotations();
    JL5LocalDecl annotations(List annotations);
}
