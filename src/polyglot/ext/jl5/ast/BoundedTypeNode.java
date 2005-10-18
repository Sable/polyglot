package polyglot.ext.jl5.ast;

import polyglot.util.Enum;
import polyglot.ast.*;
import java.util.*;

public interface BoundedTypeNode extends TypeNode {

      public static class Kind extends Enum {
          public Kind(String name) {super(name); }
      }

      public static final Kind SUPER = new Kind("super");
      public static final Kind EXTENDS = new Kind("extends");

      Kind kind();

      BoundedTypeNode kind(Kind kind);

      TypeNode bound();

      BoundedTypeNode bound(TypeNode bound);
      
}
