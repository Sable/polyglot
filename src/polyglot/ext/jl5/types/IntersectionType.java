package polyglot.ext.jl5.types;

import polyglot.types.*;
import java.util.*;

public interface IntersectionType extends ClassType {

    public static final Kind INTERSECTION = new Kind("intersection");

    List bounds();
    void bounds(List l);
    void addBound(ClassType bound);

    void name(String name);
}
