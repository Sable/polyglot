package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.util.*;
import java.util.*;
import polyglot.ext.jl.ast.*;

/**
 * A <code>AmbAssign</code> represents a Java assignment expression to
 * an as yet unknown expression.
 */
public class JL5AmbAssign_c extends AmbAssign_c implements JL5AmbAssign
{
  public JL5AmbAssign_c(Position pos, Expr left, Operator op, Expr right) {
    super(pos, left, op, right);
  }

  public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
      Assign n = (Assign) super.disambiguate(ar);
      
      if (n.left() instanceof Local) {
          return ((JL5NodeFactory)ar.nodeFactory()).JL5LocalAssign(n.position(), (Local)left(), operator(), right());
      }
      else if (n.left() instanceof Field) {
          return ((JL5NodeFactory)ar.nodeFactory()).JL5FieldAssign(n.position(), (Field)left(), operator(), right());
      } 
      else if (n.left() instanceof ArrayAccess) {
          return ((JL5NodeFactory)ar.nodeFactory()).JL5ArrayAccessAssign(n.position(), (ArrayAccess)left(), operator(), right());
      }

      throw new SemanticException("Could not disambiguate left side of assignment!", n.position());
  }
  
}
