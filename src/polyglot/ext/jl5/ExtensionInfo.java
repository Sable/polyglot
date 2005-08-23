package polyglot.ext.jl5;

import polyglot.lex.Lexer;
import polyglot.ext.jl5.parse.Lexer_c;
import polyglot.ext.jl5.parse.Grm;
import polyglot.ext.jl5.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

import polyglot.ast.*;
import polyglot.types.*;
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
        return passes;
    }

}
