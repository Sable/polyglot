/* Copyright (C) 2006 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package polyglot.ext.jl5;

import polyglot.lex.Lexer;
import polyglot.ext.jl5.parse.Lexer_c;
import polyglot.ext.jl5.parse.Grm;
import polyglot.ext.jl5.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.types.reflect.*;
import polyglot.ext.jl5.visit.*;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.types.reflect.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.frontend.*;
import polyglot.main.*;

import java.util.*;
import java.io.*;

/**
 * Extension information for jl5 extension.
 */
public class ExtensionInfo extends polyglot.ext.jl.ExtensionInfo {
    static {
        // force Topics to load
        Topics t = new Topics();
    }

    public static final polyglot.frontend.Pass.ID ENUM_SWITCH_DISAMBIGUATE = new polyglot.frontend.Pass.ID("enum-switch-disambiguate");
    public static final polyglot.frontend.Pass.ID TYPE_CHECK_ALL = new polyglot.frontend.Pass.ID("type-check-all");
    public static final polyglot.frontend.Pass.ID APPLICATION_CHECK = new polyglot.frontend.Pass.ID("application-check");
    public static final polyglot.frontend.Pass.ID GENERIC_TYPE_HANDLER = new polyglot.frontend.Pass.ID("generic-type-handler");
    public static final polyglot.frontend.Pass.ID TYPE_VARS_ALL = new polyglot.frontend.Pass.ID("type-vars-all");
    public static final polyglot.frontend.Pass.ID ADD_TYPE_VARS = new polyglot.frontend.Pass.ID("add-type-vars");
    public static final polyglot.frontend.Pass.ID SIMPLIFY = new polyglot.frontend.Pass.ID("simplify");
    public static final polyglot.frontend.Pass.ID BOXING = new polyglot.frontend.Pass.ID("boxing");
    public static final polyglot.frontend.Pass.ID UNBOXING = new polyglot.frontend.Pass.ID("unboxing");
    public static final polyglot.frontend.Pass.ID LET_INSERTER = new polyglot.frontend.Pass.ID("let-inserter");
    public static final polyglot.frontend.Pass.ID GENERIC_CAST_INSERTER = new polyglot.frontend.Pass.ID("generic-cast-inserter");
    //public static final polyglot.frontend.Pass.ID GENERIC_ARGS = new polyglot.frontend.Pass.ID("generic-args");
    //public static final polyglot.frontend.Pass.ID GENERIC_RESET = new polyglot.frontend.Pass.ID("generic-reset");
   
    
    public String defaultFileExtension() {
        return "java";
    }

    public String compilerName() {
        return "jl5c";
    }

    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
        Lexer lexer = new Lexer_c(reader, source.name(), eq);
        Grm grm = new Grm(lexer, ts, nf, eq);
        return new CupParser(grm, source, eq);
    }

    protected NodeFactory createNodeFactory() {
        return new JL5NodeFactory_c();
    }

    protected TypeSystem createTypeSystem() {
        return new JL5TypeSystem_c();
    }

    public List passes(Job job) {
        getOptions().serialize_type_info = false;
        List passes = super.passes(job);
        // TODO: add passes as needed by your compiler
        //afterPass(passes, Pass.DISAM_ALL, new VisitorPass(ENUM_SWITCH_DISAMBIGUATE, job, new LeftoverAmbiguityRemover(job, ts, nf, LeftoverAmbiguityRemover.SWITCH_CASES)));
        
        //replacePass(passes, Pass.BUILD_TYPES, new VisitorPass(Pass.BUILD_TYPES, job, new JL5TypeBuilder(job, ts, nf)));
        
        afterPass(passes, Pass.BUILD_TYPES_ALL, new VisitorPass(GENERIC_TYPE_HANDLER, job, new JL5AmbiguityRemover(job, ts, nf, JL5AmbiguityRemover.TYPE_VARS)));
        
        afterPass(passes, GENERIC_TYPE_HANDLER, new GlobalBarrierPass(TYPE_VARS_ALL, job));
        
        //afterPass(passes, GENERIC_TYPE_HANDLER, new VisitorPass(ADD_TYPE_VARS, job, new AddTypeVarsVisitor(job, ts, nf)));
        
        //beforePass(passes, Pass.TYPE_CHECK, new VisitorPass(GENERIC_ARGS, job, new GenericTypeHandler(job, ts, nf)));
        //afterPass(passes, Pass.TYPE_CHECK, new VisitorPass(GENERIC_RESET, job, new GenericTypeReseter(job, ts, nf)));
        afterPass(passes, Pass.TYPE_CHECK, new BarrierPass(TYPE_CHECK_ALL, job));
        beforePass(passes, Pass.REACH_CHECK, new VisitorPass(APPLICATION_CHECK, job, new ApplicationChecker(job, ts, nf)));
        
        beforePass(passes, Pass.PRE_OUTPUT_ALL, new VisitorPass(GENERIC_CAST_INSERTER, job, new GenericCastInsertionVisitor(job, ts, nf)));
        beforePass(passes, GENERIC_CAST_INSERTER, new VisitorPass(BOXING, job, new BoxingVisitor(job, ts, nf)));

        beforePass(passes, BOXING, new VisitorPass(UNBOXING, job, new UnboxingVisitor(job, ts, nf)));
        
        beforePass(passes, UNBOXING, new VisitorPass(SIMPLIFY, job, new SimplifyVisitor(job, ts, nf)));

        beforePass(passes, SIMPLIFY, new VisitorPass(LET_INSERTER, job, new LetInsertionVisitor(job, ts, nf)));
        return passes;
    }
    
    public ClassFile createClassFile(File classFileSource, byte[] code){
        return new JL5ClassFile(classFileSource, code, this);
    }
    
    public Method createMethod(DataInputStream in, ClassFile clazz) throws IOException {
        Method m = new JL5Method(in, clazz);
        m.initialize();
        return m;
    }
    
}
