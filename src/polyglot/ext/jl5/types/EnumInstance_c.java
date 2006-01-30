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

import polyglot.ext.jl.types.*;
import polyglot.types.*;
import polyglot.util.*;

public class EnumInstance_c extends FieldInstance_c implements EnumInstance {

    /*protected ReferenceType container;

    protected EnumInstance_c(){
    }*/

    protected ParsedClassType anonType;

    public EnumInstance_c(TypeSystem ts, Position pos, ReferenceType container,  Flags f, String name, ParsedClassType anonType){
        super(ts, pos, container, f.set(JL5Flags.STATIC), container, name);
        this.anonType = anonType;
    }
    
    public ParsedClassType anonType(){
        return anonType;
    }
   
    /*public ReferenceType container(){
        return container;
    }
    
    public EnumInstance flags(Flags flags);
        if (!flags.equals(this.flags)){
            EnumInstance_c n = (EnumInstance_c) copy();
            n.flags = flags;
            return n;
        }
        return this;
    }

    public EnumInstance name(String name);
        if ((name != null && !name.equals(this.name)) || (name == null && name != this.name)){
            EnumInstance_c n = (EnumInstance_c) copy();
            n.name = name;
            return n;
        }
        return this;
    }

    EnumInstance type(Type type);
        if (this.container != container){
            EnumInstance_c n = (EnumInstance_c) copy();
            n.container = container;
            return n;
        }
        return this;
    }

    public EnumInstance container(ReferenceType container){
        if (this.container != container){
            EnumInstance_c n = (EnumInstance_c) copy();
            n.container = container;
            return n;
        }
        return this;
    }*/

}

