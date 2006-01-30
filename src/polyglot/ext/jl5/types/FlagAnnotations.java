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
import polyglot.ext.jl5.ast.*;
import polyglot.util.*;

public class FlagAnnotations {

    protected Flags classicFlags;
    protected List annotations;

    public FlagAnnotations(Flags classic, List annots){
        this.classicFlags = classic;
        this.annotations = annots;
    }

    public FlagAnnotations(){
    }

    public Flags classicFlags(){
        if (classicFlags == null){
            classicFlags = Flags.NONE;
        }
        return classicFlags;
    }

    public FlagAnnotations classicFlags(Flags flags){
        if (this.classicFlags != null){
            this.classicFlags = this.classicFlags.set(flags);
        }
        else {
            this.classicFlags = flags;
        }
        return this;
    }

    public FlagAnnotations annotations(List annotations){
        this.annotations = annotations;
        return this;
    }
    
    public List annotations(){
        return annotations;
    }
    
    public FlagAnnotations addAnnotation(Object o){
        if (annotations == null){
            annotations = new TypedList(new LinkedList(), AnnotationElem.class, false);
        }
        annotations.add((AnnotationElem)o);
        return this;
    }
}
