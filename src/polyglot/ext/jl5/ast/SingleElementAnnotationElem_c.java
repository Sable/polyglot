package polyglot.ext.jl5.ast;

import java.util.*;
import polyglot.util.*;
import polyglot.ast.*;

public class SingleElementAnnotationElem_c extends NormalAnnotationElem_c implements SingleElementAnnotationElem {

    public SingleElementAnnotationElem_c(Position pos, TypeNode typeName, List elements){
        super(pos, typeName, elements);
    }

}
