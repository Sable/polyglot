package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import java.util.*;

public class JL5ArrayAccessAssign_c extends ArrayAccessAssign_c implements JL5ArrayAccessAssign, LetInsertionVisit {

    public JL5ArrayAccessAssign_c(Position pos, ArrayAccess left, Operator op, Expr right){
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
    
    public Node insertLet(LetInsertionVisitor v) throws SemanticException{
        // don't do anything for regular assigns
        if (operator() == Assign.ASSIGN) return this;
        
        // only care about op assigns
        JL5TypeSystem ts = (JL5TypeSystem)v.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)v.nodeFactory();
        
        ArrayAccess aa = (ArrayAccess)left();

        LocalDecl ld = nf.JL5LocalDecl(aa.position(), new FlagAnnotations(Flags.NONE, new ArrayList()), nf.CanonicalTypeNode(aa.position(), aa.index().type()), "$arg", (Expr)aa.index()).localInstance(ts.localInstance(aa.position(), Flags.NONE, aa.index().type(), "$arg"));

        Expr local = nf.Local(aa.position(), "$arg").localInstance(ld.localInstance()).type(aa.index().type());

        // make beta
        ArrayAccess array = (ArrayAccess)nf.JL5ArrayAccess(aa.position(), aa.array(), local).type(aa.type());
        
        Expr bin = nf.JL5Binary(aa.position(), array, nf.getBinOpFromAssignOp(operator()), right()).type(aa.type());

        Expr aAssign = nf.JL5ArrayAccessAssign(aa.position(), array, Assign.ASSIGN, bin).type(aa.type());
        
        return nf.JL5Let(aa.position(), ld, aAssign).type(aa.type()); 
    }
    
}
