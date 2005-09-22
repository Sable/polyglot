package polyglot.ext.jl5.parse;

import polyglot.ast.*;
import polyglot.util.*;
import java.util.*;

/**
 * Encapsulates some of the data in a method declaration.  Used only by the parser.
 */
public class MethodDeclarator {
	public Position pos;
	public String name;
	public List formals;
    public Integer dims = new Integer(0);    

	public MethodDeclarator(Position pos, String name, List formals) {
		this.pos = pos;
		this.name = name;
		this.formals = formals;
	}
	
	public MethodDeclarator(Position pos, String name, List formals, Integer dims) {
        this(pos, name, formals);
        this.dims = dims;
	}
	
	public Position position() {
		return pos;
	}

    public String name(){
        return name;
    }

    public List formals(){
        return formals;
    }

    public Integer dims(){
        return dims;
    }
}
