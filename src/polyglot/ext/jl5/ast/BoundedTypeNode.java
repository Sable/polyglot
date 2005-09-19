package polyglot.ext.jl5.ast;

import polyglot.util.Enum;
import polyglot.ast.*;
import java.util.*;

public interface BoundedTypeNode extends TypeNode {

      public static class Kind extends Enum {
          public Kind(String name) {super(name); }
      }

      public static final Kind UPPER = new Kind("upper");
      public static final Kind LOWER = new Kind("lower");

      Kind kind();

      BoundedTypeNode kind(Kind kind);

      List boundsList();

      BoundedTypeNode boundsList(List list);
      
}
