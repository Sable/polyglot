package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;
import polyglot.ext.jl.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.types.*;

public class JL5LocalDecl_c extends LocalDecl_c implements JL5LocalDecl, ApplicationCheck, SimplifyVisit {

    protected List annotations;
    
    public JL5LocalDecl_c(Position pos, FlagAnnotations flags, TypeNode type, String name, Expr init){
        super(pos, flags.classicFlags(), type, name, init);
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, true);
        }
    }
    
    public List annotations(){
        return annotations;
    }
    
    public JL5LocalDecl annotations(List annotations){
        JL5LocalDecl_c n = (JL5LocalDecl_c) copy();
        n.annotations = annotations;
        return n;
    }

    protected LocalDecl reconstruct(TypeNode type, Expr init, List annotations){
        if (this.type() != type || this.init() != init ||  !CollectionUtil.equals(annotations, this.annotations)){
            JL5LocalDecl_c n = (JL5LocalDecl_c)copy();
            n.type = type;
            n.init = init;
            n.annotations = annotations;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        TypeNode type = (TypeNode)visitChild(this.type(), v);
        Expr init = (Expr)visitChild(this.init(), v);
        List annots = visitList(this.annotations, v);
        return reconstruct(type, init, annots);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (!flags().clear(Flags.FINAL).equals(Flags.NONE)){
            throw new SemanticException("Modifier: "+flags().clearFinal()+" not allowed here.", position());
        }
        if (type().type() instanceof IntersectionType && tc.context().inStaticContext()){
            throw new SemanticException("Cannot access non-static type: "+((IntersectionType)type().type()).name()+" in a static context.", position());
        }
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        ts.checkDuplicateAnnotations(annotations);
        if (init != null && init instanceof Call && ((JL5MethodInstance)((Call)init).methodInstance()).isGeneric() && type().type() instanceof ParameterizedType){
            if (!ts.isImplicitCastValid(init.type(), type.type()) && 
                !ts.equals(init.type(), type.type())){
                // determine infered type of type vars in init type
                // and update init type
                List formalTypes = new ArrayList();
                formalTypes.add(init.type());
                List argTypes = new ArrayList();
                argTypes.add(type.type());
                List inferredTypes = ts.inferTypesFromArgs(((JL5MethodInstance)((Call)init).methodInstance()).typeVariables(), formalTypes, argTypes, new ArrayList());
                Type inferred = init.type();
                if (init.type() instanceof IntersectionType && ((JL5MethodInstance)((Call)init).methodInstance()).typeVariables().contains(init.type())){
                    int pos = ((JL5MethodInstance)((Call)init).methodInstance()).typeVariables().indexOf(init.type());
                    inferred = (Type)inferredTypes.get(pos);
                }
                else if (init.type() instanceof ParameterizedType){
                    inferred = ((ParameterizedType)init.type()).convertToInferred(((JL5MethodInstance)((Call)init).methodInstance()).typeVariables(), inferredTypes);
                }
                if (inferred != init.type()){
                    return init(((JL5Call)init).type(inferred)).typeCheck(tc);
                }
            }
        }
        return super.typeCheck(tc);
    }
   
    public Node applicationCheck(ApplicationChecker appCheck) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)appCheck.typeSystem();
        for( Iterator it = annotations.iterator(); it.hasNext(); ){
            AnnotationElem next = (AnnotationElem)it.next();
            ts.checkAnnotationApplicability(next, this);
        }
        return this;
    }
        
    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        if (annotations != null){
            for (Iterator it = annotations.iterator(); it.hasNext(); ){
                print((AnnotationElem)it.next(), w, tr);
            }
        }
        super.prettyPrint(w, tr);
    }

    public Node simplify(SimplifyVisitor sv) throws SemanticException{
        /*if (init() != null){
            if (this.type().isPrimitive() && init().type().isClass()){
                
            }
        
        }*/
        return this;
    }
}
