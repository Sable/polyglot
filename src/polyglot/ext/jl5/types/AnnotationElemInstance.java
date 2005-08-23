package polyglot.ext.jl5.types;

import polyglot.types.*;

public interface AnnotationElemInstance extends MemberInstance {
    public Flags flags();

    public Type type();

    public String name();

    public ReferenceType container();

    public boolean hasDefault();
}
