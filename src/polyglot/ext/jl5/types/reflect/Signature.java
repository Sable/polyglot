package polyglot.ext.jl5.types.reflect;

import java.io.*;
import polyglot.types.reflect.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;
import java.util.*;
import polyglot.util.*;

public class Signature extends Attribute {
    
    protected DataInputStream in;
    protected ClassFile cls;
    protected ArrayList typeVars;
    protected int index; 
    protected Type superType;
    protected ArrayList interfaceTypes;
    
    Signature(DataInputStream in, int nameIndex, int length, ClassFile clazz) throws IOException{
        super(nameIndex, length);
        this.index = in.readUnsignedShort();
        this.cls = clazz;
    }

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
    }

    private Type findTypeArg(String next, Position pos) throws SemanticException{
        next = next.substring(1);
        if (typeVars != null){
            for (Iterator it = typeVars.iterator(); it.hasNext(); ){
                IntersectionType iType = (IntersectionType)it.next();
                if (iType.name().equals(next)) return iType;
            }
        }
        throw new SemanticException("Could not parse class file", pos);
    }
    
    
    public List typeVariables(){
        return typeVars;
    }
}
