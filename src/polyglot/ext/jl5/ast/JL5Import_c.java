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

        System.out.println("class: "+name);
        System.out.println("class: "+StringUtil.getPackageComponent(name));
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
                    /*if (!isIdStaticMember(t.toClass(), id)){
                        throw new SemanticException("Cannot import: "+id+" from class: "+t, position());
                    }*/
                }
            }
        }
        
        return this; 
    }

    private boolean isIdStaticMember(ClassType t, String id){
        FieldInstance fi = t.fieldNamed(id);
        if (fi != null) return true;

        List list = t.methodsNamed(id);
        if (!list.isEmpty()) return true;

        ClassType ct = t.memberClassNamed(id);
        if (ct != null) return true;

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
