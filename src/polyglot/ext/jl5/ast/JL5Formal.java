package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5Formal extends Formal {

    List annotations();
    JL5Formal annotations(List annotations);

    boolean isVariable();
}
