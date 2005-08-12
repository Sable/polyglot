package polyglot.ext.jl5.ast;

import polyglot.ext.jl.ast.*;
import polyglot.ext.jl.types.*;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;

public class JL5Disamb_c extends Disamb_c implements JL5Disamb {

    protected Node disambiguateTypeNodePrefix(TypeNode tn) throws SemanticException {
    
        Type t = tn.type();
        if (t.isReference() && exprOK()){
            try {
                FieldInstance fi = ts.findField(t.toReference(), name, c);
                return ((JL5NodeFactory)nf).JL5Field(pos, tn, name).fieldInstance(fi);
            }
            catch(NoMemberException e){
                if (e.getKind() != e.FIELD){
                    throw e;
                }
            }
        }

        if (t.isClass() && typeOK()){
            Resolver tc = ts.classContextResolver(t.toClass());
            Named n = tc.find(name);
            if (n instanceof Type){
                Type type = (Type)n;
                return nf.CanonicalTypeNode(pos, type);
            }
        }
        return null;
    }

    protected Node disambiguateExprPrefix(Expr e) throws SemanticException {
        if (exprOK()){
            return ((JL5NodeFactory)nf).JL5Field(pos, e, name);        
        }
        return null;
    }


    protected Node disambiguateVarInstance(VarInstance vi) throws SemanticException {
        if (vi instanceof FieldInstance) {
            FieldInstance fi = (FieldInstance) vi;
            Receiver r = makeMissingFieldTarget(fi);
            return ((JL5NodeFactory)nf).JL5Field(pos, r, name).fieldInstance(fi).targetImplicit(true);
        } else if (vi instanceof LocalInstance) {
            LocalInstance li = (LocalInstance) vi;
            return nf.Local(pos, name).localInstance(li);
        }
        return null;
    }


}
