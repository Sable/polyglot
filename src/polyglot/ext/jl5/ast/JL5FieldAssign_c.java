package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import java.util.*;

public class JL5FieldAssign_c extends FieldAssign_c implements JL5FieldAssign, LetInsertionVisit,  BoxingVisit, UnboxingVisit {

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
            node = (JL5FieldAssign)node.right(nf.createBoxed(node.right().position(), node.right(), node.type(), ts, sv.context()));
        }
        
        return node;
                
    }
    
    public Node unboxing(UnboxingVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();

        JL5FieldAssign node = this;
        if (ts.needsUnboxing(node.type(), node.right().type())){
            node = (JL5FieldAssign)node.right(nf.createUnboxed(node.right().position(), node.right(), node.type(), ts, sv.context()));
        }
        return node;
                
    }

    public Node insertLet(LetInsertionVisitor v) throws SemanticException{
        // don't do anything for regular assigns
        if (operator() == Assign.ASSIGN) return this;
        
        // only care about op assigns
        JL5TypeSystem ts = (JL5TypeSystem)v.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)v.nodeFactory();
        
        Field f = (Field)left();
        if (f.target() instanceof TypeNode){
            // no local is needed --> no let is needed convert to x = x op y
            JL5Binary bin = nf.JL5Binary(f.position(), f, nf.getBinOpFromAssignOp(operator()), right());
            bin = (JL5Binary)bin.type(f.type());
            return operator(Assign.ASSIGN).right(bin);
        }
        else {

            FlagAnnotations fl = new FlagAnnotations();
            fl.classicFlags(Flags.NONE);
            fl.annotations(new ArrayList());
            
            JL5LocalDecl ld = nf.JL5LocalDecl(f.position(), fl, nf.CanonicalTypeNode(f.position(), f.target().type()), "$arg", (Expr)f.target());
            ld = (JL5LocalDecl)ld.localInstance(ts.localInstance(f.position(), Flags.NONE, f.target().type(), "$arg"));

            JL5Local local = nf.JL5Local(f.position(), "$arg");
            local = (JL5Local)local.localInstance(ld.localInstance());
            local = (JL5Local)local.type(f.target().type());

            // make alpha
            //JL5LocalAssign lAssign = nf.JL5LocalAssign(f.position(), local, Assign.ASSIGN, (Expr)f.target());
            //lAssign = (JL5LocalAssign)lAssign.type(f.target().type());
            
            // make beta
            JL5Field field = nf.JL5Field(f.position(), local, f.name());
            field = (JL5Field)field.fieldInstance(f.fieldInstance());
            field = (JL5Field)field.type(f.type());
            
            JL5Binary bin = nf.JL5Binary(f.position(), field, nf.getBinOpFromAssignOp(operator()), right());
            bin = (JL5Binary)bin.type(f.type());

            JL5FieldAssign fAssign = nf.JL5FieldAssign(f.position(), field, Assign.ASSIGN, bin);
            fAssign = (JL5FieldAssign)fAssign.type(f.type());
            
            return nf.JL5Let(f.position(), ld, fAssign).type(f.type()); 
        }        
    }
    
}
