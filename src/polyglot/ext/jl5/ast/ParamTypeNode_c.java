package polyglot.ext.jl5.ast;

import polyglot.util.*;
import java.util.*;
import polyglot.visit.*;
import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

public class ParamTypeNode_c extends TypeNode_c implements ParamTypeNode, TypeVarsAdder {
    
    protected String id;
    protected List bounds;
    
    public ParamTypeNode_c(Position pos, List bounds, String id){
        super(pos);
        this.id = id;
        this.bounds = bounds;
    }
    
    public ParamTypeNode id(String id){
        ParamTypeNode_c n = (ParamTypeNode_c) copy();
        n.id = id;
        return n;
    }
    
    public String id(){
        return this.id;
    }

    public ParamTypeNode bounds(List l){
        ParamTypeNode_c n = (ParamTypeNode_c) copy();
        n.bounds = l;
        return n;
    }

    public List bounds(){
        return bounds;
    }

    public ParamTypeNode reconstruct(List bounds){
        if (!CollectionUtil.equals(bounds, this.bounds)){
            ParamTypeNode_c n = (ParamTypeNode_c) copy();
            n.bounds = bounds;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        List bounds = visitList(this.bounds, v);
        return reconstruct(bounds);
    }
   
    // nothihng needed for buildTypesEnter - not a code block like methods

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        // makes a new IntersectionType with a list of bounds which
        // are unknown types
        JL5TypeSystem ts = (JL5TypeSystem)tb.typeSystem();
        
        ArrayList typeList = new ArrayList(bounds.size());
        for (int i = 0; i < bounds.size(); i++){
            typeList.add(ts.unknownType(position()));
        }

        IntersectionType iType = ts.intersectionType(position(), id, typeList);

        return type(iType);
        
    }
   
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        // all of the children (bounds list) will have already been 
        // disambiguated and should there for be actual types
        JL5TypeSystem ts = (JL5TypeSystem)ar.typeSystem();
        
        ArrayList typeList = new ArrayList(bounds.size());
        for (Iterator it = bounds.iterator(); it.hasNext();){
            typeList.add(((TypeNode)it.next()).type());
        }

        IntersectionType iType = ts.intersectionType(position(), id, typeList);

        return type(iType);
    }

    public NodeVisitor addTypeVars(AddTypeVarsVisitor am){
        if (am.context().inCode()){
            CodeInstance ci = am.context().currentCode();
            if (ci instanceof JL5MethodInstance){
                ((JL5MethodInstance)ci).addTypeVariable((IntersectionType)type());
                System.out.println("add type var: "+type()+" to mi: "+ci);
            }
            else if (ci instanceof JL5ConstructorInstance){
                ((JL5ConstructorInstance)ci).addTypeVariable((IntersectionType)type());
            }
        }
        else {
            JL5ParsedClassType ct = (JL5ParsedClassType)am.context().currentClassScope();
            ct.addTypeVariable((IntersectionType)type());
        }
        return am.bypassChildren(this);
    }
    
    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        w.write(id);
        if (bounds() != null && !bounds().isEmpty()){
            w.write(" extends ");
            for (Iterator it = bounds.iterator(); it.hasNext(); ){
                TypeNode tn = (TypeNode)it.next();
                print(tn, w, tr);
                if (it.hasNext()){
                    w.write(" & ");
                }
            }
        }
    }
}
