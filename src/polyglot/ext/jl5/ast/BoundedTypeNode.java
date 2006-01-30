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

import polyglot.util.Enum;
import polyglot.ast.*;
import java.util.*;

public interface BoundedTypeNode extends TypeNode {

      public static class Kind extends Enum {
          public Kind(String name) {super(name); }
      }

      public static final Kind SUPER = new Kind("super");
      public static final Kind EXTENDS = new Kind("extends");

      Kind kind();

      BoundedTypeNode kind(Kind kind);

      TypeNode bound();

      BoundedTypeNode bound(TypeNode bound);
      
}
