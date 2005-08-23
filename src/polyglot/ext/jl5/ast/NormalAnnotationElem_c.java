package polyglot.ext.jl5.ast;

import java.util.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;

public class NormalAnnotationElem_c extends AnnotationElem_c implements NormalAnnotationElem {

    protected List elements;

    public NormalAnnotationElem_c(Position pos, TypeNode typeName, List elements){
        super(pos, typeName);
        this.elements = TypedList.copyAndCheck(elements, ElementValuePair.class, true);
    }
    
    public List elements(){
        return Collections.unmodifiableList(this.elements);
    }
    
    public NormalAnnotationElem elements(List elements){
        NormalAnnotationElem_c n = (NormalAnnotationElem_c) copy();
        n.elements = TypedList.copyAndCheck(elements, ElementValuePair.class, true);
        return n;
    }

    protected Node reconstruct(TypeNode tn, List elements){
        if (tn != this.typeName || !CollectionUtil.equals(elements, this.elements)) {
            NormalAnnotationElem_c n = (NormalAnnotationElem_c) copy();
            n.typeName = tn;
            n.elements = TypedList.copyAndCheck(elements, ElementValuePair.class, true);
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        TypeNode tn = (TypeNode) visitChild(this.typeName, v);
        List elements = visitList(this.elements, v);
        return reconstruct(tn, elements);
    }
   
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem(); 
        Context c = tc.context();
        // check that elements refer to annotation element instances
        for (Iterator it = elements().iterator(); it.hasNext(); ){
            ElementValuePair next = (ElementValuePair)it.next();
            AnnotationElemInstance ai = ts.findAnnotation(typeName().type().toReference(), next.name(), c.currentClass());
            // and value has to be the right type
            if (! ts.isImplicitCastValid(next.value().type(), ai.type()) &&
                ! ts.equals(next.value().type(), ai.type()) &&
                ! ts.numericConversionValid(ai.type(), next.value().constantValue()) && 
                ! ts.isBaseCastValid(next.value().type(), ai.type()) &&
                ! ts.numericConversionBaseValid(ai.type(), next.value().constantValue())){
                throw new SemanticException("The type of the value: "+next.value().type()+" for element: "+next.name()+" does not match the declared annotation type: "+ai.type(), next.value().position());
            }
        }

        // check all elements assigned values or have defaults
        List requiredAnnots = ((JL5ParsedClassType)typeName().type()).annotationElems();
        for(Iterator it = requiredAnnots.iterator(); it.hasNext(); ){
            AnnotationElemInstance next = (AnnotationElemInstance)it.next();
            if (!next.hasDefault()){
                // if the annotation decl doesn't have a default value
                // then one of the elements must be setting it
                if (!elementForNoDefault(next)){
                    throw new SemanticException("Must have value for element: "+next.name(), typeName().position());
                }
            }
        }
        
        return super.typeCheck(tc);
    }

    protected boolean elementForNoDefault(AnnotationElemInstance ai){
        for (Iterator it = elements.iterator(); it.hasNext(); ){
            if (((ElementValuePair)it.next()).name().equals(ai.name())) return true;
        }
        return false;
    }
    
    public void translate(CodeWriter w, Translator tr){
        super.translate(w, tr);
        w.write("(");
        for (Iterator it = elements().iterator(); it.hasNext(); ){
            print((ElementValuePair)it.next(), w, tr);
            if (it.hasNext()){
                w.write(",");
            }
        }
        w.write(") ");
    }
}
