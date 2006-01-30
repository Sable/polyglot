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

package polyglot.ext.jl5.ast;

import java.util.*;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.ext.jl.ast.*;
import polyglot.visit.*;
import polyglot.util.*;
import polyglot.ext.jl5.types.*;
import polyglot.frontend.*;

public class EnumConstantDecl_c extends Term_c implements EnumConstantDecl
{   
    protected List args;
    protected String name;
    protected Flags flags;
    protected List annotations;
    protected ClassBody body;
    protected EnumInstance enumInstance;
    protected ConstructorInstance constructorInstance;
    protected ParsedClassType anonType;
    
    public EnumConstantDecl_c(Position pos, FlagAnnotations flags, String name, List args, ClassBody body){
        super(pos);
        this.name = name;
        this.args = args;
        this.body = body;
        this.flags = flags.classicFlags();
        this.annotations = flags.annotations();
    }
        
    
    /** get args */
    public List args(){
        return args;
    }

    public List annotations(){
        return annotations;
    }

    /** set args */
    public EnumConstantDecl args(List args){
        EnumConstantDecl_c n = (EnumConstantDecl_c)copy();
        n.args = args;
        return n;
    }

    /** set name */
    public EnumConstantDecl name(String name){
        EnumConstantDecl_c n = (EnumConstantDecl_c)copy();
        n.name = name;
        return n;
    }

    /** get name */
    public String name(){
        return name;
    }
    
    /** set body */
    public EnumConstantDecl body(ClassBody body){
        EnumConstantDecl_c n = (EnumConstantDecl_c)copy();
        n.body = body;
        return n;
    }

    /** get body */
    public ClassBody body(){
        return body;
    }

    public ParsedClassType anonType(){
        return anonType;
    }

    public Flags flags(){
        return flags;
    }
    
    public EnumConstantDecl anonType(ParsedClassType pct){
        EnumConstantDecl_c n = (EnumConstantDecl_c) copy();
        n.anonType = pct;
        return n;
    }
    
    public EnumConstantDecl enumInstance(EnumInstance ei){
        EnumConstantDecl_c n = (EnumConstantDecl_c) copy();
        n.enumInstance = ei;
        return n;
    }

    public EnumInstance enumInstance(){
        return enumInstance;
    }
    
    public EnumConstantDecl constructorInstance(ConstructorInstance ci){
        EnumConstantDecl_c n = (EnumConstantDecl_c) copy();
        n.constructorInstance = ci;
        return n;
    }

    public ConstructorInstance constructorInstance(){
        return constructorInstance;
    }
    
    protected EnumConstantDecl_c reconstruct(List args, ClassBody body){
        if (!CollectionUtil.equals(args, this.args) || body != this.body) {
            EnumConstantDecl_c n = (EnumConstantDecl_c) copy();
            n.args = TypedList.copyAndCheck(args, Expr.class, true);
            n.body = body;
            return n;
        }
        return this;
    }
    
    protected EnumConstantDecl_c reconstruct(List args, ClassBody body, List annotations){
        if (!CollectionUtil.equals(args, this.args) || body != this.body ||
                !CollectionUtil.equals(annotations, this.annotations)) {
            EnumConstantDecl_c n = (EnumConstantDecl_c) copy();
            n.args = TypedList.copyAndCheck(args, Expr.class, true);
            n.body = body;
            n.annotations = annotations;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        List args = visitList(this.args, v);
        ClassBody body = (ClassBody) visitChild(this.body, v);
        List annotations = visitList(this.annotations, v);
        return reconstruct(args, body, annotations);
    }

    public Context enterScope(Node child, Context c) {
        if (child == body && anonType != null && body != null){
            c = c.pushClass(anonType, anonType);
        }
        return super.enterScope(child, c);
    }
    
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        tb = (TypeBuilder)tb.pushCode();
        if (body != null){
            tb = (TypeBuilder) tb.bypass(body);
        }
        return tb;
    }

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tb.typeSystem();

        EnumConstantDecl n = this;
        
        if (n.body() != null){
            TypeBuilder bodyTB = (TypeBuilder)tb.visitChildren();
            bodyTB = bodyTB.pushAnonClass(position());

            n = (EnumConstantDecl)n.body((ClassBody)n.body().visit(bodyTB));
            ParsedClassType bodyType = (ParsedClassType) bodyTB.currentClass();
            n = (EnumConstantDecl)n.anonType(bodyType);
        }
            
        EnumInstance ei = ts.enumInstance(n.position(), ts.Enum(), JL5Flags.NONE, n.name(), n.anonType());

        List l = new ArrayList(n.args().size());
        for (int i = 0; i < n.args().size(); i++){
            l.add(ts.unknownType(position()));
        }

        ConstructorInstance ci = ts.constructorInstance(position(), ts.Object(), Flags.NONE, l, Collections.EMPTY_LIST);

        n = (EnumConstantDecl)n.constructorInstance(ci);
        
        return n.enumInstance(ei);
    }

    public NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException {

        if (body != null){
            return ar.bypass(body);
        }
        
        if (ar.kind() == AmbiguityRemover.SUPER) {
            return ar.bypassChildren(this);
        }

        return ar;
    }


    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {

        if (ar.kind() == AmbiguityRemover.SIGNATURES){
            Context c = ar.context();
            JL5TypeSystem ts = (JL5TypeSystem)ar.typeSystem();

            ParsedClassType ct = c.currentClassScope();
            
            EnumInstance ei = ts.enumInstance(position(), ct, JL5Flags.NONE, name, anonType);
            return enumInstance(ei);
        }

        return this;
    }

    public NodeVisitor typeCheckEnter(TypeChecker tc) throws SemanticException {
        if (body != null){
            return tc.bypass(body);
        }
        return tc;
    }
    
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        Context c = tc.context();
        ClassType ct = c.currentClass();

        List argTypes = new LinkedList();
        for (Iterator it = this.args.iterator(); it.hasNext();){
            Expr e = (Expr) it.next();
            argTypes.add(e.type());
        }
        
        ConstructorInstance ci = ts.findConstructor(ct, argTypes, c.currentClass());
        EnumConstantDecl_c n = (EnumConstantDecl_c)this.constructorInstance(ci);
        
        if (n.flags() != Flags.NONE){
            throw new SemanticException("Cannot have modifier(s): "+flags+" on enum constant declaration", this.position());
        }
        
        ts.checkDuplicateAnnotations(n.annotations());

        // set superType for anon class
        if (body != null){
            anonType().superType(ct);
            ClassBody body = n.typeCheckBody(tc, ct);
            return n.body(body);
        }

        
        return n;
    }

    protected ClassBody typeCheckBody(TypeChecker tc, ClassType superType) throws SemanticException {
        Context bodyCtxt = tc.context().pushClass(anonType, anonType);
        Job sj = tc.job().spawn(bodyCtxt, body, Pass.CLEAN_SUPER, Pass.DISAM_ALL);
        if (!sj.status()){
            if (!sj.reportedErrors()){
                throw new SemanticException("Could not disambiguate body of anonymous subclass "+name+" of "+superType+".");
                            
            }
            throw new SemanticException();
        }

        ClassBody b = (ClassBody)sj.ast();

        TypeChecker bodyTC = (TypeChecker)tc.context(bodyCtxt);
        b = (ClassBody) visitChild(b, bodyTC.visitChildren());

        bodyTC.typeSystem().checkClassConformance(anonType());

        return b;
    }
    
    public String toString(){
        return name + "(" + args + ")" + body != null ? "..." : "";
    }
    
    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        w.write(name);
        if (args != null){
            w.write(" ( ");
            Iterator it = args.iterator();
            while (it.hasNext()){
                Expr e = (Expr) it.next();
                print(e, w, tr);
                if (it.hasNext()){
                    w.write(", ");
                    w.allowBreak(0);
                }
            }
            w.write(" )");
        }
        if (body != null){
            w.write(" {");
            print(body, w, tr);
            w.write("}");
        }
    }

    public List acceptCFG(CFGBuilder v, List succs){
        return succs;
    }

    public Term entry(){
        return this;
    }

    public NodeVisitor addMembersEnter(AddMemberVisitor am){
        JL5ParsedClassType ct = (JL5ParsedClassType_c)am.context().currentClassScope();

        ct.addEnumConstant(enumInstance);
        return am.bypassChildren(this);
    }
}
