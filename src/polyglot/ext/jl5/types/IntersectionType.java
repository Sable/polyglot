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
import polyglot.ast.*;

public interface IntersectionType extends ClassType {

    public static final Kind INTERSECTION = new Kind("intersection");

    List bounds();
    void bounds(List l);
    void addBound(ClassType bound);

    void name(String name);

    boolean isEquivalent(TypeObject arg2);
    /*void pushRestriction(TypeNode restriction);
    void popRestriction(TypeNode restriction);
    List restrictions();
    TypeNode restriction();*/

    boolean equivalentImpl(TypeObject arg2);

    Type erasureType();

    Type upperBound();
}
