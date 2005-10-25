package polyglot.ext.jl5.types.reflect;

import java.io.*;
import polyglot.types.reflect.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;
import java.util.*;
import polyglot.util.*;

public class Signature extends Attribute {
    
    protected DataInputStream in;
    protected int index;
    protected ClassFile cls;
    protected JL5TypeSystem ts;
    protected Position position;
    protected ClassSig classSignature;
    protected MethodSig methodSignature;
    protected FieldSig fieldSignature;
    protected List typeVars;
    protected ClassType curClass;
    
    /**
     * Grammar:
     * class_sig = 
     *  formal_type_params_opt super_class_sig super_inter_sig_list_opt
     * formal_type_params_opt =
     *  * empty *
     *  | LEFT_ANGLE formal_type_param_list RIGHT_ANGLE
     * formal_type_param_list =
     *  formal_type_param
     *  | formal_type_param_list formal_type_param
     * formal_type_param =
     *  ID class_bound inter_bound_list_opt
     * class_bound =
     *  COLON field_type_sig_opt
     * inter_bound_list_opt = 
     *  * empty *
     *  | inter_bound_list
     * inter_bound_list = 
     *  inter_bound
     *  inter_bound_list inter_bound
     * inter_bound = 
     *  COLON field_type_sig
     * super_class_sig =
     *  class_type_sig
     * super_inter_sig_list_opt = 
     *  * empty *
     *  | super_inter_sig_list
     * super_inter_sig_list =
     *  super_inter_sig
     *  | super_inter_sig_list | super_inter_sig
     * super_inter_sig =
     *  class_type_sig
     * field_type_sig =
     *  class_type_sig
     *  | array_type_sig
     *  | type_var_sig
     * class_type_sig =
     *  L pack_spec_list_opt simple_class_type_sig 
     *      class_type_sig_suffix_list_opt SEMI_COLON
     * pack_spec_list_opt =
     *  * empty *
     *  | pack_spec_list
     * pack_spec_list =
     *  pack_spec
     *  | pack_spec_list pack_spec
     * pack_spec =
     *  ID SLASH
     * simple_class_type_sig = 
     *  ID type_args_opt
     * class_type_sig_suffix_list_opt =
     *  * empty *
     *  | class_type_sig_suffix_list
     * class_type_sig_suffix_list =
     *  class_type_sig_suffix
     *  | class_type_sig_suffix_list class_type_sig_suffix
     * class_type_sig_suffix =
     *  DOT simple_class_type_sig
     * type_var_sig =
     *  T ID SEMI_COLON
     * type_args =
     *  LEFT_ANGLE type_arg_list RIGHT_ANGLE
     * type_arg_list = 
     *  type_arg
     *  | type_arg_list type_arg
     * type_arg =
     *  wild_card_ind_opt field_type_sig
     *  | STAR
     * wild_card_ind_opt = 
     *  * empty *
     *  | wild_card_ind
     * wild_card_ind = 
     *  PLUS
     *  | MINUS
     * array_type_sig =
     *  LEFT_SQUARE type_sig
     * type_sig =
     *  field_type_sig
     *  | base_type
     * 
     * method_type_sig =
     *  formal_type_params_opt LEFT_BRACE type_sig_list_opt RIGHT_BRACE 
     *      return_type throws_sig_list_opt
     * return_type =
     *  type_sig
     *  | V
     * throws_sig_list_opt = 
     *  * empty *
     *  | throws_sig_list
     * throws_sig_list =
     *  throws_sig 
     *  | throws_sig_list throws_sig
     * throws_sig =
     *  HAT class_type_sig
     *  | HAT type_var_sig
     * 
     * base_type =
     *  B | C | D | F | I | J | S | Z 
     *  
     */
    
    Signature(DataInputStream in, int nameIndex, int length, ClassFile clazz) throws IOException{
        super(nameIndex, length);
        this.index = in.readUnsignedShort();
        this.cls = clazz;
    }
    
    // tokens
    private final char LEFT_ANGLE = '<';
    private final char RIGHT_ANGLE = '>';
    private final char COLON = ':';
    private final char L = 'L';
    private final char SEMI_COLON = ';';
    private final char SLASH = '/';
    private final char DOT = '.';
    private final char T = 'T';
    private final char STAR = '*';
    private final char PLUS = '+';
    private final char MINUS = '-';
    private final char LEFT_SQUARE = '[';
    private final char LEFT_BRACE = '(';
    private final char RIGHT_BRACE = ')';
    private final char V = 'V';
    private final char HAT = '^';
    private final char B = 'B';
    private final char C = 'C';
    private final char D = 'D';
    private final char F = 'F';
    private final char I = 'I';
    private final char J = 'J';
    private final char S = 'S';
    private final char Z = 'Z';

    
    class ClassSig {
        public ClassSig(List typeVars, Type superType, List interfaces){
            this.typeVars = typeVars;
            this. superType = superType;
            this.interfaces = interfaces;
        }
        protected List typeVars;   // list of intersection types
        public List typeVars(){
            return typeVars;
        }
        protected Type superType;
        public Type superType(){
            return superType;
        }
        protected List interfaces; // list of types 
        public List interfaces(){
            return interfaces;
        }
    }
    
    class MethodSig {
        public MethodSig(List typeVars, List formalTypes, Type returnType, List throwTypes){
            this.typeVars = typeVars;
            this.formalTypes = formalTypes;
            this.returnType = returnType;
            this.throwTypes = throwTypes;
        }
        protected List typeVars;     // list of intersection types
        public List typeVars(){
            return typeVars;
        }
        protected List formalTypes;  // list of types
        public List formalTypes(){
            return formalTypes;
        }
        protected Type returnType; 
        public Type returnType(){
            return returnType;
        }
        protected List throwTypes;   // list of types
        public List throwTypes(){
            return throwTypes;
        }
    }

    class FieldSig {
        protected Type type;
    }

    class Result {
        public Result(Object result, int pos){
            this.result = result;
            this.pos = pos;
        }
        protected int pos;
        protected Object result;
        public int pos(){
            return pos;
        }
        public Object result(){
            return result;
        }
    }
    
    public Result classSig(String value, int pos){
        char token = value.charAt(pos);
        
        Result fres = null;
        if (token == LEFT_ANGLE){
            fres = formalTypeParamList(value, ++pos);
            pos = fres.pos();
            typeVars = (List)fres.result();
        }
        Result sres = classTypeSig(value, pos);
        List superInterfaces = new ArrayList();
        pos = sres.pos();
        //pos++;
        //System.out.println("pos: "+pos+" value.length: "+value.length());
        //System.out.println("pos: "+(pos<value.length()));
        while (pos < value.length()){
            //System.out.println("pos: "+pos+"value: "+value+"value.length(): "+value.length());
            Result ires = classTypeSig(value, pos);
            pos = ires.pos();
            superInterfaces.add(ires.result());
            //pos++;
        }
        return new Result(new ClassSig(fres == null ? new ArrayList(): (List)fres.result(), (Type)sres.result(), superInterfaces), pos);
    }

    public Result formalTypeParamList(String value, int pos){
        List list = new ArrayList();
        char token = value.charAt(pos);
        while (token != RIGHT_ANGLE){
            Result fres = formalTypeParam(value, pos);
            list.add(fres.result());
            pos = fres.pos();
            token = value.charAt(pos);
        }
        pos++;
        return new Result(list, pos);
    }
    
    // ID classBound interfaceBoundList(opt)
    public Result formalTypeParam(String value, int pos){
        String id = "";
        char token = value.charAt(pos);
        while (token != COLON){
            id += token;
            pos++;
            token = value.charAt(pos);
        }
        //System.out.println("id: "+id);
        Result cres = classBound(value, pos);
        pos = cres.pos();
        Result ires = null;
        List bounds = new ArrayList();
        token = value.charAt(pos);
        while (token != RIGHT_ANGLE){
            if (value.charAt(pos) != COLON) break;
            ires = classBound(value, pos);
            pos = ires.pos();
            bounds.add(ires.result());
        }
        return new Result(ts.intersectionType(position, id, bounds), pos);
    }

    // : fieldTypeSig
    public Result classBound(String value, int pos){
        return fieldTypeSig(value, ++pos);
    }

    // classTypeSig L...;
    // typeVarSig T...;
    // arrayTypeSig [...;
    public Result fieldTypeSig(String value, int pos){
        Result res = null;
        char token = value.charAt(pos);
        //System.out.println("token start fieldTypeSig: "+token);
        switch(token){
            case L: { res = classTypeSig(value, pos); break; }
            case LEFT_SQUARE: { res = arrayTypeSig(value, pos); break; }
            case T: { res = typeVarSig(value, pos); break; }                  
            case COLON: { res = new Result(ts.Object(), pos); break; }
        }
        return res;
    }

    public Result classTypeSig(String value, int pos){
        char token = value.charAt(pos); // L
        String className = "";
        String id = "";
        Map classArgsMap = new HashMap();
        pos++;
        token = value.charAt(pos);   
        while (token != SEMI_COLON){
            switch(token){
                case SLASH: { // id is a package 
                              className += id;
                              className += "."; 
                              id = "";
                              pos++;
                              token = value.charAt(pos);
                              break; }
                case DOT: { // id is a className
                              className += id;
                              className += "$"; 
                              id = "";
                              pos++;
                              token = value.charAt(pos);
                              break; }
                case LEFT_ANGLE: { // id is a className
                                   Result tres = typeArgList(value, pos);
                                   pos = tres.pos();
                                   //System.out.println("putting: "+id+"res: "+tres.result());
                                   classArgsMap.put(id, tres.result());
                                   //System.out.println("after type arg: "+value.charAt(pos));
                                   token = value.charAt(pos);
                                   break;}          
                default: { id += token; 
                           pos++;
                           token = value.charAt(pos);
                           break; }          
            }
        }
        className += id;
        //System.out.println("className: "+className);
        ClassType ct = null;
        try {
            ct = cls.typeForName(ts, className);
        }
        catch (SemanticException e){
        }
        if (classArgsMap.containsKey(ct.name())){
            ParameterizedType pt = ts.parameterizedType((JL5ParsedClassType)ct);
            pt.typeArguments((List)classArgsMap.get(ct.name()));
        }
        ClassType current = ct;
        //ClassType outer = ct.outer();
        while (current.outer() != null) {
            if (classArgsMap.containsKey(current.outer().name())){
                ParameterizedType pt = ts.parameterizedType((JL5ParsedClassType)ct);
                pt.typeArguments((List)classArgsMap.get(current.outer().name()));
                ((JL5ParsedClassType)current).outer(pt);
            }
            if (current == current.outer()) break;
            current = current.outer();
            //System.out.println("current: "+current.name());
        }
        pos++;
        return new Result(ct, pos);
    }
    
    /*public Result packSpecList(String value, int pos){
        Result res = null;
        return res;    
    }

    public Result packSpec(String value, int pos){
        Result res = null;
        String id = "";
        char token;
        while ((token = value.charAt(pos++)) != SLASH){
            id += token;
        }
        res = new Result(id, ++pos);
        return res;
    }
    
    public Result simpleClassTypeSig(String value, int pos){
        String id = "";
        char token;
        while ((token = value.charAt(pos++)) != LEFT_ANGLE){
            id += token;
        }
        return new Result(id, ++pos);
    }
    

    public Result classTypeSigSuffix(String value, int pos){
        Result res = null;
        char token = value.charAt(pos);
        switch(token){
            case DOT: { res = simpleClassTypeSig(value, ++pos); }
        }
        return res;
    }*/

    public Result typeVarSig(String value, int pos){
        Result res = null;
        char token = value.charAt(pos);
        switch(token){
            case T: { String id = "";
                      pos++;
                      token = value.charAt(pos);    
                      while (token != SEMI_COLON){
                        id += token;
                        pos++;
                        token = value.charAt(pos);
                      }
                      pos++;
                      res = new Result(findTypeArg(id), pos);
                    }
        }
        return res;
    }

    public Result typeArgList(String value, int pos){
        List typeArgs = new ArrayList();
        char token = value.charAt(pos++);
        while (token != RIGHT_ANGLE){
            Result tres = typeArg(value, pos);
            pos = tres.pos();
            typeArgs.add(tres.result());
            token = value.charAt(pos);
        }
        pos++;
        return new Result(typeArgs, pos);
    }   
    
    public Result typeArg(String value, int pos){
        Result res = null;
        char token = value.charAt(pos);
        //System.out.println("token: "+token);
        switch(token){
            case PLUS: { Result fres = fieldTypeSig(value, ++pos);
                         /*Type t = null;
                         if (fres.result() instanceof String){
                           t = findTypeArg((String)fres.result());
                         }
                         else {
                            t = (Type)fres.result();
                         }*/
                         res = new Result(ts.anySubType((Type)fres.result()), fres.pos());
                         break;
                       }
            case MINUS: { Result fres = fieldTypeSig(value, ++pos);
                          /*Type t = null;
                          if (fres.result() instanceof String){
                            t = findTypeArg((String)fres.result());
                          }
                          else {
                            t = (Type)fres.result();
                          }*/
                          res = new Result(ts.anySuperType((Type)fres.result()), fres.pos());
                          break;
                   }
            case STAR: { pos++;
                         res = new Result(ts.anyType(), pos);
                         break;  
                       }
            case L:
            case LEFT_SQUARE:
            case T: { res = fieldTypeSig(value, pos);  
                      /*Type t = null;
                      if (fres.result() instanceof String){
                        t = findTypeArg((String)fres.result());
                      }
                      else {
                        t = (Type)fres.result();
                      }
                      res = new Result(t, fres.pos());*/
                      break;}
        }
        return res;
    }

    public Result arrayTypeSig(String value, int pos){
        Result res = null;
        char token = value.charAt(pos);
        //System.out.println("token start arrayTypeSig: "+token);
        switch(token){
            case LEFT_SQUARE : {pos++;
                                Result tres = typeSig(value, pos);
                                Type type = (Type)tres.result();
                                //System.out.println("tres pos: "+tres.pos());
                                res = new Result(ts.arrayOf(position, type, 1), tres.pos());
                                break;
                               }
        }
        return res;
    }
  
    public Result typeSigList(String value, int pos){
        List formals = new ArrayList();
        char token = value.charAt(pos);
        while (token != RIGHT_BRACE){
            Result ares = typeSig(value, pos);
            pos = ares.pos();
            formals.add(ares.result());
            token = value.charAt(pos);
        }
        pos++;
        return new Result(formals, pos);
    }
    
    public Result typeSig(String value, int pos){
        Result res = null;
        char token = value.charAt(pos);
        //System.out.println("token start typeSig: "+token);
        switch(token) {
            case L: 
            case LEFT_SQUARE:
            case T: { res = fieldTypeSig(value, pos);
                      //System.out.println("res: "+res);
                      break;
                    }
            case B:
            case C:
            case D:
            case F:
            case I:
            case J:
            case S:
            case Z: { res = baseType(value, pos);
                      break;
                    }
        }
        return res;
    }

    public Result methodTypeSig(String value, int pos){
        char token = value.charAt(pos);
        Result fres = null;
        if (token == LEFT_ANGLE){
            fres = formalTypeParamList(value, ++pos);
            pos = fres.pos();
            typeVars = (List)fres.result();
        }
        Result ares = null;
        if ((token = value.charAt(pos)) == LEFT_BRACE){
            ares = typeSigList(value, ++pos);
            pos = ares.pos();
        }
        Result rres = returnType(value, pos);
        pos = rres.pos();
        Type retType = null;
        Result tres = null;
        if ((pos < value.length()) && ((token = value.charAt(pos)) == HAT)){
            tres = throwsSigList(value, pos);
            pos = tres.pos();
        }
        return new Result(new MethodSig(fres == null ? new ArrayList() : (List)fres.result(), ares == null ? new ArrayList() : (List)ares.result(), (Type)rres.result(), tres == null ? new ArrayList() : (List)tres.result()), pos);
    }

    // returnType used in methodSig
    // starts pointing at char 
    // ends after (may be end of string
    public Result returnType(String value, int pos){
        Result res = null;
        char token = value.charAt(pos);
        switch(token){
            case L: 
            case LEFT_SQUARE:
            case T: 
            case B:
            case C:
            case D:
            case F:
            case I:
            case J:
            case S:
            case Z: { res = typeSig(value, pos);
                      break;
                    }
            case V: { pos++;
                      res = new Result(ts.Void(), pos); break;}
        }
        return res;
    }
    
    // list of throwSigs ^L...;^L...;^T...;
    // starts at ^ may advance beyond end of string
    // this is okay as throwsSigList is last part 
    // of methodTypeSig
    public Result throwsSigList(String value, int pos){
        List throwsList = new ArrayList();
        char token;
        while (pos < value.length()){
            Result tres = throwsSig(value, pos);
            pos = tres.pos();
            throwsList.add(tres.result());
        }
        return new Result(throwsList, pos);    
    }

    // throwsSig used in throwsSigList
    // ^L...; or ^T...;
    // starts at ^ and advances past ; 
    public Result throwsSig(String value, int pos){
        Result res = null;
        char token = value.charAt(pos);
        switch(token){
            case HAT: { token = value.charAt(++pos);
                        switch(token){
                            case L: { res = classTypeSig(value, pos); 
                                    }
                            case T: { res = typeVarSig(value, pos);                                                 }
                        }
                      }
        }
        return res;
    }
  
    // baseType used in typeSig one of:
    // B, C, D, F, I, J, S, Z
    // starts pointing to the char and ends
    // advanced to next char
    public Result baseType(String value, int pos){
        Result res = null;
        char token = value.charAt(pos);
        switch(token) {
            case B: { res = new Result(ts.Byte(), ++pos); 
                      break;
                    }
            case C: { res = new Result(ts.Char(), ++pos); 
                      break;
                    }
            case D: { res = new Result(ts.Double(), ++pos); 
                      break;
                    }
            case F: { res = new Result(ts.Float(), ++pos); 
                      break;
                    }
            case I: { res = new Result(ts.Int(), ++pos); 
                      break;
                    }
            case J: { res = new Result(ts.Long(), ++pos); 
                      break;
                    }
            case S: { res = new Result(ts.Short(), ++pos); 
                      break;
                    }
            case Z: { res = new Result(ts.Boolean(), ++pos); 
                      break;
                    }
        
        }
        return res;
    }
    
    public void parseClassSignature(TypeSystem ts, Position pos) throws IOException, SemanticException{
        this.ts = (JL5TypeSystem)ts;
        this.position = pos;
        String sigValue = (String)cls.constants()[index].value();
        //System.out.println("parsing class sig: "+sigValue);
        classSignature = (ClassSig)classSig(sigValue, 0).result();
    }

    public void parseMethodSignature(TypeSystem ts, Position pos, ClassType ct) throws IOException, SemanticException{
        this.ts = (JL5TypeSystem)ts;
        this.position = pos;
        this.curClass = ct;
        String sigValue = (String)cls.constants()[index].value();
        //System.out.println("parsing method sig: "+sigValue);
        methodSignature = (MethodSig)methodTypeSig(sigValue, 0).result();
    }

    public void parseFieldSignature(TypeSystem ts, Position pos) throws IOException, SemanticException{
        this.ts = (JL5TypeSystem)ts;
        this.position = pos;
        String sigValue = (String)cls.constants()[index].value();
        fieldSignature = (FieldSig)fieldTypeSig(sigValue, 0).result();
    }



    

        
    /* 
    public void parseSignature(TypeSystem ts, Position pos) throws IOException, SemanticException{
        Constant sigValue = cls.constants()[index];

        int i = 0;
        i = readTypeVars((String)sigValue.value(), i, ts, pos);
       
        i = readArgs((String)sigValue.value(), i, ts, pos);
    }

    public int readTypeVars(String sig, int i, TypeSystem ts, Position pos) throws SemanticException {
        if (sig.charAt(i) != '<') return i;
        int result = sig.indexOf(">",++i);
       
        typeVars = new ArrayList();
        String vars = sig.substring(i, result);
        result++;
       
        StringTokenizer varsTokens = new StringTokenizer(vars, ";");
        while (varsTokens.hasMoreTokens()){
            String nextVar = varsTokens.nextToken();
       
            StringTokenizer st = new StringTokenizer(nextVar, ":");
            ArrayList bounds = new ArrayList();
            
            String id = st.nextToken();
           
            while (st.hasMoreTokens()){
                // this is the next intersection bound
                String next = st.nextToken();
                next = next.substring(1);
                next = next.replace('/', '.');
                
                ClassType type = cls.typeForName(ts, next);
                bounds.add(type);
                
            }

            IntersectionType t = ((JL5TypeSystem)ts).intersectionType(pos, id, bounds);
            typeVars.add(t);
        }
        return result;
    }

    public int readArgs(String sig, int i, TypeSystem ts, Position pos) throws SemanticException {

        interfaceTypes = new ArrayList();
        
        String value = sig.substring(i);
       

        int k = 0;
        char j = 0;
        int count = 0;
      
        while (k < value.length()){
            String next = "";
            String vars = "";
            ArrayList args = new ArrayList();
            while (j != ';'){
                j = value.charAt(k);
                // handle type args
                if (j == '<'){
                    k++;
                    while (value.charAt(k) != '>'){
                        j = value.charAt(k);
                        vars += j;
                        k++;
                    }
                    StringTokenizer st = new StringTokenizer(vars, ";");
                    while (st.hasMoreTokens()){
                        String nextArg = st.nextToken();
                        Type argType;
                        if (nextArg.charAt(0) == 'T'){
                            argType = findTypeArg(nextArg, pos);
                        }
                        else {
                            nextArg = nextArg.substring(1);
                            nextArg = nextArg.replace('/', '.');
                            argType = cls.typeForName(ts, nextArg);
                        }
                        args.add(argType);
                    }
                    k++;
                }
                next += j;
                k++;
                
            }
            next = next.substring(1, next.length()-1);
            next = next.replace('/', '.');
            if (count == 0){
                if (!args.isEmpty()){
                    superType = ((JL5TypeSystem)ts).parameterizedType(((JL5ParsedClassType)cls.typeForName(ts, next)));
                    ((ParameterizedType)superType).typeArguments(args);
                }
                else {
                    superType = (JL5ParsedClassType)cls.typeForName(ts, next);
                }
            }
            j = 0;
        }
        return i;
    }*/

    private Type findTypeArg(String next){
        if (typeVars != null){
            for (Iterator it = typeVars.iterator(); it.hasNext(); ){
                IntersectionType iType = (IntersectionType)it.next();
                //System.out.println("finding type arg: "+iType.name());
                if (iType.name().equals(next)) return iType;
            }
        }
        if (curClass != null && curClass instanceof JL5ParsedClassType){
            if (((JL5ParsedClassType)curClass).typeVariables() != null){
                for (Iterator it = ((JL5ParsedClassType)curClass).typeVariables().iterator(); it.hasNext(); ){
                    IntersectionType iType = (IntersectionType)it.next();
                    if (iType.name().equals(next)) return iType;
                }
            }
        }
        return null;
    }
    
    
    /*public List typeVariables(){
        return typeVars;
    }*/

    public String toString(){
        return (String)cls.constants()[index].value();
    }
}
