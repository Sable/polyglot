package polyglot.ext.jl5.ast;

import polyglot.ast.*;

public interface JL5Import extends Import {

    public static final Kind MEMBER = new Kind("member");
    public static final Kind ALL_MEMBERS = new Kind("all-members");

}
