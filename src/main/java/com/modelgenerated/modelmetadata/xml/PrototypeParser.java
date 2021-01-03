/* PrototypeParser.java
 * Created on Dec 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * Copyright 2002-2005 Kevin Delargy.
 */
 
package com.modelgenerated.modelmetadata.xml;


import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.Parameter;
import com.modelgenerated.modelmetadata.Prototype;
import com.modelgenerated.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * Parses a prototype string and returns a Prototype object. 
 */
public class PrototypeParser {
    String codeBuffer;
    int pointer;
    
    public Prototype parse(String input) {
        codeBuffer = input;
        pointer = 0;

        Prototype prototype = new Prototype();
        List<Parameter> parameterList = prototype.getParameters();
        List<ClassDescriptor> exceptionList = prototype.getExceptionList();
        
        Token tok = getToken();         
        if (tok.type == TokenTypeEnum.PUBLIC) {
            tok = getToken();         
        }
        
        if (tok.type != TokenTypeEnum.TEXT) { return null; }
        String className = getFullyQualifiedClass(tok.value);
        prototype.setReturnType(new ClassDescriptor(className));
        
        tok = getToken();
        if (tok.type != TokenTypeEnum.TEXT) { return null; }
        
        prototype.setMethodName(tok.value);

        tok = getToken();
        if (tok.type != TokenTypeEnum.OPENPAREN) { return null; }
        
        tok = getToken();
        while (tok.type != TokenTypeEnum.CLOSEPAREN) {
            Parameter parameter = new Parameter();

            if (tok.type == TokenTypeEnum.COMMA) { 
                tok = getToken();
            }
            
            if (tok.type != TokenTypeEnum.TEXT) { return null; }
            className = getFullyQualifiedClass(tok.value);
            parameter.setType(new ClassDescriptor(className));            

            tok = getToken();
            if (tok.type == TokenTypeEnum.ARRAYBRACKETS) {
	            parameter.setArray(true);
                tok = getToken();
        	}
            
            if (tok.type != TokenTypeEnum.TEXT) { return null; }
            parameter.setName(tok.value);
            
            parameterList.add(parameter);                        

            tok = getToken();            
        } 
        
        tok = getToken();         
        if (tok.type == TokenTypeEnum.THROWS) {
            tok = getToken();         
            while (tok.type != TokenTypeEnum.EOL) {
                if (tok.type == TokenTypeEnum.COMMA) { 
                    tok = getToken();
                }
                
                if (tok.type != TokenTypeEnum.TEXT) { return null; }
                
                exceptionList.add(new ClassDescriptor(tok.value));                        

                tok = getToken();            
            } 
        }
        
        return prototype;
    }
    
    /*
     * Returns the next token.
     * A token has a type and a value. 
     * Skips whitespace to get to the next token.  
     */
    protected Token getToken() {
        Token tok = new Token(); 

        // advance past whitespace
        Pattern whitespacePattern = Pattern.compile("[\\s]*");
        Matcher matcher = whitespacePattern.matcher(codeBuffer);
        if (matcher.lookingAt()) {
            codeBuffer = codeBuffer.substring(matcher.end());
            Logger.debug(this, "new codeBuffer" + codeBuffer); 
        }
        
        if (pointer >= codeBuffer.length()) {
            tok.type = TokenTypeEnum.EOL;
            return tok;             
        }
        
        String nextChar = codeBuffer.substring(pointer, 1);
        if (",".equals(nextChar)) {
            tok.type = TokenTypeEnum.COMMA;
            codeBuffer = codeBuffer.substring(1);
            return tok;             
        }
        if ("(".equals(nextChar)) {
            tok.type = TokenTypeEnum.OPENPAREN;
            codeBuffer = codeBuffer.substring(1);
            return tok;             
        }
        if (")".equals(nextChar)) {
            tok.type = TokenTypeEnum.CLOSEPAREN;
            codeBuffer = codeBuffer.substring(1);
            return tok;             
        }
    	if ("[".equals(nextChar)) { 
	        String brackets = codeBuffer.substring(pointer, 2);
	        Assert.check("[]".equals(brackets), "brackets should equal [] but is " + brackets);
	        tok.type = TokenTypeEnum.ARRAYBRACKETS;
            codeBuffer = codeBuffer.substring(2);
	        return tok;
    	}
        
        Pattern patern = Pattern.compile("[a-zA-Z][a-zA-Z0-9\\x2e]*");
        matcher = patern.matcher(codeBuffer);
        String textValue = null;
        if (matcher.lookingAt()) {
            textValue = codeBuffer.substring(0, matcher.end());
            codeBuffer = codeBuffer.substring(matcher.end());
        }

        if (TokenTypeEnum.PUBLIC.toString().equals(textValue)) { 
            tok.type = TokenTypeEnum.PUBLIC;
            return tok;
        } else if (TokenTypeEnum.THROWS.toString().equals(textValue)) { 
            tok.type = TokenTypeEnum.THROWS;
            return tok;
        } else {
            tok.type = TokenTypeEnum.TEXT;
            tok.value = textValue;            
            return tok;
        }
        //Logger.debug(this, "tokString.length" + tokString.length);            
        
        //return tok;
        
    }
    
    protected String getFullyQualifiedClass(String className) {
        // return fully qualified name for classes in java.lang
        if ("Boolean".equals(className)) {
            return "java.lang.Boolean"; 
        } else if ("Double".equals(className)) {
            return java.lang.Double.class.getCanonicalName(); 
        } else if ("Integer".equals(className)) {
            return "java.lang.Integer"; 
        } else if ("String".equals(className)) {
            return "java.lang.String"; 
        } else if ("UserContext".equals(className)) {            
            return "com.modelgenerated.foundation.dataaccess.UserContext";
        }        
        
        return className;        
    }
    
    // TODO: this should be a real java enum. 
    static class TokenTypeEnum {
        public static final TokenTypeEnum TEXT = new TokenTypeEnum("text");
        public static final TokenTypeEnum PUBLIC = new TokenTypeEnum("public");
        public static final TokenTypeEnum OPENPAREN = new TokenTypeEnum("(");
        public static final TokenTypeEnum CLOSEPAREN = new TokenTypeEnum(")");
        public static final TokenTypeEnum COMMA = new TokenTypeEnum(",");
        public static final TokenTypeEnum SEMICOLON = new TokenTypeEnum(";");
        public static final TokenTypeEnum EOL = new TokenTypeEnum("eol");
        public static final TokenTypeEnum ARRAYBRACKETS = new TokenTypeEnum("[]");
        public static final TokenTypeEnum THROWS = new TokenTypeEnum("throws");

        private static TokenTypeEnum[] list = {TEXT, PUBLIC, OPENPAREN, CLOSEPAREN, COMMA, SEMICOLON, EOL, ARRAYBRACKETS, THROWS};
        private String value;
    
        /** Creates a new instance of TokenTypeEnum */
        private TokenTypeEnum(String initValue) {
            value = initValue;        
        }
    
        public static TokenTypeEnum getTokenType(String value) {
            for (int i = 0; i < list.length; i++) {
                if (list[i].value.equals(value)) {
                    return (TokenTypeEnum)list[i];
                }            
            }
            return null;
        }
    
        public String toString() {
            return value;
        }
    }   
    class Token {
        TokenTypeEnum type;
        String value;
        
        public String toString() {
            return "Token:" + type + "; '" + value + "'";
        }
    }   
}
