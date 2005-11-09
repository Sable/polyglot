package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;
import polyglot.types.*;

public interface JL5ClassDecl extends ClassDecl {

    public List annotations();

    public JL5ClassDecl annotations(List annotations);

    public List runtimeAnnotations();
    public List classAnnotations();
    public List sourceAnnotations();
    Node addDefaultConstructorIfNeeded(TypeSystem ts, NodeFactory nf);
}
