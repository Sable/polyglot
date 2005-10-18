package polyglot.ext.jl5.types;

import polyglot.types.*;
import java.util.*;
import polyglot.ast.*;

public interface IntersectionType extends ClassType {

    public static final Kind INTERSECTION = new Kind("intersection");

    List bounds();
    void bounds(List l);
    void addBound(ClassType bound);

    void name(String name);

    /*void pushRestriction(TypeNode restriction);
    void popRestriction(TypeNode restriction);
    List restrictions();
    TypeNode restriction();*/
}
