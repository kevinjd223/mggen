/*
 * EnumDescriptor.java
 *
 * Created on November 3, 2002, 9:09 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.enums;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.Model;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This object describes an Enum.
 *
 * It parses an XML file that looks likes this.
 * 
 * <ObjectDescriptor>
 *     <Implementation>CompanyImpl</Implementation>
 *     <Table>Company</Table>
 *     <BaseInterface>Party</BaseInterface>
 *     <Fields>
 *         <Field>
 *             <Name>Name</Name>
 *             <ColumnName>Name</ColumnName>
 *             <Type>String</Type>
 *             <Size>50</Size>
 *             <Nullable>no</Nullable>
 *         </Field>
 *         <Field>
 *             <Name>Name</Name>
 *             <ColumnName>Name</ColumnName>
 *             <Type>String</Type>
 *             <Size>50</Size>
 *             <Nullable>no</Nullable>
 *         </Field>        
 *     </Fields>   
 *     
 *     <Queries>
 * 
 *     </Queries>
 * </ObjectDescriptor>
 *
 *
 * @author  kevind
 */
public class EnumDescriptor implements Displayable {
    private Model model;
    
    private ClassDescriptor implementation;
    private String description = null;
    private List values = new ArrayList();

    /** Creates a new instance of ObjectDescriptor */
    public EnumDescriptor(Model model) {
        this.model = model;
    }
    
    public Model getModel() {
        return model;
    }

    public ClassDescriptor getImplementation() {
        return implementation;
    }
    public void setImplementation(ClassDescriptor newImplementation) {
        implementation = newImplementation;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String newDescription) {
        description = newDescription;
    }
    

    public List getValues() {
        return values;
    }
    public void setValues(List newValues) {
        values = newValues;
    }
    
    // Displayable interface methods
    public String display() {
        return display ("");
    }
    
    public String display(String objectDescription) {
        Map displayedObjects = new HashMap();
        return display (objectDescription, 0, 0, displayedObjects);
    }
    
    public String display(String objectDescription, int level, int maxLevels, Map displayedObjects) {
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("ObjectDescriptor", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        displayBuffer.addLine(level+1, "implementation: " + implementation); 
        displayBuffer.addLine(level+1, "description: " + description); 
    
        Iterator i = values.iterator();
        while (i.hasNext()) {
            EnumValueDescriptor enumValueDescriptor = (EnumValueDescriptor)i.next();
            displayBuffer.append(enumValueDescriptor.display("", level+1, maxLevels, displayedObjects));
        }
        
        
        return displayBuffer.toString();
        
    }
    
}
