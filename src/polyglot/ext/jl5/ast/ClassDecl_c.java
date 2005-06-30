package polyglot.ext.jl5.ast;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.*;
import polyglot.main.Report;
import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;

/**
 * A <code>ClassDecl</code> is the definition of a class, abstract class,
 * or interface. It may be a public or other top-level class, or an inner
 * named class, or an anonymous class.
 */
public class ClassDecl_c extends polyglot.ext.jl.ast.ClassDecl_c implements ClassDecl
{

    protected polyglot.ext.jl5.types.ParsedClassType type;
    
    public ClassDecl_c(Position pos, Flags flags, String name,
                       TypeNode superClass, List interfaces, ClassBody body) {
	    super(pos, flags, name, superClass, interfaces, body);
    }


    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
        if (flags.isInterface()) {
            w.write(flags.clearInterface().clearAbstract().translate());
        }
        else {
            w.write(flags.translate());
        }

        if (flags.isInterface()) {
            w.write("interface ");
        }
        else if (JL5Flags.isEnumModifier(flags)){
        }
        else {
            w.write("class ");
        }

        w.write(name);

        if (superClass() != null) {
            w.write(" extends ");
            print(superClass(), w, tr);
        }

        if (! interfaces.isEmpty()) {
            if (flags.isInterface()) {
                w.write(" extends ");
            }
            else {
                w.write(" implements ");
            }

            for (Iterator i = interfaces().iterator(); i.hasNext(); ) {
                TypeNode tn = (TypeNode) i.next();
                print(tn, w, tr);

                if (i.hasNext()) {
                    w.write (", ");
                }
            }
        }

        w.write(" {");
    }

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        polyglot.ext.jl5.types.ParsedClassType type = (polyglot.ext.jl5.types.ParsedClassType)tb.currentClass();
        if (type != null){
            return type(type).flags(type.flags());
        }
        return this;
    }

    public Context enterScope(Node child, Context c){
        if (child == this.body) {
            TypeSystem ts = c.typeSystem();
            c = c.pushClass(type, ts.staticTarget(type).toClass());
        }
        return super.enterScope(child, c);
    }
}
