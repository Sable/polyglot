package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.ext.jl5.types.*;
import polyglot.types.*;

public class JL5FieldDecl_c extends FieldDecl_c implements JL5FieldDecl {

    public JL5FieldDecl_c(Position pos, FlagAnnotations flags, TypeNode type, String name, Expr init){
        super(pos, flags.classicFlags(), type, name, init);
    }
}
