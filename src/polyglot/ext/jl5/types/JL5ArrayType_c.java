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
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.*;

public class JL5ArrayType_c extends ArrayType_c implements JL5ArrayType, SignatureType {

    protected boolean  variable;

    public JL5ArrayType_c(TypeSystem ts, Position pos, Type base){
        super(ts, pos, base);
    }
    
    public void setVariable(){
        this.variable = true;
    }

    public boolean isVariable(){
        return this.variable;
    }

    public String signature(){
        return "["+((SignatureType)base).signature()+";";
    }
}
