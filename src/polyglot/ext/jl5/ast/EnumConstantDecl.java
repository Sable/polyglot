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

package polyglot.ext.jl5.ast;

import java.util.*;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;
import polyglot.visit.*;

/**
 * An immutable representation of a Java language extended <code>for</code>
 * statement.  Contains a statement to be executed and an expression
 * to be tested indicating whether to reexecute the statement.
 */
public interface EnumConstantDecl extends ClassMember
{    
    /** get args */
    List args();

    /** set args */
    EnumConstantDecl args(List args);

    /** set name */
    EnumConstantDecl name(String name);

    /** get name */
    String name();
    
    /** set body */
    EnumConstantDecl body(ClassBody body);

    /** get body */
    ClassBody body();

    ParsedClassType anonType();
    EnumConstantDecl anonType(ParsedClassType pct);

    ConstructorInstance constructorInstance();
    EnumConstantDecl constructorInstance(ConstructorInstance ci);
    
    EnumInstance enumInstance();

    EnumConstantDecl enumInstance(EnumInstance ei);

    List annotations();

    Flags flags();

}
