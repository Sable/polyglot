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
import polyglot.ext.jl.types.*;
import polyglot.util.*;

public class AnnotationElemInstance_c extends TypeObject_c implements AnnotationElemInstance {
    
    protected Flags flags;
    protected Type type;
    protected String name;
    protected ClassType container;
    protected boolean hasDefault;
        
    public AnnotationElemInstance_c(TypeSystem ts, Position pos, ClassType ct, Flags flags, Type type, String name, boolean hasDef){
        super(ts, pos);
        this.flags = flags;
        this.type = type;
        this.name = name;
        this.container = ct;
        this.hasDefault = hasDef;
    }

    public Flags flags(){
        return flags;
    }

    public Type type(){
        return type;
    }

    public String name(){
        return name;
    }

    public ReferenceType container(){
        return container;
    }

    public boolean isCanonical(){
        return true;
    }

    public boolean hasDefault(){
        return hasDefault;
    }
}
