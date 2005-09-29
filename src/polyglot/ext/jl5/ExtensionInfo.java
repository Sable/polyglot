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
    public static final polyglot.frontend.Pass.ID GENERIC_TYPE_ALL = new polyglot.frontend.Pass.ID("generic-type-all");
    
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
        
        replacePass(passes, Pass.BUILD_TYPES, new VisitorPass(Pass.BUILD_TYPES, job, new JL5TypeBuilder(job, ts, nf)));
        
        beforePass(passes, Pass.CLEAN_SIGS, new VisitorPass(GENERIC_TYPE_HANDLER, job, new JL5AmbiguityRemover(job, ts, nf, JL5AmbiguityRemover.TYPE_VARS)));
        
        //afterPass(passes, GENERIC_TYPE_HANDLER, new BarrierPass(GENERIC_TYPE_ALL, job));
        afterPass(passes, Pass.TYPE_CHECK, new BarrierPass(TYPE_CHECK_ALL, job));
        beforePass(passes, Pass.REACH_CHECK, new VisitorPass(APPLICATION_CHECK, job, new ApplicationChecker(job, ts, nf)));

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
