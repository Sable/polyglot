package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import java.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

public class JL5MethodDecl_c extends MethodDecl_c implements JL5MethodDecl, ApplicationCheck {

    protected boolean compilerGenerated;
    protected List annotations;
    protected List paramTypes;
    
    public JL5MethodDecl_c(Position pos, FlagAnnotations flags, TypeNode returnType, String name, List formals, List throwTypes, Block body){
        super(pos, flags.classicFlags(), returnType, name, formals, throwTypes, body);
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, true);
        }
        
    }
    
    public JL5MethodDecl_c(Position pos, FlagAnnotations flags, TypeNode returnType, String name, List formals, List throwTypes, Block body, List paramTypes){
        super(pos, flags.classicFlags(), returnType, name, formals, throwTypes, body);
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, true);
        }
        this.paramTypes = paramTypes;
    }
    
    
    public boolean isGeneric(){
        if (!paramTypes.isEmpty()) return true;
        return false;
    }
    
    public boolean isCompilerGenerated(){
        return compilerGenerated;
    }

    public JL5MethodDecl setCompilerGenerated(boolean val){
        JL5MethodDecl_c n = (JL5MethodDecl_c) copy();
        n.compilerGenerated = val;
        return n;
    }

    public List annotations(){
        return this.annotations;
    }

    public JL5MethodDecl annotations(List annotations){
        JL5MethodDecl_c n = (JL5MethodDecl_c) copy();
        n.annotations = annotations;
        return n;    
    }
    
    public List paramTypes(){
        return this.paramTypes;
    }

    public JL5MethodDecl paramTypes(List paramTypes){
        JL5MethodDecl_c n = (JL5MethodDecl_c) copy();
        n.paramTypes = paramTypes;
        return n;
    }
           
    protected MethodDecl_c reconstruct(TypeNode returnType, List formals, List throwTypes, Block body, List annotations, List paramTypes){
        if (returnType != this.returnType || ! CollectionUtil.equals(formals, this.formals) || ! CollectionUtil.equals(throwTypes, this.throwTypes) || body != this.body || !CollectionUtil.equals(annotations, this.annotations) || !CollectionUtil.equals(paramTypes, this.paramTypes)) {
            JL5MethodDecl_c n = (JL5MethodDecl_c) copy();
            n.returnType = returnType;
            n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
            n.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
            n.body = body;
            n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, true);
            n.paramTypes = paramTypes;
            return n;
        }
        return this;
                                                            
    }

     public Node buildTypes(TypeBuilder tb) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tb.typeSystem();

        List l = new ArrayList(formals.size());
        for (int i = 0; i < formals.size(); i++) {
            l.add(ts.unknownType(position()));
        }

        List m = new ArrayList(throwTypes().size());
        for (int i = 0; i < throwTypes().size(); i++) {
            m.add(ts.unknownType(position()));
        }

        List typeVars = new ArrayList(paramTypes.size());
        for (Iterator it = paramTypes.iterator(); it.hasNext(); ){
            ParamTypeNode n = (ParamTypeNode)it.next();
            IntersectionType iType = new IntersectionType_c(tb.typeSystem());
            iType.name(n.id());
            ArrayList typeList = new ArrayList();
            if (n.bounds() != null){
                         for (int i = 0; i < n.bounds().size(); i++){//Iterator typesIt = n.boundsList().iterator(); typesIt.hasNext(); ){
                    typeList.add(tb.typeSystem().unknownType(position()));
                }
            }
            iType.bounds(typeList);
            typeVars.add(iType);
        }

        MethodInstance mi = ts.methodInstance(position(), ts.Object(), Flags.NONE, ts.unknownType(position()), name, l, m, typeVars);

        return methodInstance(mi);
    }
  
    public NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException {
        if (ar.kind() == JL5AmbiguityRemover.TYPE_VARS) {
            return ar.bypass(formals).bypass(returnType).bypass(throwTypes).bypass(body);
        }
        else {
            return super.disambiguateEnter(ar);
        }
    }
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        if (ar.kind() == JL5AmbiguityRemover.TYPE_VARS){
            JL5MethodInstance gmi = (JL5MethodInstance)methodInstance();
            // for each param type - create intersection type and 
            // add to type
            for (Iterator it = paramTypes.iterator(); it.hasNext(); ){
                ParamTypeNode n = (ParamTypeNode)it.next();
                if (gmi.hasTypeVariable(n.id())){
                    IntersectionType iType = gmi.getTypeVariable(n.id());
                    ArrayList typeList = new ArrayList();
                    if (n.bounds() != null){
                            for (Iterator typesIt = n.bounds().iterator(); typesIt.hasNext(); ){
                            typeList.add(((TypeNode)typesIt.next()).type());
                        }
                    }
                    iType.bounds(typeList);
                }
            }
            return this.methodInstance(gmi);
        }
        else {
            return super.disambiguate(ar);
        }
    }
        
    protected MethodInstance makeMethodInstance(ClassType ct, TypeSystem ts) throws SemanticException {
        List argTypes = new LinkedList();
        List excTypes = new LinkedList();

        for (Iterator i = formals.iterator(); i.hasNext(); ) {
            Formal f = (Formal) i.next();
            argTypes.add(f.declType());
        }

        for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
            TypeNode tn = (TypeNode) i.next();
            excTypes.add(tn.type());
        }

        Flags flags = this.flags;

        if (ct.flags().isInterface()) {
            flags = flags.Public().Abstract();
        }

        List typeVars = new ArrayList(paramTypes.size());
        for (Iterator it = paramTypes.iterator(); it.hasNext(); ){
            ParamTypeNode n = (ParamTypeNode)it.next();
            IntersectionType iType = new IntersectionType_c(ts);
            iType.name(n.id());
            
            ArrayList typeList = new ArrayList();
            if (n.bounds() != null){
                for (Iterator typesIt = n.bounds().iterator(); typesIt.hasNext(); ){
                    typeList.add(((TypeNode)typesIt.next()).type());
                }
            }
            iType.bounds(typeList);
            //iType.bounds(n.boundsList());
            typeVars.add(iType);
        }

        return ((JL5TypeSystem)ts).methodInstance(position(), ct, flags, returnType.type(), name, argTypes, excTypes, typeVars);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        // check no duplicate annotations used
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        ts.checkDuplicateAnnotations(annotations);
        
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
    
    public Node visitChildren(NodeVisitor v){
        List formals = visitList(this.formals, v);
        TypeNode returnType = (TypeNode) visitChild(this.returnType, v);
        List throwTypes = visitList(this.throwTypes, v);
        Block body = (Block) visitChild(this.body, v);
        List annotations = visitList(this.annotations, v);
        List paramTypes = visitList(this.paramTypes, v);
        return reconstruct(returnType, formals, throwTypes, body, annotations, paramTypes);
    }
    
    public void translate(CodeWriter w, Translator tr){
        if (isCompilerGenerated()) return;
        
        for (Iterator it = annotations.iterator(); it.hasNext(); ){
            print((AnnotationElem)it.next(), w, tr);
        }

        super.translate(w, tr);
    }

    public void prettyPrintHeader(Flags flags, CodeWriter w, PrettyPrinter tr) {
        w.begin(0);
        w.write(flags.translate());

        w.write("<");
        for (Iterator it = paramTypes.iterator(); it.hasNext(); ){
            ParamTypeNode next = (ParamTypeNode)it.next();
            print(next, w, tr);
            if (it.hasNext()){
                w.write(", ");
            }
        }
        w.write("> ");

        print(returnType, w, tr);
        w.write(" " + name + "(");
        w.begin(0);

        for (Iterator i = formals.iterator(); i.hasNext(); ) {
            Formal f = (Formal) i.next();
            print(f, w, tr);

            if (i.hasNext()) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }

        w.end();
        w.write(")");

        if (! throwTypes().isEmpty()) {
            w.allowBreak(6);
            w.write("throws ");

            for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
                TypeNode tn = (TypeNode) i.next();
                print(tn, w, tr);

                if (i.hasNext()) {
                    w.write(",");
                    w.allowBreak(4, " ");
                }
            }
        }

        w.end();
    }


}
