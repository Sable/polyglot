package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import java.util.*;

public class JL5FieldAssign_c extends FieldAssign_c implements JL5FieldAssign, BoxingVisit, UnboxingVisit {

    public JL5FieldAssign_c(Position pos, Field left, Operator op, Expr right){
        super(pos, left, op, right);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        if (right() instanceof Call && ((JL5MethodInstance)((Call)right()).methodInstance()).isGeneric()){
            if (!ts.isImplicitCastValid(right.type(), left.type()) && 
                !ts.equals(right.type(), left.type())){
                // determine infered type of type vars in init type
                // and update init type
                List formalTypes = new ArrayList();
                formalTypes.add(right.type());
                List argTypes = new ArrayList();
                argTypes.add(left.type());
                List inferredTypes = ts.inferTypesFromArgs(((JL5MethodInstance)((Call)right()).methodInstance()).typeVariables(), formalTypes, argTypes, new ArrayList());
                Type inferred = right().type();
                if (right().type() instanceof IntersectionType && ((JL5MethodInstance)((Call)right()).methodInstance()).typeVariables().contains(right().type())){
                    int pos = ((JL5MethodInstance)((Call)right()).methodInstance()).typeVariables().indexOf(right().type());
                    inferred = (Type)inferredTypes.get(pos);
                }
                else if (right().type() instanceof ParameterizedType){
                    inferred = ((ParameterizedType)right().type()).convertToInferred(((JL5MethodInstance)((Call)right()).methodInstance()).typeVariables(), inferredTypes);
                }
                if (inferred != right().type()){
                    return right(((JL5Call)right()).type(inferred)).typeCheck(tc);
                }
            }
        }
    
        return super.typeCheck(tc);
    }
    
    public Node boxing(BoxingVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();

        JL5FieldAssign node = this;
        /*if (ts.needsBoxing(node.type(), node.left().type())){
            node = (JL5FieldAssign)node.left(nf.createBoxed(node.left().position(), node.left(), ts, sv.context()));
        }*/
        if (ts.needsBoxing(node.type(), node.right().type())){
            node = (JL5FieldAssign)node.right(nf.createBoxed(node.right().position(), node.right(), ts, sv.context()));
        }
        
        return node;
                
    }
    
    public Node unboxing(UnboxingVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();

        JL5FieldAssign node = this;
        //System.out.println("node type: "+node.type());
        //System.out.println("left type: "+left().type());
        //System.out.println("right type: "+right().type());
        /*if (ts.needsUnboxing(node.type(), node.left().type())){
            node = (JL5FieldAssign)node.left(nf.createUnboxed(node.left().position(), node.left(), ts, sv.context()));
        }*/
        if (ts.needsUnboxing(node.type(), node.right().type())){
            node = (JL5FieldAssign)node.right(nf.createUnboxed(node.right().position(), node.right(), ts, sv.context()));
        }
        return node;
                
    }
}
