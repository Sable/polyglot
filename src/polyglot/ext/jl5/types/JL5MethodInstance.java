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

package polyglot.ext.jl5.types;

import polyglot.types.*;
import java.util.*;

public interface JL5MethodInstance extends MethodInstance{

    public boolean isCompilerGenerated();
    public JL5MethodInstance setCompilerGenerated(boolean val);

    List typeVariables();
    void addTypeVariable(IntersectionType type);

    boolean hasTypeVariable(String name);
    IntersectionType getTypeVariable(String name);

    void typeVariables(List vars);
    boolean isGeneric();

    boolean callValidImpl(List typeArgs);
    
    boolean genericMethodCallValidImpl(String name, List argTypes, List inferredTypes);
    boolean genericCallValidImpl(List argTypes, List inferredTypes);
}
