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
import polyglot.util.*;

public class JL5ImportTable extends ImportTable {

    protected ArrayList memberImports;
    protected ArrayList staticClassImports;

    public JL5ImportTable(TypeSystem ts, Resolver base, polyglot.types.Package pkg, String src) {
        super(ts, base, pkg, src);
        this.memberImports = new ArrayList();
        this.staticClassImports = new ArrayList();
    }

    public JL5ImportTable(TypeSystem ts, Resolver base, polyglot.types.Package pkg) {
        this(ts, base, pkg, null);
    }

    public void addMemberImport(String member){
        memberImports.add(member);
    }

    public void addStaticClassImport(String className){
        staticClassImports.add(className);
    }

    public List memberImports(){
        return memberImports;
    }

    public List staticClassImports(){
        return staticClassImports;
    }

    public Named find(String name) throws SemanticException {

        Named result = null;
        // may be member in static import
        for (Iterator it = memberImports.iterator(); it.hasNext(); ){
            String next = (String)it.next();
            String id = StringUtil.getShortNameComponent(next);
            if (name.equals(id)){
                String className = StringUtil.getPackageComponent(next);
                Named nt = ts.forName(className);
                if (nt instanceof Type){
                    Type t = (Type)nt;
                    try {
                        result = ts.findMemberClass(t.toClass(), name);
                    }
                    catch (SemanticException e){
                    }
                    if (result != null && ((ClassType)result).flags().isStatic()) return result;
                }                                    
            }
        }

        for (Iterator it = staticClassImports.iterator(); it.hasNext(); ){
            String next = (String)it.next();
            Named nt = ts.forName(next);
            
            if (nt instanceof Type){
                Type t = (Type)nt;
                try {
                    result = ts.findMemberClass(t.toClass(), name);
                }
                catch(SemanticException e){
                }
                if (result != null && ((ClassType)result).flags().isStatic()) return result;
            }
        }

        return super.find(name);
    }
}
