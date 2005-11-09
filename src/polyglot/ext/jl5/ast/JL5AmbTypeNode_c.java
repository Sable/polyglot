package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import java.util.*;
import polyglot.visit.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;

public class JL5AmbTypeNode_c extends AmbTypeNode_c implements JL5AmbTypeNode {

    protected List typeArguments;

    public JL5AmbTypeNode_c(Position pos, QualifierNode qual, String name, List typeArguments) {
        super(pos, qual, name);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public JL5AmbTypeNode typeArguments(List args){
        JL5AmbTypeNode_c n = (JL5AmbTypeNode_c) copy();
        n.typeArguments = args;
        return n;
    }

    protected JL5AmbTypeNode_c reconstruct(QualifierNode qual, List args){
        if (qual != this.qual() || !CollectionUtil.equals(args, this.typeArguments)){
            JL5AmbTypeNode_c n = (JL5AmbTypeNode_c)copy();
            n.qual = qual;
            n.typeArguments = args;
            return n;
        }
        return this;
    }
    
    public Node visitChildren(NodeVisitor v){
        QualifierNode qual = (QualifierNode)visitChild(this.qual(), v);
        List args = visitList(this.typeArguments, v);
        return reconstruct(qual, args);
    }

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        Node n = ar.nodeFactory().disamb().disambiguate(this, ar, position(), qual(), name());

        if (n instanceof CanonicalTypeNode && ((CanonicalTypeNode)n).type() instanceof JL5ParsedClassType){

            ParameterizedType pt = new ParameterizedType_c((JL5ParsedClassType)((CanonicalTypeNode)n).type());
            ArrayList typeArgs = new ArrayList(typeArguments.size());
            for (int i = 0; i < typeArguments.size(); i++ ){
                Type tn = ((TypeNode)typeArguments.get(i)).type();
                IntersectionType iType = (IntersectionType)pt.typeVariables().get(i);
                if (tn instanceof AnySuperType){
                    ((AnySuperType)tn).upperBound(iType.upperBound());
                }
                else if (tn instanceof AnyType){
                    ((AnyType)tn).upperBound(iType.upperBound());
                }
                typeArgs.add(tn);
            }
            pt.typeArguments(typeArgs);
            
            CanonicalTypeNode an = ((JL5NodeFactory)ar.nodeFactory()).JL5CanonicalTypeNode(n.position(), pt);
            return an;
        }
        else if (n instanceof CanonicalTypeNode){
            throw new SemanticException("Unexpected type: "+n+". Only class types can have type arguments.", n.position());
        }
        else if (n instanceof TypeNode){
            return n;
        }
         
        throw new SemanticException("Could not find type \"" +
            (qual() == null ? name() : qual().toString() + "." + name()) +
            "\".", position());
                
    }
}
