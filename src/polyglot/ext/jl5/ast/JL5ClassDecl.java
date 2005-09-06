package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5ClassDecl extends ClassDecl {

    public List annotations();

    public JL5ClassDecl annotations(List annotations);
}
