package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.visit.*;

public class JL5GenericMethodDecl_c extends JL5MethodDecl_c implements JL5GenericMethodDecl {

    protected List paramTypes;
    
    public JL5GenericMethodDecl_c(Position pos, FlagAnnotations flags, TypeNode returnType, String name, List formals, List throwTypes, Block body, List paramTypes){
        super(pos, flags, returnType, name, formals, throwTypes, body);
        this.paramTypes = paramTypes;
    }
    
    public List paramTypes(){
        return this.paramTypes;
    }
    
    public JL5GenericMethodDecl paramTypes(List paramTypes){
        JL5GenericMethodDecl_c n = (JL5GenericMethodDecl_c) copy();
        n.paramTypes = paramTypes;
        return n;
    }

    protected JL5MethodDecl_c reconstruct(TypeNode returnType, List formals, List throwTypes, Block body, List annotations, List paramTypes){
        if (returnType != this.returnType || ! CollectionUtil.equals(formals, this.formals) || ! CollectionUtil.equals(throwTypes, this.throwTypes) || body != this.body || !CollectionUtil.equals(annotations, this.annotations) || !CollectionUtil.equals(paramTypes, this.paramTypes)) {
            JL5GenericMethodDecl_c n = (JL5GenericMethodDecl_c) copy();
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
   
    public Node visitChildren(NodeVisitor v){
        List formals = visitList(this.formals, v);
        TypeNode returnType = (TypeNode) visitChild(this.returnType, v);
        List throwTypes = visitList(this.throwTypes, v);
        Block body = (Block) visitChild(this.body, v);
        List annotations = visitList(this.annotations, v);
        List paramTypes = visitList(this.paramTypes, v);
        return reconstruct(returnType, formals, throwTypes, body, annotations, paramTypes);
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
            iType.bounds(n.boundsList());
            typeVars.add(iType);
        }

        MethodInstance mi = ts.genericMethodInstance(position(), ts.Object(), Flags.NONE, ts.unknownType(position()), name, l, m, typeVars);

        return methodInstance(mi);
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
