/* Copyright (C) 2006 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

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
