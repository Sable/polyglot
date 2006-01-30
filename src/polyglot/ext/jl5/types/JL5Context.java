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
public interface JL5Context extends Context {

    public VarInstance findVariableInThisScope(String name);
    public VarInstance findVariableSilent(String name);

    public JL5Context pushTypeVariable(IntersectionType iType);
    public IntersectionType findTypeVariableInThisScope(String name);

    public boolean inTypeVariable();


    public JL5Context addTypeVariable(IntersectionType type);

    public MethodInstance findGenericMethod(String name, List argTypes, List inferredTypes) throws SemanticException;
}
