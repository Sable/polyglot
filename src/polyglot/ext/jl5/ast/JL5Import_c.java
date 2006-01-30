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

import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;
import polyglot.visit.*;
import java.util.*;
import polyglot.ast.*;

public class JL5Import_c extends Import_c implements JL5Import{
    
    public JL5Import_c(Position pos, Import.Kind kind, String name){
        super(pos, kind, name);
    }

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        JL5ImportTable it = (JL5ImportTable)tb.importTable();
        if (kind() == JL5Import.MEMBER){
            it.addMemberImport(name);
        }
        else if (kind() == JL5Import.ALL_MEMBERS){
            it.addStaticClassImport(name);
        }
        return this;
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
    
        // check package exists
        String pkgName = StringUtil.getFirstComponent(name);
        if (! tc.typeSystem().packageExists(pkgName)){
            throw new SemanticException("Package \"" + pkgName +
                "\" not found.", position());
        }

        // check class is exists and is accessible
        Named nt;
        if (kind() == JL5Import.MEMBER){
            nt = tc.typeSystem().forName(StringUtil.getPackageComponent(name));
        }
        else {
            nt = tc.typeSystem().forName(name);
        }
        
        if (nt instanceof Type){
            Type t = (Type) nt;
            if (t.isClass()){
                tc.typeSystem().classAccessibleFromPackage(t.toClass(), 
                    tc.context().package_());
                // if member check class contains some static member by the 
                // given name
                if (kind() == JL5Import.MEMBER){
                    String id = StringUtil.getShortNameComponent(name);
                    if (!isIdStaticMember(t.toClass(), id, tc.typeSystem())){
                        throw new SemanticException("Cannot import: "+id+" from class: "+t, position());
                    }
                }
            }
        }

        //findStaticMemberImportCollisions(tc);
        
        return this; 
    }

    private void findStaticMemberImportCollisions(TypeChecker tc) throws SemanticException {
        if (kind() == JL5Import.MEMBER){
            String id = StringUtil.getShortNameComponent(name);
            List l = ((JL5ImportTable)tc.context().importTable()).memberImports();
            for (Iterator it = l.iterator(); it.hasNext(); ){
                String next = (String)it.next();
                String nextId = StringUtil.getShortNameComponent(next);
                //if (nextId.equals(id) && !next.equals(name)){
                if (next.equals(name)){
                    throw new SemanticException("The import statement "+this+" collides with another import statement.", position());
                }
            }
        }
    }
    
    private boolean isIdStaticMember(ClassType t, String id, TypeSystem ts){
        try {
            FieldInstance fi = ts.findField(t, id);
            if (fi != null && fi.flags().isStatic()) return true;
        }
        catch(SemanticException e){}

        if (ts.hasMethodNamed(t, id)) return true;

        try {
            ClassType ct = ts.findMemberClass(t, id);
            if (ct != null && ct.flags().isStatic()) return true;
        }
        catch(SemanticException e){}

        return false;
    }

    public String toString(){
        return "import static "+name + (kind() == ALL_MEMBERS ? ".*": "");
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        w.write("import static ");
        w.write(name);

        if (kind() == ALL_MEMBERS){
            w.write(".*");
        }

        w.write(";");
        w.newline(0);
    }
}
