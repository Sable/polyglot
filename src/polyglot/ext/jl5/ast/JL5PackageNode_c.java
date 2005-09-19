package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import java.util.*;
import polyglot.ext.jl5.types.*;

public class JL5PackageNode_c extends PackageNode_c implements JL5PackageNode {

    protected Flags classicFlags;
    protected List annotations;
    
    public JL5PackageNode_c(Position pos, FlagAnnotations flags, polyglot.types.Package package_){
        super(pos, package_);
        this.classicFlags = flags.classicFlags();
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, false);
        }    
    }
    
    public List annotations(){
        return this.annotations;
    }
    
    public JL5PackageNode annotations(List annotations){
        JL5PackageNode_c n = (JL5PackageNode_c) copy();
        n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, false);
        return n;
    }

    protected JL5PackageNode_c reconstruct(List annotations){
        if (!CollectionUtil.equals(annotations, this.annotations)){
            JL5PackageNode_c n = (JL5PackageNode_c) copy();
            n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, false);
            return n;
        }
        return this;
    }
    
    public Node visitChildren(NodeVisitor v){
        List annotations = visitList(this.annotations, v);
        return reconstruct(annotations);
    }
    
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (!classicFlags.equals(Flags.NONE)){
            throw new SemanticException ("Modifier "+classicFlags+" not allowed here.", position());
        }
        return super.typeCheck(tc);
    }
}

