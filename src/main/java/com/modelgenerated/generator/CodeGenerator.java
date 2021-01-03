/*
 * CodeGenerator.java
 *
 * Created on February 17, 2003, 5:47 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator;

import com.modelgenerated.modelmetadata.ObjectDescriptor;
/**
 *
 * @author  kevind
 */
public interface CodeGenerator {    
    public void setEventListener(GeneratorEventListener newEventListener);
    public void generate(String rootPath, ObjectDescriptor initObjectDescriptor);
}
