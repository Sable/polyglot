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
import polyglot.ext.jl.ast.*;

/**
 * A <code>ClassDecl</code> is the definition of a class, abstract class,
 * or interface. It may be a public or other top-level class, or an inner
 * named class, or an anonymous class.
 */
public class JL5ClassDecl_c extends ClassDecl_c implements ClassDecl
{

    public JL5ClassDecl_c(Position pos, Flags flags, String name,
                       TypeNode superClass, List interfaces, ClassBody body) {
	    super(pos, flags, name, superClass, interfaces, body);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (JL5Flags.isEnumModifier(flags()) && flags().isAbstract()){
            throw new SemanticException("Enum types cannot have abstract modifier", this.position());
        }
        if (JL5Flags.isEnumModifier(flags()) && flags().isPrivate()){
            throw new SemanticException("Enum types cannot have explicit final modifier", this.position());
        }
        return super.typeCheck(tc);    
    }
   
    /*public Node addMembers(AddMemberVisitor tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();
        NodeFactory nf = tc.nodeFactory();
        addGenEnumMembersIfNeeded(ts, nf);
        return super.addMembers(tc);
    }
    
    protected void addGenEnumMembersIfNeeded(TypeSystem ts, NodeFactory nf){
        if (JL5Flags.isEnumModifier(type.flags())){
            MethodInstance valuesMi = ts.methodInstance(position(), this, Flags.PUBLIC, ts.Void(), "values" 
        }
    }*/
    
    protected Node addDefaultConstructor(TypeSystem ts, NodeFactory nf) {
        ConstructorInstance ci = ts.defaultConstructor(position(), this.type);
        this.type.addConstructor(ci);
        Block block = null;
        if (this.type.superType() instanceof ClassType && !JL5Flags.isEnumModifier(type.flags())) {
            ConstructorInstance sci = ts.defaultConstructor(position(),
                                                (ClassType) this.type.superType());
            ConstructorCall cc = nf.SuperCall(position(), 
                                              Collections.EMPTY_LIST);
            cc = cc.constructorInstance(sci);
            block = nf.Block(position(), cc);
        }
        else {
            block = nf.Block(position());
        }
        
        ConstructorDecl cd;
        if (!JL5Flags.isEnumModifier(type.flags())){
            cd = nf.ConstructorDecl(position(), Flags.PUBLIC,
                                                name, Collections.EMPTY_LIST,
                                                Collections.EMPTY_LIST,
                                                block);
        }
        else {
            cd = nf.ConstructorDecl(position(), Flags.PRIVATE,
                                                name, Collections.EMPTY_LIST,
                                                Collections.EMPTY_LIST,
                                                block);
        }
        cd = (ConstructorDecl) cd.constructorInstance(ci);
        return body(body.addMember(cd));
    }

    protected void disambiguateSuperType(AmbiguityRemover ar) throws SemanticException {
        TypeSystem ts = ar.typeSystem();
        if (this.superClass == null && JL5Flags.isEnumModifier(this.type().flags())) {
            this.type.superType(((JL5TypeSystem)ts).Enum());
        }
        super.disambiguateSuperType(ar);
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


}
