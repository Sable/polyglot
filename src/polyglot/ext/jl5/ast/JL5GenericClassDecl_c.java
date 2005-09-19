package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

import polyglot.util.*;
import polyglot.ast.*;
import java.util.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import polyglot.visit.*;
import polyglot.types.*;

public class JL5GenericClassDecl_c extends JL5ClassDecl_c implements JL5GenericClassDecl {

    protected List paramTypes;
    
    public JL5GenericClassDecl_c(Position pos, FlagAnnotations fl, String name, TypeNode superType, List interfaces, ClassBody body, List paramTypes){
        
        super(pos, fl, name, superType, interfaces, body);
        this.paramTypes = paramTypes;
    }
    
    public List paramTypes(){
        return this.paramTypes;
    }
    public JL5GenericClassDecl paramTypes(List types){
        JL5GenericClassDecl_c n = (JL5GenericClassDecl_c) copy();
        n.paramTypes = types;
        return n;
    }

    protected ClassDecl reconstruct(TypeNode superClass, List interfaces, ClassBody body, List annotations, List paramTypes){
        if (superClass != this.superClass || !CollectionUtil.equals(interfaces, this.interfaces) || body != this.body || !CollectionUtil.equals(annotations, this.annotations) || !CollectionUtil.equals(paramTypes, this.paramTypes)){
            JL5GenericClassDecl_c n = (JL5GenericClassDecl_c) copy();
            n.superClass = superClass;
            n.interfaces = TypedList.copyAndCheck(interfaces, TypeNode.class, false);
            n.body = body;
            n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, false);
            n.paramTypes = paramTypes;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        TypeNode superClass = (TypeNode) visitChild(this.superClass, v);
        List interfaces = visitList(this.interfaces, v);
        ClassBody body = (ClassBody) visitChild(this.body, v);
        List annots = visitList(this.annotations, v); 
        List paramTypes = visitList(this.paramTypes, v);
        return reconstruct(superClass, interfaces, body, annots, paramTypes);
    }

    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        tb = ((JL5TypeBuilder)tb).pushGenericClass(position(), flags, name);
        GenericParsedClassType ct = (GenericParsedClassType)tb.currentClass();
         
        // Member classes of interfaces are implicitly public and static.
        if (ct.isMember() && ct.outer().flags().isInterface()) {
            ct.flags(ct.flags().Public().Static());
        }

        // Member interfaces are implicitly static. 
        if (ct.isMember() && ct.flags().isInterface()) {
            ct.flags(ct.flags().Static());
        }

        // Interfaces are implicitly abstract. 
        if (ct.flags().isInterface()) {
            ct.flags(ct.flags().Abstract());
        }

        return tb;
 
    }

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        GenericParsedClassType type = (GenericParsedClassType)tb.currentClass();
        // for each param type - create intersection type and 
        // add to type
        for (Iterator it = paramTypes.iterator(); it.hasNext(); ){
            ParamTypeNode n = (ParamTypeNode)it.next();
            IntersectionType iType = new IntersectionType_c(tb.typeSystem());
            iType.name(n.id());
            iType.bounds(n.boundsList());
            type.addTypeVariable(iType);
        }
        if (type != null){
            return type(type).flags(type.flags());
        }
        return this;
    }
 
    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
        prettyPrintModifiers(w, tr);
        prettyPrintName(w, tr);
       
        w.write("<");
        for (Iterator it = paramTypes.iterator(); it.hasNext(); ){
            ParamTypeNode next = (ParamTypeNode)it.next();
            print(next, w, tr);
            if (it.hasNext()){
                w.write(", ");
            }
        }
        w.write("> ");
        
        prettyPrintHeaderRest(w, tr);
    }
    
}
