/*
 * ActionGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.strutsgenerator;

import com.modelgenerated.foundation.config.ConfigLocator;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.FieldDescriptor;
import com.modelgenerated.modelmetadata.ReferenceDescriptor;
import com.modelgenerated.modelmetadata.uimodel.ScreenColumnDescriptor;
import com.modelgenerated.modelmetadata.uimodel.ScreenDescriptor;
import com.modelgenerated.modelmetadata.uimodel.ScreenFieldDescriptor;
import com.modelgenerated.modelmetadata.uimodel.SelectData;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.generator.GeneratorConfig;
import com.modelgenerated.generator.java.ImportGenerator;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.FileUtil;
import com.modelgenerated.util.StringUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author  kevind
 */
public class ActionGenerator {
    protected GeneratorConfig generatorConfig;
    protected CodeBuffer code;
    protected ScreenDescriptor screenDescriptor;
    protected ObjectDescriptor objectDescriptor;
    protected ClassDescriptor controllerClass;
    protected String objectIdConstant;
    
    /** Creates a new instance of ActionGenerator */
    public ActionGenerator() {
    }
    
    public void generate(Model model, ScreenDescriptor initScreenDescriptor) {
        generatorConfig = (GeneratorConfig)ConfigLocator.findConfig(GeneratorConfig.CONFIG_NAME);
        Assert.check(generatorConfig != null, "generatorConfig != null");        

        code = new CodeBuffer();
        screenDescriptor = initScreenDescriptor;        

        objectDescriptor = model.findObject(screenDescriptor.getValueObjectClass());        
        
        controllerClass = new ClassDescriptor(screenDescriptor.getControllerClass());
        objectIdConstant = objectDescriptor.getValueObjectInterface().getClassName().toUpperCase() + "_ID"; 
        
        generateFileContent();
        
        Logger.debug(this, code.toString());
        
        String controllerClassName = screenDescriptor.getControllerClass();
        String relativeControllerFile = controllerClassName.replaceAll("\\.", "/");
        String fullyQualifiedName = generatorConfig.getControllerRoot() + "/" + relativeControllerFile + ".java";
        Logger.debug(this, "fullyQualifiedName: " + fullyQualifiedName);
        String packagePath = StringUtil.fullyQualifiedNameToPackage(fullyQualifiedName);
        Logger.debug(this, "packagePath: " + packagePath);
                
        File directory = new File(packagePath);
        boolean bCreated = directory.mkdirs();
        
        FileUtil.writeFile(fullyQualifiedName, code.toString());

    }

    
    private void generateFileContent() {
        Logger.debug(this, screenDescriptor);
        generateHeader();
        generateImports();     

        generateClass();
    }

    private void generateClass() {
        Logger.debug(this, screenDescriptor);

        ClassDescriptor controllerClass = new ClassDescriptor(screenDescriptor.getControllerClass());
        ClassDescriptor controllerBaseClass = new ClassDescriptor(screenDescriptor.getControllerBaseClass());
        code.addLine("public class " + controllerClass.getClassName() + " extends " + controllerBaseClass.getClassName() + " {");
        code.addLine("    public static final String " + objectIdConstant + " = \"" + objectIdConstant + "\";");
        code.addLine();

        String screenType = screenDescriptor.getScreenType();
        if ("update".equals(screenType)) {
            //generateView();     
            generateViewAdd();     
            generateViewUpdate();     
            generatePopulateFormData();
            generatePopulateList();
            generateUpdate();             
        } else if ("report".equals(screenType)) {          
            generateViewList();     
            generateUpdateList();             
            generateUpdateReport();
        }
        
        
        code.addLine();
        code.addLine("}");
    }
    
    protected void generateHeader() {
        String fqn = screenDescriptor.getControllerClass();
        code.addLine("/* " + StringUtil.fullyQualifiedNameToClass(fqn));
        code.addLine("*/");
        code.addLine();
        code.addLine("package " + controllerClass.getPackage() + ";");
        code.addLine();
    
    }
    
    protected void generateImports() {
        ImportGenerator importGenerator = new ImportGenerator();
        
        // this comes from objectDescriptor
        importGenerator.addImport(objectDescriptor.getValueObjectInterface().getFQN());
        importGenerator.addImport(objectDescriptor.getListInterface().getFQN());

        String  serviceClass = screenDescriptor.getService();
        importGenerator.addImport(serviceClass);
        
        String parentService = screenDescriptor.getParentService();
        if (!StringUtil.isEmpty(parentService) && !parentService.equals(serviceClass)) {
            importGenerator.addImport(parentService);
        }
        String parentAttribute = screenDescriptor.getParentAttribute();
        if (!StringUtil.isEmpty(parentAttribute)) {            
            FieldDescriptor fieldDescriptor = objectDescriptor.findField(parentAttribute);
            Assert.check(fieldDescriptor != null, "fieldDescriptor != null");
            ClassDescriptor parentClassDescriptor = fieldDescriptor.getClassDescriptor();
            Assert.check(parentClassDescriptor != null, "parentClassDescriptor != null");
            importGenerator.addImport(parentClassDescriptor.getFQN());
        }

        // TODO: Clean up this if statement. Shouldn't be needed.
        if ("update".equals(screenDescriptor.getScreenType())) { 
            Iterator i = screenDescriptor.getFieldList().iterator();
            while (i.hasNext()) {
                ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();        

                String type = screenFieldDescriptor.getType();

                if ("ChooseObject".equals(type)) { 
                    String service = screenFieldDescriptor.getService();
                    if (!StringUtil.isEmpty(service)) {
                        importGenerator.addImport(service);
                    }
                    String objectClass = screenFieldDescriptor.getObjectClass();
                    if (!StringUtil.isEmpty(objectClass)) {
                        importGenerator.addImport(objectClass);
                    }
                } else if (type.equals("List") && hasUpdateableColumns(screenFieldDescriptor) ) {
                    String fieldName = screenFieldDescriptor.getFieldName();
                    ReferenceDescriptor referenceDescriptor = objectDescriptor.findReference(fieldName);                

                    ClassDescriptor listClassDescriptor = referenceDescriptor.getClassDescriptor();
                    importGenerator.addImport(listClassDescriptor.getFQN());
                    ClassDescriptor targetClassDescriptor = referenceDescriptor.getTargetClass();
                    importGenerator.addImport(targetClassDescriptor.getFQN());
                    
                } else if (type.equals("Enum")) {
                    String enumClass = screenFieldDescriptor.getEnumClass();
                    if (!StringUtil.isEmpty(enumClass)) {
                        importGenerator.addImport(enumClass);
                    }
                }
                

            }
        } else if ("report".equals(screenDescriptor.getScreenType())) { 
            Iterator i = screenDescriptor.getFieldList().iterator();
            while (i.hasNext()) {
                ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();        

                // capture the types and services in the columns
                List updateableColumns = getUpdatableColumns(screenFieldDescriptor);
                Iterator j = updateableColumns.iterator();
                while (j.hasNext()) {
                    ScreenColumnDescriptor column = (ScreenColumnDescriptor)j.next();
                    
                    String service = column.getService();
                    if (!StringUtil.isEmpty(service)) {
                        importGenerator.addImport(service);
                    }
                    String objectClass = column.getObjectClass();
                    if (!StringUtil.isEmpty(objectClass)) {
                        importGenerator.addImport(objectClass);
                    }
                    String enumClass = column.getEnumClass();
                    if (!StringUtil.isEmpty(enumClass)) {
                        importGenerator.addImport(enumClass);
                    }
                }
            }
        }


        // probably always need these
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.UserContext");
        importGenerator.addImport("com.modelgenerated.foundation.logging.Logger");
        importGenerator.addImport("com.modelgenerated.foundation.service.ServiceLocator");
        importGenerator.addImport("com.modelgenerated.lookup.Lookup");
        importGenerator.addImport("com.modelgenerated.lookup.LookupData");
        importGenerator.addImport("com.modelgenerated.lookup.LookupDataList");
        importGenerator.addImport("com.modelgenerated.lookup.LookupService");
        importGenerator.addImport("com.modelgenerated.util.Assert");
        importGenerator.addImport("com.modelgenerated.util.DateUtil");
        importGenerator.addImport("com.modelgenerated.util.NumberUtil");
        importGenerator.addImport("com.modelgenerated.util.StringUtil");

        ClassDescriptor controllerBaseClass = new ClassDescriptor(screenDescriptor.getControllerBaseClass());
        importGenerator.addImport(controllerBaseClass.getFQN());

        if ("report".equals(screenDescriptor.getScreenType())) { 
            ScreenFieldDescriptor screenFieldDescriptor = getReportList();

            String searchCriteriaClass = screenFieldDescriptor.getSearchCriteriaClass();
            if (!StringUtil.isEmpty(searchCriteriaClass)) {
                importGenerator.addImport(searchCriteriaClass);
            }
        }
        
        importGenerator.addImport("java.io.IOException");
        importGenerator.addImport("java.util.Date");
        boolean hasUpdateableColumns = hasUpdateableColumns();
        if (hasUpdateableColumns) {
            importGenerator.addImport("java.util.Enumeration");            
            importGenerator.addImport("java.util.HashMap");            
            importGenerator.addImport("java.util.Iterator");            
        }
        importGenerator.addImport("java.util.Locale");
        if (hasUpdateableColumns) {
            importGenerator.addImport("java.util.Map");            
        }
        importGenerator.addImport("javax.servlet.RequestDispatcher");
        importGenerator.addImport("javax.servlet.ServletException");
        importGenerator.addImport("javax.servlet.http.HttpServletRequest");
        importGenerator.addImport("javax.servlet.http.HttpSession");
        importGenerator.addImport("javax.servlet.http.HttpServletResponse");
        importGenerator.addImport("org.apache.struts.action.Action");
        importGenerator.addImport("org.apache.struts.action.ActionErrors");
        importGenerator.addImport("org.apache.struts.action.ActionForm");
        importGenerator.addImport("org.apache.struts.action.ActionForward");
        importGenerator.addImport("org.apache.struts.action.ActionMapping");
        importGenerator.addImport("org.apache.struts.action.ActionServlet");
        importGenerator.addImport("org.apache.struts.action.ActionMessage");
        importGenerator.addImport("org.apache.struts.action.ActionMessages");
        importGenerator.addImport("org.apache.struts.action.DynaActionForm");
        importGenerator.addImport("org.apache.struts.actions.DispatchAction");
        importGenerator.addImport("org.apache.struts.util.MessageResources");
        
        code.add(importGenerator.getImports(controllerClass.getPackage()));
        code.addLine();
    }

    public void generateView() {
        
        code.addLine("    public ActionForward view(ActionMapping mapping,");
        code.addLine("				 ActionForm form,");
        code.addLine("				 HttpServletRequest request,");
        code.addLine("				 HttpServletResponse response)");
        code.addLine("        throws Exception {");
        code.addLine("        Logger.debug(this, \"CALL: view\");");

        code.addLine("        // Extract attributes we will need");
        code.addLine("        Locale locale = getLocale(request);");
        code.addLine("        MessageResources messages = getResources(request);");
        code.addLine();        
        code.addLine("        UserContext userContext = getUserContext(request);");
        code.addLine("        if (userContext == null) {");
        code.addLine("            return mapping.findForward(\"logon\");");
        code.addLine("        }");
        code.addLine();

        code.addLine("        HttpSession session = request.getSession();");
        code.addLine("        Assert.check(session != null, \"session != null\");");
        code.addLine();

        code.addLine("        String id = request.getParameter(\"Id\");");
        code.addLine("        if (StringUtil.isEmpty(id)) {");
        code.addLine("            id = (String)session.getAttribute(" + objectIdConstant + ");");
        code.addLine("            Assert.check(!StringUtil.isEmpty(id), \"should have found " + objectIdConstant + "\");");
        code.addLine("        }");
        code.addLine("        session.setAttribute(" + objectIdConstant + ", id);");
        code.addLine();
        
        code.addLine("        populateFormData(userContext, request, (DynaActionForm)form, id);");
        code.addLine("        populateLists(userContext, request);");
        code.addLine();
        
        code.addLine("        // Forward control to the specified success URI");
        code.addLine("        // return (new ActionForward(mapping.getInput()));");
        code.addLine("        return (new ActionForward(mapping.getInput()));");
        code.addLine("    }");
        code.addLine();
    }
    
    public void generateViewAdd() {
        code.addLine("    public ActionForward viewAdd(ActionMapping mapping,");
        code.addLine("                                 ActionForm form,");
        code.addLine("                                 HttpServletRequest request,");
        code.addLine("                                 HttpServletResponse response)");
        code.addLine("        throws Exception {");
        code.addLine("        Logger.debug(this, \"CALL: viewAdd\");");
        code.addLine();
        
        code.addLine("        // Extract attributes we will need");
        code.addLine("        Locale locale = getLocale(request);");
        code.addLine("        MessageResources messages = getResources(request);");
        code.addLine();

        code.addLine("        UserContext userContext = getUserContext(request);");
        code.addLine("        if (userContext == null) {");
        code.addLine("            return mapping.findForward(\"logon\");");
        code.addLine("        }");
        code.addLine();

        code.addLine("        HttpSession session = request.getSession();");
        code.addLine("        Assert.check(session != null, \"session != null\");");
        code.addLine();

        genenerateGetParentId();
        
        String name = objectDescriptor.getValueObjectInterface().getClassName();
        code.addLine("        session.setAttribute(\"pageTitle\", messages.getMessage(\"" + name.toLowerCase() + ".pagetitle.add\"));");
        code.addLine();

        code.addLine("        populateLists(userContext, request);");
        code.addLine("        // Forward control to the specified success URI");
        code.addLine("        // return (new ActionForward(mapping.getInput()));");
        code.addLine("        return (new ActionForward(mapping.getInput()));");
        code.addLine("    }");
        code.addLine();
    }
    
    public void generateViewUpdate() {
        code.addLine("    public ActionForward viewUpdate(ActionMapping mapping,");
        code.addLine("                                 ActionForm form,");
        code.addLine("                                 HttpServletRequest request,");
        code.addLine("                                 HttpServletResponse response)");
        code.addLine("        throws Exception {");
        code.addLine("        Logger.debug(this, \"CALL: viewUpdate\");");
        code.addLine();
        
        code.addLine("        // Extract attributes we will need");
        code.addLine("        Locale locale = getLocale(request);");
        code.addLine("        MessageResources messages = getResources(request);");
        code.addLine();

        code.addLine("        UserContext userContext = getUserContext(request);");
        code.addLine("        if (userContext == null) {");
        code.addLine("            return mapping.findForward(\"logon\");");
        code.addLine("        }");
        code.addLine();

        code.addLine("        HttpSession session = request.getSession();");
        code.addLine("        Assert.check(session != null, \"session != null\");");
        code.addLine();
        
        code.addLine("        String id = request.getParameter(\"Id\");");
        code.addLine("        if (StringUtil.isEmpty(id)) {"); 
        code.addLine("            id = (String)session.getAttribute(" + objectIdConstant + ");");
        code.addLine("            Assert.check(!StringUtil.isEmpty(id), \"should have found a " + objectIdConstant + "\");");
        code.addLine("        }");
        code.addLine("        session.setAttribute(" + objectIdConstant + " , id);");
        String name = objectDescriptor.getValueObjectInterface().getClassName();
        code.addLine("        session.setAttribute(\"pageTitle\", messages.getMessage(\"" + name.toLowerCase() + ".pagetitle.update\"));");
        code.addLine();

        code.addLine("        populateFormData(userContext, request, (DynaActionForm )form, id);");
        code.addLine("        populateLists(userContext, request);");
        code.addLine();
        
        code.addLine("        // Forward control to the specified success URI");
        code.addLine("        // return (new ActionForward(mapping.getInput()));");
        code.addLine("        return (new ActionForward(mapping.getInput()));");
        code.addLine("    }");
        code.addLine();
    }
    
    
    public void generatePopulateFormData() {
        code.addLine("    private void populateFormData(UserContext userContext, HttpServletRequest request, DynaActionForm form, String id) throws Exception {");
        code.addLine("        Assert.check(form != null, \"form != null\");");
        code.addLine("        Assert.check(id != null, \"id != null\");");
        code.addLine();
        code.addLine("        MessageResources messages = getResources(request);");
        code.addLine();
        

        ClassDescriptor serviceClass = new ClassDescriptor(screenDescriptor.getService());
        String serviceClassName = serviceClass.getClassName();
        String serviceVariable = serviceClass.getJavaVariableName();
        
        ClassDescriptor valueObjectClass = new ClassDescriptor(screenDescriptor.getValueObjectClass());
        String valueObjectClassName = valueObjectClass.getClassName();
        String valueObjectVariable = valueObjectClass.getJavaVariableName();
        
        // get the service
        code.addLine("        " + serviceClassName + " " + serviceVariable + " = (" + serviceClassName + ")ServiceLocator.findService(" + serviceClassName + ".class.getName());");
        code.addLine("        Assert.check(" + serviceVariable + " != null, \"" + serviceVariable + " != null\");");
        code.addLine();

        code.addLine("        " + valueObjectClassName + " " + valueObjectVariable + " = " + serviceVariable + "." + screenDescriptor.getFindMethod() + "(userContext, id);");
        code.addLine("        Assert.check(" + valueObjectVariable + " != null, \"" + valueObjectVariable + " != null\");");
        code.addLine();
        
        code.addLine("        form.set(\"id\", " + valueObjectVariable + ".getId().toString());");
        
        Iterator i = screenDescriptor.getFieldList().iterator();
        while (i.hasNext()) {
            ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();        
            
            String fieldName = screenFieldDescriptor.getFieldName();
            String fieldVariableName = StringUtil.convertToJavaVariableName(fieldName);
            String type = screenFieldDescriptor.getType();
            
            if (type.equals("Date")) {
                code.addLine("        form.set(\"" + fieldVariableName + "\", DateUtil.formatDate(" + valueObjectVariable + ".get" + fieldName + "(), messages.getMessage(\"misc.dateformat\")));");
            } else if (type.equals("Link")) {
                // do nothing
            } else if (type.equals("Double")) { 
                // todo: use numberUtil.format()
                code.addLine("        form.set(\"" + fieldVariableName + "\", \"\" + " + valueObjectVariable + ".get" + fieldName + "());");
            } else if ("ChooseObject".equals(type)) { 
                code.addLine("        form.set(\"" + fieldVariableName + "Id\", " + valueObjectVariable + ".get" + fieldName + "Id());");
            } else if ("int".equals(type)) {
                code.addLine("        form.set(\"" + fieldVariableName + "\", \"\" + " + valueObjectVariable + ".get" + fieldName + "());");
            } else if ("Integer".equals(type)) {
                code.addLine("        form.set(\"" + fieldVariableName + "\", \"\" + " + valueObjectVariable + ".get" + fieldName + "());");
            } else if ("Enum".equals(type)) { 
                code.addLine("        form.set(\"" + fieldVariableName + "\", " + valueObjectVariable + ".get" + fieldName + "Key());");
            } else { // String, text, Lookup, Integer, List
                code.addLine("        form.set(\"" + fieldVariableName + "\", " + valueObjectVariable + ".get" + fieldName + "());");
            }
        }

        code.addLine("    }");
        code.addLine();
    }
    
    
    public void generatePopulateList() {
        code.addLine("    private void populateLists(UserContext userContext, HttpServletRequest request) throws Exception {");

        code.addLine("        LookupService lookupService = (LookupService)ServiceLocator.findService(LookupService.class.getName());");
        code.addLine("        Assert.check(lookupService != null, \"lookupService != null\");");
        code.addLine();        
        
        Iterator i = screenDescriptor.getFieldList().iterator();
        while (i.hasNext()) {
            ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();        
         
            String type = screenFieldDescriptor.getType();
            if (type.equals("Lookup") ) {
                String lookupName = screenFieldDescriptor.getLookupName();
                String lookupVariable = screenFieldDescriptor.getLookupVariable();
            
                code.addLine("        Lookup " + lookupVariable + " = lookupService.findLookupByName(userContext, " + lookupName + ");");
                code.addLine("        request.setAttribute(\"" + lookupVariable + "\", " + lookupVariable + ".getLookupData());");
                code.addLine();        
            } else if (type.equals("Select") ) {
                SelectData selectData = screenFieldDescriptor.getSelectData();
                                
                // todo: refactor this to selectData
                ClassDescriptor service = new ClassDescriptor(selectData.getService());
                String serviceClass = service.getClassName();
                String serviceVariable = service.getJavaVariableName();
                code.addLine("        " + serviceClass + " " + serviceVariable + " = (" + serviceClass + ")ServiceLocator.findService(" + serviceClass + ".class.getName());");
                code.addLine("        Assert.check(" + serviceVariable + " != null, \"" + serviceVariable + " != null\");");
                code.addLine();        
                
                code.addLine("        request.setAttribute(\"" + selectData.getListName() + "\", " + serviceVariable + "." + selectData.getMethod() + "(userContext));");
                code.addLine();        
                
            } else if ("ChooseObject".equals(type)) { 
                ClassDescriptor service = new ClassDescriptor(screenFieldDescriptor.getService());
                String serviceClass = service.getClassName();
                String serviceVariable = service.getJavaVariableName();
                ClassDescriptor objectClass = new ClassDescriptor(screenFieldDescriptor.getObjectClass());
                
                code.addLine("        " + serviceClass + " " + serviceVariable + " = (" + serviceClass + ")ServiceLocator.findService(" + serviceClass + ".class.getName());");
                code.addLine("        Assert.check(" + serviceVariable + " != null, \"" + serviceVariable + " != null\");");
                code.addLine();        
                
                code.addLine("        request.setAttribute(\"" + objectClass.getJavaVariableName() + "List\", " + serviceVariable + "." + screenFieldDescriptor.getListMethod() + "(userContext));");
                code.addLine();        
            } else if ("Enum".equals(type)) {
                ClassDescriptor enumClass = new ClassDescriptor(screenFieldDescriptor.getEnumClass()); 

                code.addLine("        request.setAttribute(\"" + enumClass.getJavaVariableName() + "List\", " + enumClass.getClassName() + ".getValueDisplayPairs());");
            }
        }

        
        
        
        code.addLine("    }");
        code.addLine();
    }
    
    public void generateUpdate() {
        code.addLine("    public ActionForward update(ActionMapping mapping,");
        code.addLine("                                 ActionForm form,");
        code.addLine("                                 HttpServletRequest request,");
        code.addLine("                                 HttpServletResponse response)");
        code.addLine("        throws Exception {");
        code.addLine("        Logger.debug(this, \"CALL: update\");");
        code.addLine();
        
        code.addLine("        // Extract attributes we will need");
        code.addLine("        Locale locale = getLocale(request);");
        code.addLine("        MessageResources messages = getResources(request);");
        code.addLine("        ActionMessages errors = new ActionErrors();");
        code.addLine();

        code.addLine("        UserContext userContext = getUserContext(request);");
        code.addLine("        if (userContext == null) {");
        code.addLine("            return mapping.findForward(\"logon\");");
        code.addLine("        }");
        code.addLine();

        code.addLine("        HttpSession session = request.getSession();");
        code.addLine("        Assert.check(session != null, \"session != null\");");
        code.addLine();

        generateServiceLocationCalls();

        ClassDescriptor serviceClass = new ClassDescriptor(screenDescriptor.getService());
        String serviceClassName = serviceClass.getClassName();
        String serviceVariable = serviceClass.getJavaVariableName();
        
        ClassDescriptor valueObjectClass = new ClassDescriptor(screenDescriptor.getValueObjectClass());
        String valueObjectClassName = valueObjectClass.getClassName();
        String valueObjectVariable = valueObjectClass.getJavaVariableName();
        
        code.addLine("        String id = (String)((DynaActionForm)form).get(\"id\");");
        
        List updateableFields = getUpdateableFields();        
        Iterator i = updateableFields.iterator();
        //Iterator i = screenDescriptor.getFieldList().iterator();
        while (i.hasNext()) {
            ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();        
            
            String fieldName = screenFieldDescriptor.getFieldName();
            String fieldVariableName = StringUtil.convertToJavaVariableName(fieldName);
            String type = screenFieldDescriptor.getType();
            
            if (type.equals("String") || type.equals("Text") || type.equals("Lookup") || type.equals("RadioButtons") ) {
                code.addLine("        String " + fieldVariableName + " = (String)((DynaActionForm)form).get(\"" + fieldVariableName + "\");");
            } else if (type.equals("Date")) {
                String error = screenDescriptor.getResourcePrefix() + ".error." + fieldName.toLowerCase();
                code.addLine("        Date " + fieldVariableName + " = validateDate((DynaActionForm)form, \"" + fieldVariableName + "\", errors, \"" + error + "\", messages);");
            } else if (type.equals("Double")) {
                String error = screenDescriptor.getResourcePrefix() + ".error." + fieldName.toLowerCase() + "notnumeric";
                code.addLine("        Double " + fieldVariableName + " = validateDouble((DynaActionForm)form, \"" + fieldVariableName + "\", errors, \"" + error + "\");");
            } else if (type.equals("int") || type.equals("Integer")) {
                String error = screenDescriptor.getResourcePrefix() + ".error." + fieldName.toLowerCase() + "notnumeric";
                code.addLine("        Integer " + fieldVariableName + " = validateInteger((DynaActionForm)form, \"" + fieldVariableName + "\", errors, \"" + error + "\");");
            } else if (type.equals("Boolean") || type.equals("BooleanDropDown")  || type.equals("BooleanCheckbox")  ) {
                code.addLine("        Boolean " + fieldVariableName + " = (Boolean)((DynaActionForm)form).get(\"" + fieldVariableName + "\");");
            } else if (type.equals("List")) {
                // do nothing
            } else if (type.equals("Link")) {
                // do nothing
            } else if (type.equals("Select")) {
                code.addLine("        String " + fieldVariableName + " = (String)((DynaActionForm)form).get(\"" + fieldVariableName + "\");");                
            } else if ("ChooseObject".equals(type)) { 
                code.addLine("        String " + fieldVariableName + "Id = (String)((DynaActionForm)form).get(\"" + fieldVariableName + "Id\");");                
            }else if (type.equals("Enum")) {
                    code.addLine("        String " + fieldVariableName + " = (String)((DynaActionForm)form).get(\"" + fieldVariableName + "\");");
            } else {
                Assert.check(false, "unknown type");
            }
        }
        code.addLine();

        generateColumnUpdateMaps();

        
        code.addLine("        // Report any errors we have discovered back to the original form");
        code.addLine("        if (!errors.isEmpty()) {");
        code.addLine("            saveErrors(request, errors);");
        code.addLine("            populateLists(userContext, request);");
        code.addLine("            return (new ActionForward(mapping.getInput()));");
        code.addLine("        }");
        code.addLine();

        
        code.addLine("        ActionMessage actionMessage = null;");
        code.addLine("        " + valueObjectClassName + " " + valueObjectVariable + " = null;");
        code.addLine("        if (!StringUtil.isEmpty(id)) {");
        code.addLine("            // update ");
        code.addLine("            " + valueObjectVariable + " = " + serviceVariable + "." + screenDescriptor.getFindMethod() + "(userContext, id);");
        //code.addLine("            actionMessage = new ActionMessage(\"" + billing.message.updated", invoiceNumber);;
        code.addLine("        } else {");
        code.addLine("            // add ");
        code.addLine("            " + valueObjectVariable + " = " + serviceVariable + "." + screenDescriptor.getNewMethod() + "(userContext);");
        //code.addLine("            actionMessage = new ActionMessage(\"" + billing.message.added", invoiceNumber);;
        code.addLine("            Assert.check(" + valueObjectVariable + " != null, \"" + valueObjectVariable + " != null\");");        
        code.addLine("            session.setAttribute(" + objectIdConstant + ", " + valueObjectVariable + ".getId().toString());");
        genenerateSetParent();
        code.addLine("        }");
        code.addLine();

        i = updateableFields.iterator();
        //i = screenDescriptor.getFieldList().iterator();
        while (i.hasNext()) {
            ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();        
            
            String fieldName = screenFieldDescriptor.getFieldName();
            String fieldVariableName = StringUtil.convertToJavaVariableName(screenFieldDescriptor.getFieldName());
            FieldDescriptor fieldDescriptor = objectDescriptor.findField(fieldName);
            String type = screenFieldDescriptor.getType();
            
            if (type.equals("int")) {
                code.addLine("        if (" + fieldVariableName + " != null) {");
                code.addLine("            " + valueObjectVariable + ".set" + fieldDescriptor.getName() + "(" + fieldVariableName + ".intValue());");
                code.addLine("        }");
            } else if (type.equals("List")) {
                generatListUpdate(screenFieldDescriptor);
            } else if (type.equals("Link")) {
                // do nothing
            } else if (type.equals("Select")) {
            } else if ("ChooseObject".equals(type)) { 
                ClassDescriptor service = new ClassDescriptor(screenFieldDescriptor.getService());
                String chooseObjectServiceClass = service.getClassName();
                String chooseObjectServiceVariable = service.getJavaVariableName();
                ClassDescriptor objectClass = new ClassDescriptor(screenFieldDescriptor.getObjectClass());

                code.addLine("        " + objectClass.getClassName() + " " + fieldVariableName + " = null;");
                code.addLine("        if (!StringUtil.isEmpty(" + fieldVariableName + "Id)) {");
                code.addLine("            " + fieldVariableName + " = " + chooseObjectServiceVariable + ".find" + objectClass.getClassName() + "(userContext, " + fieldVariableName + "Id);");                 
                code.addLine("        }");
                code.addLine("        " + valueObjectVariable + ".set" + fieldDescriptor.getName() + "(" + fieldVariableName + ");");                
            } else {
                Assert.check(fieldDescriptor != null, "fieldDescriptor != null - fieldName:" + fieldName);
                code.addLine("        " + valueObjectVariable + ".set" + fieldDescriptor.getName() + "(" + fieldVariableName + ");");                
            }
        }
        code.addLine();
        
        code.addLine("        " + serviceVariable + "." + screenDescriptor.getSaveMethod() + "(userContext," + valueObjectVariable + ");");
        code.addLine();
        
        code.addLine("        // Save the messages.");
        code.addLine("        ActionMessages actionMessages = new ActionMessages();");
        code.addLine("        actionMessages.add(ActionMessages.GLOBAL_MESSAGE, actionMessage);");
        code.addLine("        saveMessages(request, actionMessages);");
        code.addLine();
        
        code.addLine("        return mapping.findForward(\"Success\");");
        code.addLine("    }");
        code.addLine();
    }

    private void genenerateGetParentId() {
        String parentAttribute = screenDescriptor.getParentAttribute();
        String parentVariableName = null;
        if (!StringUtil.isEmpty(parentAttribute)) {            
            FieldDescriptor fieldDescriptor = objectDescriptor.findField(parentAttribute);
            Assert.check(fieldDescriptor != null, "fieldDescriptor != null");
            ClassDescriptor parentClassDescriptor = fieldDescriptor.getClassDescriptor();
                
            String parentClassName = parentClassDescriptor.getClassName();
            parentVariableName = parentClassDescriptor.getJavaVariableName();
                
            String parentFindMethod = screenDescriptor.getParentFindMethod();
            String parentObjectIdConstant = screenDescriptor.getParentObjectId();
            String parentObjectIdName = parentVariableName + "Id";

            code.addLine("        String " + parentObjectIdConstant + " = request.getParameter(\"" + parentObjectIdConstant + "\");");                
            code.addLine("        ((DynaActionForm)form).set(\"" + parentObjectIdConstant + "\", " + parentObjectIdConstant + ");");                

            ClassDescriptor valueObjectClass = new ClassDescriptor(screenDescriptor.getValueObjectClass());
            String valueObjectClassName = valueObjectClass.getClassName();
            String valueObjectVariable = valueObjectClass.getJavaVariableName();


        }
    }


    private void genenerateSetParent() {
        ClassDescriptor serviceClass = new ClassDescriptor(screenDescriptor.getService());
        String serviceClassName = serviceClass.getClassName();
        String serviceVariable = serviceClass.getJavaVariableName();

        String parentAttribute = screenDescriptor.getParentAttribute();
        String parentVariableName = null;
        if (!StringUtil.isEmpty(parentAttribute)) {            
            code.addLine();
            FieldDescriptor fieldDescriptor = objectDescriptor.findField(parentAttribute);
            Assert.check(fieldDescriptor != null, "fieldDescriptor != null");
            ClassDescriptor parentClassDescriptor = fieldDescriptor.getClassDescriptor();
                
            String parentClassName = parentClassDescriptor.getClassName();
            parentVariableName = parentClassDescriptor.getJavaVariableName();
                
            String parentService = screenDescriptor.getParentService();
            String parentServiceClassVariable = null;
            if (parentService == null || parentService.equals(screenDescriptor.getService())) {
                parentServiceClassVariable = serviceVariable;
            } else {
                ClassDescriptor parentServiceClass = new ClassDescriptor(parentService);
                String parentServiceClassName = parentServiceClass.getClassName();
                String parentServiceVariable = parentServiceClass.getJavaVariableName();
                    
                code.addLine("            " + parentServiceClassName + " " + parentServiceVariable + " = (" + parentServiceClassName + ")ServiceLocator.findService(" + parentServiceClassName + ".class.getName());");
                code.addLine("            Assert.check(" + parentServiceVariable + " != null, \"" + parentServiceVariable + " != null\");");        
                code.addLine();        
            }
                
            String parentFindMethod = screenDescriptor.getParentFindMethod();
            String parentObjectIdConstant = screenDescriptor.getParentObjectId();
            String parentObjectIdName = parentVariableName + "Id";
            code.addLine("            String " + parentObjectIdName + " = (String)((DynaActionForm)form).get(\"" + parentObjectIdConstant + "\");");                
                
            code.addLine("            " + parentClassName + " " + parentVariableName + " = " + parentServiceClassVariable + "." + parentFindMethod + "(userContext, " + parentObjectIdName + ");");
            code.addLine("            Assert.check(" + parentVariableName + " != null, \"" + parentVariableName + " != null\");");

            ClassDescriptor valueObjectClass = new ClassDescriptor(screenDescriptor.getValueObjectClass());
            String valueObjectClassName = valueObjectClass.getClassName();
            String valueObjectVariable = valueObjectClass.getJavaVariableName();

            code.addLine("            " + valueObjectVariable + ".set" + parentAttribute + "(" + parentVariableName + ");");                

        }
    }


    
    public void generateUpdateReport() {
        code.addLine("    public ActionForward update(ActionMapping mapping,");
        code.addLine("                                 ActionForm form,");
        code.addLine("                                 HttpServletRequest request,");
        code.addLine("                                 HttpServletResponse response)");
        code.addLine("        throws Exception {");
        code.addLine("        Logger.debug(this, \"CALL: update\");");
        code.addLine();
        
        code.addLine("        // Extract attributes we will need");
        code.addLine("        Locale locale = getLocale(request);");
        code.addLine("        MessageResources messages = getResources(request);");
        code.addLine("        ActionMessages errors = new ActionErrors();");
        code.addLine();

        code.addLine("        UserContext userContext = getUserContext(request);");
        code.addLine("        if (userContext == null) {");
        code.addLine("            return mapping.findForward(\"logon\");");
        code.addLine("        }");
        code.addLine();

        code.addLine("        HttpSession session = request.getSession();");
        code.addLine("        Assert.check(session != null, \"session != null\");");
        code.addLine();

        generateServiceLocationCalls();

        ClassDescriptor serviceClass = new ClassDescriptor(screenDescriptor.getService());
        String serviceClassName = serviceClass.getClassName();
        String serviceVariable = serviceClass.getJavaVariableName();
        
        ClassDescriptor valueObjectClass = new ClassDescriptor(screenDescriptor.getValueObjectClass());
        String valueObjectClassName = valueObjectClass.getClassName();
        String valueObjectVariable = valueObjectClass.getJavaVariableName();
        
        generateColumnUpdateMaps();

        
        code.addLine("        // Report any errors we have discovered back to the original form");
        code.addLine("        if (!errors.isEmpty()) {");
        code.addLine("            saveErrors(request, errors);");
        code.addLine("            return (new ActionForward(mapping.getInput()));");
        code.addLine("        }");
        code.addLine();

        ScreenFieldDescriptor screenFieldDescriptor = getReportList();
        
        ClassDescriptor listClassDesriptor = objectDescriptor.getListInterface();
        String listClassName = listClassDesriptor.getClassName();
        String listVariable = listClassDesriptor.getJavaVariableName();
        String searchMethod = screenDescriptor.getSearchMethod();

        
        String searchCriteriaConstant = screenFieldDescriptor.getSearchCriteriaConstant();
        String specialSearchMethod = screenFieldDescriptor.getSearchMethod();
        if (!StringUtil.isEmpty(searchCriteriaConstant)) {        
            ClassDescriptor searchCriteriaClass = new ClassDescriptor(screenFieldDescriptor.getSearchCriteriaClass());
            String searchCriteriaClassName = searchCriteriaClass.getClassName();
            String searchCriteriaVariable = searchCriteriaClass.getJavaVariableName();
        
            code.addLine("        " + searchCriteriaClassName + " " + searchCriteriaVariable + " = (" + searchCriteriaClassName + ")session.getAttribute(" + searchCriteriaConstant + ");");
            code.addLine("        " + listClassName + " " + listVariable + " = " + serviceVariable + "." + searchMethod + "(userContext, " + searchCriteriaVariable + ");");
        } else {
            code.addLine("        " + listClassName + " " + listVariable + " = " + serviceVariable + "." + specialSearchMethod + "(userContext);");
        }
        code.addLine();

        // TODO: reuse  generatListUpdate(screenFieldDescriptor);
        if (hasUpdateableColumns()) {
            code.addLine("        Iterator i = " + listVariable + ".iterator();");
            code.addLine("        while (i.hasNext()) {");
            code.addLine("            " + valueObjectClassName + " " + valueObjectVariable + " = (" + valueObjectClassName + ")i.next();");
            code.addLine();

            List updateableColumns = getUpdatableColumns(screenFieldDescriptor);
            Iterator i = updateableColumns.iterator();
            while (i.hasNext()) {
                ScreenColumnDescriptor column = (ScreenColumnDescriptor)i.next();

                String subFieldName = column.getFieldName();
                String mapName = StringUtil.convertToJavaVariableName(subFieldName) + "Map";
                String subFieldVariable = valueObjectVariable + subFieldName;
                String type = column.getType();

                if ("BooleanRadioButtons".equals(type) || "BooleanDropDown".equals(type)) { 
                    code.addLine("            Boolean " + subFieldVariable + " = (Boolean)" + mapName + ".get(" + valueObjectVariable + ".getId().toString());");
                } else if ("Date".equals(type)) { 
                    code.addLine("            Date " + subFieldVariable + " = (Date)" + mapName + ".get(" + valueObjectVariable + ".getId().toString());");
                } else if ("Double".equals(type)) { 
                    code.addLine("            Double " + subFieldVariable + " = (Double)" + mapName + ".get(" + valueObjectVariable + ".getId().toString());");
                
                } else if ("ChooseObject".equals(type)) { 
                    String service = column.getService();
                    ClassDescriptor chooseObjectServiceClass = new ClassDescriptor(service);
                    ClassDescriptor objectClass = new ClassDescriptor(column.getObjectClass());
                
                    code.addLine("            String " + subFieldVariable + "Id = (String)" + mapName + ".get(" + valueObjectVariable + ".getId().toString());");
                    code.addLine("            " + objectClass.getClassName() + " " + subFieldVariable + " = null;");
                    code.addLine("            if (!StringUtil.isEmpty(" + subFieldVariable + "Id)) {");
                    code.addLine("                " + subFieldVariable + " = " + chooseObjectServiceClass.getJavaVariableName() + ".find" + objectClass.getClassName() + "(userContext, " + subFieldVariable + "Id);");                 
                    code.addLine("            }");
                } else {            
                    code.addLine("            String " + subFieldVariable + " = (String)" + mapName + ".get(" + valueObjectVariable + ".getId().toString());");
                }
                code.addLine("            " + valueObjectVariable + ".set" + subFieldName + "(" + subFieldVariable + ");");
                code.addLine();
            }
            code.addLine("        }");
        }
        
        code.addLine("        " + serviceVariable + "." + screenDescriptor.getSaveMethod() + "(userContext," + listVariable + ");");
        code.addLine();
        
        
        code.addLine("        return mapping.findForward(\"Success\");");
        code.addLine("    }");
        code.addLine();
    }


    private void generatListUpdate(ScreenFieldDescriptor screenFieldDescriptor) {
        if (!hasUpdateableColumns()) {
            return;
        }        
        Logger.debug(this, objectDescriptor);
        
        ClassDescriptor valueObjectClass = new ClassDescriptor(screenDescriptor.getValueObjectClass());
        Logger.debug(this, valueObjectClass);
        String valueObjectClassName = valueObjectClass.getClassName();
        String valueObjectVariable = valueObjectClass.getJavaVariableName();
        
        String fieldName = screenFieldDescriptor.getFieldName();
        Logger.debug(this, "generatListUpdate - fieldName: " + fieldName);

        String fieldVariableName = StringUtil.convertToJavaVariableName(screenFieldDescriptor.getFieldName());
        Logger.debug(this, "generatListUpdate - fieldVariableName: " + fieldVariableName);

        ReferenceDescriptor referenceDescriptor = objectDescriptor.findReference(fieldName);
        Logger.debug(this, referenceDescriptor);
        
        ClassDescriptor listClassDescriptor = referenceDescriptor.getClassDescriptor();
        String listClass = listClassDescriptor.getClassName();
        String listVariable = listClassDescriptor.getJavaVariableName();

        ClassDescriptor targetClassDescriptor = referenceDescriptor.getTargetClass();
        String targetClass = targetClassDescriptor.getClassName();
        String targetVariable = targetClassDescriptor.getJavaVariableName();

        
        code.addLine();
        code.addLine("        " + listClass + " " + listVariable + " = " + valueObjectVariable + ".get" + fieldName + "();");
        code.addLine("        Iterator i = " + listVariable + ".iterator();");
        code.addLine("        while (i.hasNext()) {");
        code.addLine("            " + targetClass + " " + targetVariable + " = (" + targetClass + ")i.next();");
        code.addLine();
        
        List updateableColumns = getUpdatableColumns(screenFieldDescriptor);
        Iterator i = updateableColumns.iterator();
        while (i.hasNext()) {
            ScreenColumnDescriptor column = (ScreenColumnDescriptor)i.next();
            
            String subFieldName = column.getFieldName();
            String mapName = StringUtil.convertToJavaVariableName(subFieldName) + "Map";
            String subFieldVariable = targetVariable + subFieldName;
            String type = column.getType();

            if ("BooleanRadioButtons".equals(type) || "BooleanDropDown".equals(type)) { 
                code.addLine("            Boolean " + subFieldVariable + " = (Boolean)" + mapName + ".get(" + targetVariable + ".getId().toString());");
            } else if ("Date".equals(type)) { 
                code.addLine("            Date " + subFieldVariable + " = (Date)" + mapName + ".get(" + targetVariable + ".getId().toString());");
            } else if ("Double".equals(type)) { 
                code.addLine("            Double " + subFieldVariable + " = (Double)" + mapName + ".get(" + targetVariable + ".getId().toString());");
            } else {
                code.addLine("            String " + subFieldVariable + " = (String)" + mapName + ".get(" + targetVariable + ".getId().toString());");
            }            
            code.addLine("            " + targetVariable + ".set" + subFieldName + "(" + subFieldVariable + ");");
            code.addLine();
        }
        code.addLine("        }");
    }
    
    
    
    private void generateColumnUpdateMaps() {
        if (!hasUpdateableColumns()) {
            return;
        }        
        
        List updatableColumns = getUpdatableColumns();
        // create maps
        Iterator i = updatableColumns.iterator();
        while (i.hasNext()) {
            ScreenColumnDescriptor column = (ScreenColumnDescriptor)i.next();
            String fieldName = column.getFieldName();
            String mapName = StringUtil.convertToJavaVariableName(fieldName) + "Map";
            code.addLine("        Map " + mapName + " = new HashMap();");
            
        }
        // create id prefixes
        i = updatableColumns.iterator();
        while (i.hasNext()) {
            ScreenColumnDescriptor column = (ScreenColumnDescriptor)i.next();
            String fieldName = column.getFieldName();
            String prefixName = fieldName.toUpperCase() + "_PREFIX";
            String prefixValue = null;
            if ("ChooseObject".equals(column.getType())) {
                prefixValue = fieldName.toLowerCase() + "id_";
            } else {
                prefixValue = fieldName.toLowerCase() + "_";
            }
            
            code.addLine("        String " + prefixName + " = \"" + prefixValue + "\";");            
        }

        code.addLine("        Enumeration enum = request.getParameterNames();");
        code.addLine("        while (enum.hasMoreElements()) {");
        code.addLine("            String paramName = (String)enum.nextElement();");
        code.addLine("            Logger.debug(this, \"paramName:\" + paramName);");
        
        boolean first = true;

        i = updatableColumns.iterator();
        while (i.hasNext()) {
            ScreenColumnDescriptor column = (ScreenColumnDescriptor)i.next();

            String fieldName = column.getFieldName();
            String mapName = StringUtil.convertToJavaVariableName(fieldName) + "Map";
            String prefixName = fieldName.toUpperCase() + "_PREFIX";
            String prefixValue = fieldName.toLowerCase() + "_";
            String type = column.getType();            
            String errorResource = screenDescriptor.getResourcePrefix() + ".error." + fieldName.toLowerCase();

            if (first) {
                code.addLine("            if (paramName.startsWith(" + prefixName + ")) {");
                first = false;
            } else {
                code.addLine("            } else if (paramName.startsWith(" + prefixName + ")) {");
            }
            
            if ("BooleanRadioButtons".equals(type) || "BooleanDropDown".equals(type)) { 
                code.addLine("                String idString = paramName.substring(" + prefixName + ".length());");
                code.addLine("                String value = request.getParameter(paramName);");
                code.addLine("                Boolean booleanValue = new Boolean(\"true\".equals(value));");
                code.addLine();
                code.addLine("                " + mapName + ".put(idString, booleanValue);");
            } else if ("Date".equals(type)) {
                code.addLine("                String idString = paramName.substring(" + prefixName + ".length());");
                code.addLine("                String dateString = request.getParameter(paramName);");
                code.addLine("                if (!StringUtil.isEmpty(dateString)) {");
                code.addLine("                    Date date = DateUtil.parseDate(dateString);");
                code.addLine("                    if (date == null) {");
                code.addLine("                        String dateFormat = messages.getMessage(\"misc.dateformat\");");
                code.addLine("                        Assert.check(dateFormat != null, \"dateFormat != null\");");
                code.addLine("                        errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(\"" + errorResource + "\", dateFormat));");
                code.addLine("                    }");

                code.addLine("                    " + mapName + ".put(idString, date);");
                code.addLine("                }");
            } else if ("Double".equals(type)) {
                code.addLine("                String idString = paramName.substring(" + prefixName + ".length());");
                code.addLine("                String doubleString = request.getParameter(paramName);");
                code.addLine("                if (!StringUtil.isEmpty(doubleString)) {");
                code.addLine("                    Double doubleValue = NumberUtil.parseDouble(doubleString);");
                code.addLine("                    if (doubleValue == null) {");
                code.addLine("                        errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(\"" + errorResource + "\"));");
                code.addLine("                    }");

                code.addLine("                    " + mapName + ".put(idString, doubleValue);");
                code.addLine("                }");
            } else {
                code.addLine("                String idString = paramName.substring(" + prefixName + ".length());");
                code.addLine("                String value = request.getParameter(paramName);");
                code.addLine();
                code.addLine("                " + mapName + ".put(idString, value);");
            }

            
        }
        code.addLine("            }");
        code.addLine("        }");
    }
    private List getUpdateableFields() {
        List updatableFields = new ArrayList();
        
        Iterator i = screenDescriptor.getFieldList().iterator();
        while (i.hasNext()) {
            ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();        
            
            if (screenFieldDescriptor.getSupportsUpdate()) {
                updatableFields.add(screenFieldDescriptor);            
            }            
        }
        return updatableFields;
    }
        
    private List getUpdatableColumns() {
        List updatableColumns = new ArrayList();
        
        Iterator i = screenDescriptor.getFieldList().iterator();
        while (i.hasNext()) {
            ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();        
            String type = screenFieldDescriptor.getType();
            
            if (type.equals("List")) {
                updatableColumns.addAll(getUpdatableColumns(screenFieldDescriptor));            
            }            
        }
        return updatableColumns;
    }
    private List getUpdatableColumns(ScreenFieldDescriptor screenFieldDescriptor) {
        List updatableColumns = new ArrayList();
        
        List columns = screenFieldDescriptor.getColumns();
        Iterator j = columns.iterator();
        while (j.hasNext()) {
            ScreenColumnDescriptor column = (ScreenColumnDescriptor)j.next();

            if (column.getSupportsUpdate()) {
                updatableColumns.add(column);
            }
        }
        return updatableColumns;
    }
    private boolean hasUpdateableColumns() {
        List updatableColumns = getUpdatableColumns();
        return updatableColumns.size() > 0;
    }
    private boolean hasUpdateableColumns(ScreenFieldDescriptor screenFieldDescriptor) {
        List updatableColumns = getUpdatableColumns(screenFieldDescriptor);
        return updatableColumns.size() > 0;
    }

    
    
    public void generateViewList() {
        
        code.addLine("    public ActionForward viewList(ActionMapping mapping,");
        code.addLine("				 ActionForm form,");
        code.addLine("				 HttpServletRequest request,");
        code.addLine("				 HttpServletResponse response)");
        code.addLine("        throws Exception {");
        code.addLine("        Logger.debug(this, \"CALL: view\");");

        code.addLine("        // Extract attributes we will need");
        code.addLine("        Locale locale = getLocale(request);");
        code.addLine("        MessageResources messages = getResources(request);");
        code.addLine();        
        code.addLine("        UserContext userContext = getUserContext(request);");
        code.addLine("        if (userContext == null) {");
        code.addLine("            return mapping.findForward(\"logon\");");
        code.addLine("        }");
        code.addLine();

        code.addLine("        HttpSession session = request.getSession();");
        code.addLine("        Assert.check(session != null, \"session != null\");");
        code.addLine();

        generateServiceLocationCalls();

        ClassDescriptor serviceClass = new ClassDescriptor(screenDescriptor.getService());
        String serviceClassName = serviceClass.getClassName();
        String serviceVariable = serviceClass.getJavaVariableName();
        
        ClassDescriptor valueObjectClass = new ClassDescriptor(screenDescriptor.getValueObjectClass());
        String valueObjectClassName = valueObjectClass.getClassName();
        String valueObjectVariable = valueObjectClass.getJavaVariableName();
        

        ScreenFieldDescriptor screenFieldDescriptor = getReportList();
        
        ClassDescriptor listClassDesriptor = objectDescriptor.getListInterface();
        String listClassName = listClassDesriptor.getClassName();
        String listVariable = listClassDesriptor.getJavaVariableName();
        String searchMethod = screenDescriptor.getSearchMethod();
        
        String searchCriteriaConstant = screenFieldDescriptor.getSearchCriteriaConstant();
        String specialSearchMethod = screenFieldDescriptor.getSearchMethod();
        if (!StringUtil.isEmpty(searchCriteriaConstant)) {        
            ClassDescriptor searchCriteriaClass = new ClassDescriptor(screenFieldDescriptor.getSearchCriteriaClass());
            String searchCriteriaClassName = searchCriteriaClass.getClassName();
            String searchCriteriaVariable = searchCriteriaClass.getJavaVariableName();
        
            code.addLine("        " + searchCriteriaClassName + " " + searchCriteriaVariable + " = (" + searchCriteriaClassName + ")session.getAttribute(" + searchCriteriaConstant + ");");
            code.addLine("        " + listClassName + " " + listVariable + " = " + serviceVariable + "." + searchMethod + "(userContext, " + searchCriteriaVariable + ");");
        } else {
            code.addLine("        " + listClassName + " " + listVariable + " = " + serviceVariable + "." + specialSearchMethod + "(userContext);");
        }
        code.addLine("        ((DynaActionForm)form).set(\"" + listVariable + "\", " + listVariable + ");");

        generateListLookups(screenFieldDescriptor);
        
        code.addLine();

        List updateableColumns = getUpdatableColumns(screenFieldDescriptor);
        Iterator i = updateableColumns.iterator();
        while (i.hasNext()) {
            ScreenColumnDescriptor column = (ScreenColumnDescriptor)i.next();

            String type = column.getType();
            if ("ChooseObject".equals(type)) {
                String fieldName = StringUtil.convertToJavaVariableName(column.getFieldName()); 
                String service = column.getService(); 
                ClassDescriptor choosObjectServiceClass = new ClassDescriptor(service); 
                String method = column.getListMethod(); 

                code.addLine("        request.setAttribute(\"" + fieldName + "List\", " + choosObjectServiceClass.getJavaVariableName() + "." + method + "(userContext));");
            } else if ("Enum".equals(type)) {
                ClassDescriptor enumClass = new ClassDescriptor(column.getEnumClass()); 

                code.addLine("        request.setAttribute(\"" + enumClass.getJavaVariableName() + "List\", " + enumClass.getClassName() + ".getValueDisplayPairs());");
            }
            
        }
            
        code.addLine();
        code.addLine("        return (new ActionForward(mapping.getInput()));");
        code.addLine("    }");
        code.addLine();
    }

    protected void generateServiceLocationCalls() {
        Set serviceSet = new HashSet();
        serviceSet.add(screenDescriptor.getService());
    
        // generate the other services
        Iterator i = screenDescriptor.getFieldList().iterator();
        while (i.hasNext()) {
            ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();        

            String type = screenFieldDescriptor.getType();            
            if ("ChooseObject".equals(type)) { 
                String service = screenFieldDescriptor.getService();
                if (!StringUtil.isEmpty(service)) {
                    serviceSet.add(service);
                }
            } else if ("List".equals(type)) { 
                // capture the types and services in the columns
                List updateableColumns = getUpdatableColumns(screenFieldDescriptor);
                Iterator j = updateableColumns.iterator();
                while (j.hasNext()) {
                    ScreenColumnDescriptor column = (ScreenColumnDescriptor)j.next();
                            
                    String service = column.getService();
                    if (!StringUtil.isEmpty(service)) {
                        serviceSet.add(service);
                    }
                }
            }
        }
    
        i = serviceSet.iterator();
        while (i.hasNext()) {
            String service = (String)i.next();
                
            ClassDescriptor serviceClass = new ClassDescriptor(service);
            String serviceClassName = serviceClass.getClassName();
            String serviceVariable = serviceClass.getJavaVariableName();
            
            // get the service
            code.addLine("        " + serviceClassName + " " + serviceVariable + " = (" + serviceClassName + ")ServiceLocator.findService(" + serviceClassName + ".class.getName());");
            code.addLine("        Assert.check(" + serviceVariable + " != null, \"" + serviceVariable + " != null\");");
        }    
        code.addLine();
    }


    
    private void generateListLookups(ScreenFieldDescriptor screenFieldDescriptor) {
        Map lookupMap = new HashMap();
        
        List columns = screenFieldDescriptor.getColumns();
        Iterator i = columns.iterator();
        while (i.hasNext()) {
            ScreenColumnDescriptor column = (ScreenColumnDescriptor)i.next();
         
            String type = column.getType();
            if ("Lookup".equals(type) ) {
                String lookupName = column.getLookupName();
                String lookupVariable = column.getLookupVariable();
                
                String previous = (String)lookupMap.put(lookupName, lookupVariable);
                
                if (previous != null && !previous.equals(lookupVariable)) {
                    // same lookup appears more than once but with different variable names.
                    Assert.check(false, "Same lookup appears more than once but with different variable names." + lookupName);
                }
            }
        }
        if (lookupMap.size() > 0) { 
            code.addLine("        LookupService lookupService = (LookupService)ServiceLocator.findService(LookupService.class.getName());");
            code.addLine("        Assert.check(lookupService != null, \"lookupService != null\");");
            code.addLine();        

            i = lookupMap.keySet().iterator();
            while (i.hasNext()) {
                String lookupName = (String)i.next();                        
                String lookupVariable = (String)lookupMap.get(lookupName);

                code.addLine("        Lookup " + lookupVariable + " = lookupService.findLookupByName(userContext, " + lookupName + ");");
                code.addLine("        request.setAttribute(\"" + lookupVariable + "\", " + lookupVariable + ".getLookupData());");
                code.addLine();        
            }
        }
    }
                
                

    
    private void generateUpdateList() {
    }

    
    private ScreenFieldDescriptor getReportList() {    
        List fieldList = screenDescriptor.getFieldList();
        Assert.check(fieldList != null, "fieldList != null");
        Assert.check(fieldList.size() == 1, "fieldList.size() == 1");
        
        return (ScreenFieldDescriptor)fieldList.get(0);
    }
    
    

    
}
