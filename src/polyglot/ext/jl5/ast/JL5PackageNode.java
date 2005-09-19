package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5PackageNode extends PackageNode {

    List annotations();
    JL5PackageNode annotations(List annotations);
}

