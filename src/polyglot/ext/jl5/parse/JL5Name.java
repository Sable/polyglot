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

import polyglot.ext.jl.parse.*;
import polyglot.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.util.*;
import polyglot.parse.*;
import polyglot.ext.jl5.ast.*;

public class JL5Name extends Name {

    public JL5NodeFactory nf;
    public JL5TypeSystem ts;
    
    public JL5Name(BaseParser parser, Position pos, String name){
        super(parser, pos, name);
        this.nf = (JL5NodeFactory)parser.nf;
        this.ts = (JL5TypeSystem)parser.ts;
    }
   
    public JL5Name(BaseParser parser, Position pos, Name prefix, String name){
        super(parser, pos, prefix, name);
        this.nf = (JL5NodeFactory)parser.nf;
        this.ts = (JL5TypeSystem)parser.ts;
    }
    
    public Expr toExpr(){
        if (prefix == null){
            return nf.AmbExpr(pos, name);
        }
        return nf.JL5Field(pos, prefix.toReceiver(), name);
    }

    public PackageNode toPackage(FlagAnnotations fl) {
        if (prefix == null) {
            return nf.JL5PackageNode(pos, fl, ts.createPackage(null, name));
        }
        else {
            return nf.JL5PackageNode(pos, fl, ts.createPackage(((JL5Name)prefix).toPackage(fl).package_(), name));
        }
    }
   
}
