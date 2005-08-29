package polyglot.ext.jl5.ast;

import java.util.*;

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
    protected List annotations;

    public JL5ClassDecl_c(Position pos, FlagAnnotations flags, String name,
                       TypeNode superClass, List interfaces, ClassBody body) {
	    super(pos, flags.classicFlags(), name, superClass, interfaces, body);
        if (flags.annotations() != null){
            this.annotations = TypedList.copyAndCheck(flags.annotations(), AnnotationElem.class, false);
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, false);
        }
        
    }

    public List annotations(){
        return this.annotations;
    }
    
    public ClassDecl annotations(List annotations){
        JL5ClassDecl_c n = (JL5ClassDecl_c) copy();
        n.annotations = annotations;
        return n;
    }
    
    protected ClassDecl reconstruct(TypeNode superClass, List interfaces, ClassBody body, List annotations){
        if (superClass != this.superClass || !CollectionUtil.equals(interfaces, this.interfaces) || body != this.body || !CollectionUtil.equals(annotations, this.annotations)){
            JL5ClassDecl_c n = (JL5ClassDecl_c) copy();
            n.superClass = superClass;
            n.interfaces = TypedList.copyAndCheck(interfaces, TypeNode.class, true);
            n.body = body;
            n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, true);
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        TypeNode superClass = (TypeNode) visitChild(this.superClass, v);
        List interfaces = visitList(this.interfaces, v);
        ClassBody body = (ClassBody) visitChild(this.body, v);
        List annots = visitList(this.annotations, v); 
        return reconstruct(superClass, interfaces, body, annots);
    }
    
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (JL5Flags.isEnumModifier(flags()) && flags().isAbstract()){
            throw new SemanticException("Enum types cannot have abstract modifier", this.position());
        }
        if (JL5Flags.isEnumModifier(flags()) && flags().isPrivate()){
            throw new SemanticException("Enum types cannot have explicit final modifier", this.position());
        }
        if (JL5Flags.isAnnotationModifier(flags()) && flags().isPrivate()){
            throw new SemanticException("Annotation types cannot have explicit private modifier", this.position());
        }
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        ts.checkDuplicateAnnotations(annotations);
        return super.typeCheck(tc);    
    }
   
    public Node addMembers(AddMemberVisitor tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();
        NodeFactory nf = tc.nodeFactory();
        addGenEnumMethods(ts, nf);
        return super.addMembers(tc);
    }
    
    protected void addGenEnumMethods(TypeSystem ts, NodeFactory nf){
        if (JL5Flags.isEnumModifier(type.flags())){
            
            // add values method
            FlagAnnotations vmFlags = new FlagAnnotations();
            vmFlags.classicFlags(Flags.PUBLIC.set(Flags.STATIC.set(Flags.FINAL)));
            JL5MethodDecl valuesMeth = ((JL5NodeFactory)nf).JL5MethodDecl(position(), vmFlags, nf.CanonicalTypeNode(position(), ts.arrayOf(this.type())), "values", Collections.EMPTY_LIST, Collections.EMPTY_LIST,nf.Block(position()));
            
            valuesMeth = valuesMeth.setCompilerGenerated(true);
            
            JL5MethodInstance mi = (JL5MethodInstance)ts.methodInstance(position(), this.type(), JL5Flags.PUBLIC.set(JL5Flags.STATIC).set(JL5Flags.FINAL), ts.arrayOf(this.type()), "values", Collections.EMPTY_LIST, Collections.EMPTY_LIST);
            
            mi = mi.setCompilerGenerated(true);
            this.type.addMethod(mi);
            valuesMeth = (JL5MethodDecl)valuesMeth.methodInstance(mi);
            body(body.addMember(valuesMeth));

            // add valueOf method
            ArrayList formals = new ArrayList();
            Formal f1 = nf.Formal(position(), JL5Flags.NONE, nf.CanonicalTypeNode(position(), ts.String()), "arg1");
            formals.add(f1);
            
            FlagAnnotations voFlags = new FlagAnnotations();
            voFlags.classicFlags(Flags.PUBLIC.set(Flags.STATIC));
            
            JL5MethodDecl valueOfMeth = ((JL5NodeFactory)nf).JL5MethodDecl(position(), voFlags, nf.CanonicalTypeNode(position(), this.type()), "valueOf", formals, Collections.EMPTY_LIST,nf.Block(position()));
            
            valueOfMeth = valueOfMeth.setCompilerGenerated(true);
            
            ArrayList formalTypes = new ArrayList();
            formalTypes.add(ts.String());
            
            JL5MethodInstance mi2 = (JL5MethodInstance)ts.methodInstance(position(), this.type(), JL5Flags.PUBLIC.set(JL5Flags.STATIC), this.type(), "valueOf", formalTypes, Collections.EMPTY_LIST);
            
            mi2 = mi2.setCompilerGenerated(true);
            this.type.addMethod(mi2);
            valueOfMeth = (JL5MethodDecl)valueOfMeth.methodInstance(mi2);
            body(body.addMember(valueOfMeth));

            
        }
    }
    
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
            cd = nf.ConstructorDecl(position(), JL5Flags.PUBLIC,
                                                name, Collections.EMPTY_LIST,
                                                Collections.EMPTY_LIST,
                                                block);
        }
        else {
            cd = nf.ConstructorDecl(position(), JL5Flags.PRIVATE,
                                                name, Collections.EMPTY_LIST,
                                                Collections.EMPTY_LIST,
                                                block);
        }
        cd = (ConstructorDecl) cd.constructorInstance(ci);
        return body(body.addMember(cd));
    }


    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
        if (flags.isInterface()) {
            if (JL5Flags.isAnnotationModifier(flags)){
                w.write(JL5Flags.clearAnnotationModifier(flags).clearInterface().clearAbstract().translate());
                w.write("@");
            }
            else{
                w.write(flags.clearInterface().clearAbstract().translate());
            }
        }
        else {
            w.write(flags.translate());
        }

        for (Iterator it = annotations.iterator(); it.hasNext(); ){
            print((AnnotationElem)it.next(), w, tr);
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

        if (superClass() != null && !JL5Flags.isEnumModifier(type.flags())) {
            w.write(" extends ");
            print(superClass(), w, tr);
        }

        if (! interfaces.isEmpty() && !JL5Flags.isAnnotationModifier(type.flags())) {
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
