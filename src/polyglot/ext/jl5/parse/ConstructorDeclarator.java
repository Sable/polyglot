package polyglot.ext.jl5.parse;

import polyglot.ast.*;
import polyglot.util.*;
import java.util.*;

/**
 * Encapsulates some of the data in a constructor declaration.  Used only by the parser.
 */
public class ConstructorDeclarator {
	public Position pos;
	public String name;
	public List formals;

	public ConstructorDeclarator(Position pos, String name, List formals) {
		this.pos = pos;
		this.name = name;
		this.formals = formals;
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
}
