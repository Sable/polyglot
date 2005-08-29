package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import java.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;

public class JL5MethodDecl_c extends MethodDecl_c implements JL5MethodDecl {

    protected List annotations;
    
    public JL5MethodDecl_c(Position pos, FlagAnnotations flags, TypeNode returnType, String name, List formals, List throwTypes, Block body){
        super(pos, flags.classicFlags(), returnType, name, formals, throwTypes, body);
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, true);
        }
    }
    
    protected boolean compilerGenerated;
    
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
    
    protected MethodDecl_c reconstruct(TypeNode returnType, List formals, List throwTypes, Block body, List annotations){
        if (returnType != this.returnType || ! CollectionUtil.equals(formals, this.formals) || ! CollectionUtil.equals(throwTypes, this.throwTypes) || body != this.body || !CollectionUtil.equals(annotations, this.annotations)) {
            JL5MethodDecl_c n = (JL5MethodDecl_c) copy();
            n.returnType = returnType;
            n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
            n.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
            n.body = body;
            n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, true);
            return n;
        }
        return this;
                                                            
    }
   
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        // check no duplicate annotations used
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        ts.checkDuplicateAnnotations(annotations);
        
        return super.typeCheck(tc);
    }
    
    public Node visitChildren(NodeVisitor v){
        List formals = visitList(this.formals, v);
        TypeNode returnType = (TypeNode) visitChild(this.returnType, v);
        List throwTypes = visitList(this.throwTypes, v);
        Block body = (Block) visitChild(this.body, v);
        List annotations = visitList(this.annotations, v);
        return reconstruct(returnType, formals, throwTypes, body, annotations);
    }
    
    public void translate(CodeWriter w, Translator tr){
        if (isCompilerGenerated()) return;
        
        for (Iterator it = annotations.iterator(); it.hasNext(); ){
            print((AnnotationElem)it.next(), w, tr);
        }

        super.translate(w, tr);
    }
}
