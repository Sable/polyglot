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

import polyglot.ast.*;
import java.util.*;
import polyglot.ext.jl5.visit.*;
import polyglot.types.*;

public interface JL5ConstructorDecl extends ConstructorDecl{

    public boolean isCompilerGenerated();
    public JL5ConstructorDecl setCompilerGenerated(boolean val);

        
    public List paramTypes();
    public JL5ConstructorDecl paramTypes(List paramTypes);
    
    public List annotations();
    public List runtimeAnnotations();
    public List classAnnotations();
    public List sourceAnnotations();

    Node simplify(SimplifyVisitor sv) throws SemanticException;
}
