package polyglot.ext.jl5.ast;

import java.util.*;

public interface NormalAnnotationElem extends AnnotationElem {

    List elements();
    NormalAnnotationElem elements(List elements);
}
