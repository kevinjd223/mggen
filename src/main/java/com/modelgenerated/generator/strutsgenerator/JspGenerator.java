/*
 * JspGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.strutsgenerator;

import com.modelgenerated.foundation.config.ConfigLocator;
import com.modelgenerated.modelmetadata.FieldDescriptor;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.uimodel.ChoiceDescriptor;
import com.modelgenerated.modelmetadata.uimodel.SelectData;
import com.modelgenerated.modelmetadata.uimodel.ScreenColumnDescriptor;
import com.modelgenerated.modelmetadata.uimodel.ScreenDescriptor;
import com.modelgenerated.modelmetadata.uimodel.ScreenFieldDescriptor;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.generator.GeneratorConfig;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.FileUtil;
import com.modelgenerated.util.StringUtil;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author  kevind
 */
public class JspGenerator {
    protected Model model;
    protected GeneratorConfig generatorConfig;
    protected CodeBuffer code;
    protected ScreenDescriptor screenDescriptor;
    
    /** Creates a new instance of ValueObjectGenerator */
    public JspGenerator() {
    }
    
    public void generate(Model model, ScreenDescriptor initScreenDescriptor) {
        Assert.check(model != null, "model != null");        
        Assert.check(initScreenDescriptor != null, "initScreenDescriptor != null");        
        
        this.model = model;
        
        generatorConfig = (GeneratorConfig)ConfigLocator.findConfig(GeneratorConfig.CONFIG_NAME);
        Assert.check(generatorConfig != null, "generatorConfig != null");        

        screenDescriptor = initScreenDescriptor;                        
        
        if (!StringUtil.isEmpty(screenDescriptor.getJspAddFile())) {
            code = new CodeBuffer();
            generateAddFileContent();        
            writeFile(screenDescriptor.getJspAddFile());
        }
        if (!StringUtil.isEmpty(screenDescriptor.getJspUpdateFile())) {
            code = new CodeBuffer();
            generateUpdateFileContent();        
            writeFile(screenDescriptor.getJspUpdateFile());
        }
    }

    private void writeFile(String relativeFile) {    
        
        Logger.debug(this, code.toString());
        
        String fullyQualifiedName = generatorConfig.getJspRoot() + "/" + relativeFile;
        Logger.debug(this, "fullyQualifiedName: " + fullyQualifiedName);
        String packagePath = fullyQualifiedNameToPackage(fullyQualifiedName);
        Logger.debug(this, "packagePath: " + packagePath);
                
        File directory = new File(packagePath);
        boolean bCreated = directory.mkdirs();
        
        FileUtil.writeFile(fullyQualifiedName, code.toString());
    }

    
    private void generateAddFileContent() {
        Logger.debug(this, screenDescriptor);
        generateTlds();
        generateScript();
        generateForm("add");        
    }

    private void generateUpdateFileContent() {
        Logger.debug(this, screenDescriptor);
        generateTlds();
        generateScript();
        generateForm("update");        
    }

    
    protected void generateTlds() {
        code.addLine("<%@page contentType=\"text/html\"%>");
        code.addLine("<%@ taglib uri=\"/WEB-INF/struts-bean.tld\" prefix=\"bean\" %>");
        code.addLine("<%@ taglib uri=\"/WEB-INF/struts-html.tld\" prefix=\"html\" %>");
        code.addLine("<%@ taglib uri=\"/WEB-INF/struts-logic.tld\" prefix=\"logic\" %>");
        code.addLine("<%@ taglib uri=\"/WEB-INF/modelgenerated.tld\" prefix=\"modelgenerated\" %>");
        code.addLine();
        code.addLine("<%@ taglib uri=\"/WEB-INF/struts-bean-el.tld\" prefix=\"beanel\" %>");
        code.addLine("<%@ taglib uri=\"/WEB-INF/struts-html-el.tld\" prefix=\"htmlel\" %>");
        code.addLine("<%@ taglib uri=\"/WEB-INF/struts-logic-el.tld\" prefix=\"logicel\" %>");
        code.addLine("<%@ taglib uri=\"/WEB-INF/c.tld\" prefix=\"c\" %>");
        code.addLine("<%@ taglib uri=\"/WEB-INF/fmt.tld\" prefix=\"fmt\" %>");

        code.addLine();

        // TODO: make conditional on need for date
        String jspFile = screenDescriptor.getJspUpdateFile();
        if (StringUtil.matchCount(jspFile, '/') == 1) {
            code.addLine("<%@include  file=\"../include/date.jsp\" %>");
        } else if (StringUtil.matchCount(jspFile, '/') == 2) {
            code.addLine("<%@include  file=\"../../include/date.jsp\" %>");
        } else {
            code.addLine("<%@include  file=\"../include/date.jsp\" %>");
        }
        
        
        code.addLine();
    
    }
    
    protected void generateScript() {
        code.addLine("<script type=\"text/javascript\">");

        String form = screenDescriptor.getFormDataName();

        code.addLine("function go(action) {"); 
        code.addLine("  document." + form + ".submitLink.value=\"\";");
        code.addLine("  document." + form + ".action.value=action;");
        code.addLine("  return true;");
        code.addLine("}");

        code.addLine("function submitLink(link) {"); 
        code.addLine("  document." + form + ".submitLink.value=link;");
        code.addLine("  document." + form + ".action.value=\"\";");
        code.addLine("  document." + form + ".submit();");      
        code.addLine("}");

        code.addLine("</script>");
    }
    
    
    protected void generateHeader(String title) {
        String resourcePrefix = screenDescriptor.getResourcePrefix();
        String screenType = screenDescriptor.getScreenType();
        String pageTitleResource = resourcePrefix + ".pagetitle." + screenType;

        code.addLine("<div class='pagetitle'><bean:message key='" + pageTitleResource + "'/></div>");
        code.addLine("<br>");
        code.addLine();
        code.addLine("<div class='errortext'><html:errors/></div>");
        code.addLine();
        code.addLine("<logic:messagesPresent message='true'>");
        code.addLine("<UL>");
        code.addLine("<html:messages id='message' message='true'>");
        code.addLine(" <LI><bean:write name='message'/></LI>");
        code.addLine("</html:messages>");
        code.addLine("</UL>");
        code.addLine("</logic:messagesPresent>");
        code.addLine();
    }
    
    protected void generateForm(String formType) {
        generateHeader(formType);

        String action = screenDescriptor.getActionName();        
        
        code.addLine("<html:form action='" + action + "?method=" + formType + "'>");
        code.addLine();

        code.addLine("<html:hidden property=\"submitLink\"/>");
        code.addLine("<html:hidden property=\"action\"/>");
        code.addLine("<html:hidden property=\"id\"/>");
        if (!StringUtil.isEmpty(screenDescriptor.getParentObjectId())) {            
            String parentObjectIdConstant = screenDescriptor.getParentObjectId();
            code.addLine("<html:hidden property=\"" + parentObjectIdConstant + "\"/>");                
        }
        code.addLine();
        
        code.addLine("<table align='left' border='0' width='100%'>");
        code.addLine();

        generateInputFields();
        
        code.addLine("<tr>");
        code.addLine("<td align='left'>");
        code.addLine("<input type='submit' value='<bean:message key=\"misc.save\"/>'/>");
        code.addLine("</td>");
        code.addLine("<td >");
        String closeLink = screenDescriptor.getCloseLink();
        if (!StringUtil.isEmpty(closeLink)) { 
            code.addLine("<html:link page='/" + closeLink + "'><bean:message key='misc.close'/></html:link>");
        }
        code.addLine("</td>");
        code.addLine("</tr>");
        

        code.addLine("</table>");
        code.addLine("</html:form>");
    }


    protected void generateInputFields() {
        
        Iterator i = screenDescriptor.getFieldList().iterator();
        while (i.hasNext()) {
            ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();
            
            generateInputField(screenFieldDescriptor);
        }        
    }
    
    protected void generateInputField(ScreenFieldDescriptor screenFieldDescriptor) {
        ObjectDescriptor objectDescriptor = model.findObject(screenDescriptor.getValueObjectClass());                
        if (objectDescriptor == null) {
            throw new RuntimeException("Could not find value object: " + screenDescriptor.getValueObjectClass());
        }

        
        String form = screenDescriptor.getFormDataName();
        String type = screenFieldDescriptor.getType();
        
        String fieldName = screenFieldDescriptor.getFieldName();
        String fieldVariableName = null;
        FieldDescriptor fieldDescriptor = null;
        if (fieldName != null) { 
            fieldVariableName = convertToJavaVariableName(fieldName);
            fieldDescriptor = objectDescriptor.findField(fieldName);
        }
        String objectField = null;
        if (fieldDescriptor != null) { 
            objectField = fieldDescriptor.getObjectField();
        }
        String link = screenFieldDescriptor.getLink();
        boolean supportsUpdate = screenFieldDescriptor.getSupportsUpdate();
        
        Logger.debug(this, "screenDescriptor.getResourcePrefix()" + screenDescriptor.getResourcePrefix());
        Logger.debug(this, "fieldName" + fieldName);
        
        // TODO: refactor out the <tr ...><td ...>        
        if (type.equals("String")) { 
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            code.addLine("<tr>");
            drawPrompt(prompt);
            code.addLine("<td align='left'>");
            if (supportsUpdate) {
                code.addLine("<modelgenerated:text name='" + form + "' property='" + fieldVariableName + "' objectField='" + objectField + "' styleClass='textentry'/>");
            } else {
                code.addLine("<c:out value='${" + form + ".map." + fieldVariableName + "}'/>");
            }
            code.addLine("</td>");
            code.addLine("</tr>");
        } else if (type.equals("Text")) { 
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            code.addLine("<tr>");
            drawPrompt(prompt);
            code.addLine("<td align='left' valign='top'>");
            code.addLine("<html:textarea property='" + fieldVariableName + "' rows='5' cols='60'/>");
            code.addLine("</td>");
            code.addLine("</tr>");
        } else  if (type.equals("Date")) { 
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            code.addLine("<tr>");
            drawPrompt(prompt);
            code.addLine("<td align='left'>");
            code.addLine("<html:text property='" + fieldVariableName + "' size='16' maxlength='16' styleClass='dateentry' styleId='" + fieldVariableName + "'/>");
            code.addLine("<input type='button' id='" + fieldVariableName + "_button' value='date...' onclick=\"showCalendar('" + fieldVariableName + "', 2004, 2010, event); return false;\"/>");
            code.addLine("</td>");
            code.addLine("</tr>");
        } else  if (type.equals("Double")) { 
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            code.addLine("<tr>");
            drawPrompt(prompt);
            code.addLine("<td align='left'>");
            if (supportsUpdate) {
                code.addLine("<html:text property='" + fieldVariableName + "' size='16' maxlength='16' styleClass='textentry'/>");
            } else {
                code.addLine("<c:out value='${" + form + ".map." + fieldVariableName + "}'/>");
            }
            code.addLine("</td>");
            code.addLine("</tr>");
        } else  if (type.equals("int") || type.equals("Integer")) { 
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            code.addLine("<tr>");
            drawPrompt(prompt);
            code.addLine("<td align='left'>");
            if (supportsUpdate) {
                code.addLine("<html:text property='" + fieldVariableName + "' size='16' maxlength='16' styleClass='textentry'/>");
            } else {
                code.addLine("<c:out value='${" + form + ".map." + fieldVariableName + "}'/>");
            }
            code.addLine("</td>");
            code.addLine("</tr>");
        } else  if (type.equals("Boolean")) { 
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            code.addLine("<tr>");
            drawPrompt(prompt);
            code.addLine("<td align='left'>");
            code.addLine("<html:radio name='" + form + "' property='" + fieldVariableName + "' value='true'/><bean:message key='misc.yes'/>");
            code.addLine("<html:radio name='" + form + "' property='" + fieldVariableName + "' value='false'/><bean:message key='misc.no'/>");
            code.addLine("</td>");
            code.addLine("</tr>");
        } else if ("BooleanDropDown".equals(type)) {
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            code.addLine("<tr>");
            drawPrompt(prompt);
            code.addLine("<td align='left'>");
            generateBooleanDropDown(fieldName);
            code.addLine("</td>");                
            code.addLine("</tr>");
            
        } else if ("BooleanCheckbox".equals(type)) {
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            code.addLine("<tr>");
            code.addLine("<th></th>");
            code.addLine("<td align='left'>");
            code.addLine("<html:checkbox property=\"" + fieldVariableName + "\"/>");
            code.addLine("<bean:message key=\"" + prompt + "\"/>");
            code.addLine("</td>");                
            code.addLine("</tr>");
        } else if ("RadioButtons".equals(type)) {
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            code.addLine("<tr>");
            drawPrompt(prompt);
            code.addLine("<td align='left'>");
            code.addLine(generateRadioButtons(screenFieldDescriptor));
            code.addLine("</td>");
            code.addLine("</tr>");
        } else  if (type.equals("Lookup")) { 
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            String lookupVariable = screenFieldDescriptor.getLookupVariable();

            code.addLine("<tr>");
            drawPrompt(prompt);
            code.addLine("<td align='left'>");
            code.addLine("<html:select name='" + form + "' property='" + fieldVariableName + "' styleClass='select1'>");
            code.addLine("    <html:options collection='" + lookupVariable + "' property='code' labelProperty='display'/>");
            code.addLine("</html:select>");            
            code.addLine("</td>");
            code.addLine("</tr>");
        } else if (type.equals("Link")) { 
            code.addLine("<tr>");
            code.addLine("<th></th>");
            code.addLine("<td align='right'>");
            code.add("<html:link page='" + link + "'>");
            code.add("add");
            code.addLine("</html:link>");
            code.addLine("</td>");
            code.addLine("</tr>");            
        } else if (type.equals("ChooseObject")) { 
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            String lookupVariable = screenFieldDescriptor.getLookupVariable();
            String subObjectDisplayField = screenFieldDescriptor.getSubObjectDisplayField();
            
            code.addLine("<tr>");
            drawPrompt(prompt);
            code.addLine("<td align='left'>");
            
            // <html:select name="equipmentForm" property="preferredVendorPriceId" styleClass="select1">
            code.addLine("<html:select name='" + form + "' property='" + fieldVariableName + "Id' styleClass='select1'>");
            code.addLine("<html:option key='misc.noselection' value=''/>");
            code.addLine("<html:options collection='" + fieldVariableName + "List' property='id' labelProperty='" + subObjectDisplayField + "'/>");
            code.addLine("</html:select>");

            
            code.addLine("</td>");
            code.addLine("</tr>");            
        } else if (type.equals("Select")) { 
            // TODO: this does not appear to be used. When converting to enum remove if not used.
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            String lookupVariable = screenFieldDescriptor.getLookupVariable();

            SelectData selectData = screenFieldDescriptor.getSelectData();
            code.addLine("<tr>");
            drawPrompt(prompt);
            code.addLine("<td align='left'>");
            
            // <html:select name="equipmentForm" property="preferredVendorPriceId" styleClass="select1">
            code.addLine("<html:select name='" + form + "' property='" + fieldVariableName + "' styleClass='select1'>");
            code.addLine("<html:option key='misc.noselection' value=''/>");
            code.addLine("<html:options collection='" + selectData.getListName() + "' property='" + selectData.getIdName() +  "' labelProperty='" + selectData.getDisplayName() + "'/>");
            code.addLine("</html:select>");

            
            code.addLine("</td>");
            code.addLine("</tr>");            
        } else if (type.equals("Enum")) { 
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
            String lookupVariable = screenFieldDescriptor.getLookupVariable();
            ClassDescriptor enumClass = new ClassDescriptor(screenFieldDescriptor.getEnumClass()); 
            String enumVariablePrefix = enumClass.getJavaVariableName();

            SelectData selectData = screenFieldDescriptor.getSelectData();
            code.addLine("<tr>");
            drawPrompt(prompt);
            code.addLine("<td align='left'>");
            
            // <html:select name="equipmentForm" property="preferredVendorPriceId" styleClass="select1">
            code.addLine("<html:select name='" + form + "' property='" + fieldVariableName + "' styleClass='select1'>");
            code.addLine("<html:option key='misc.noselection' value=''/>");
            code.addLine("<html:options collection='" + enumVariablePrefix + "List' property='value' labelProperty='display'/>");
            code.addLine("</html:select>");

            
            code.addLine("</td>");
            code.addLine("</tr>");            
        } else  if (type.equals("List")) { 
            generateListField(screenFieldDescriptor);
        }

        
        code.addLine();
    }

    protected void drawPrompt(ScreenFieldDescriptor screenFieldDescriptor) {
        if (screenFieldDescriptor.getDrawPrompt()) { 
            String fieldName = screenFieldDescriptor.getFieldName();
            String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();

            code.addLine("<th class='prompt'>");
            code.addLine("<bean:message key='" + prompt + "'/>");
            code.addLine("</th>");
        }
    }

    protected void drawPrompt(String prompt) {
        code.addLine("<th class='prompt'>");
        code.addLine("<bean:message key='" + prompt + "'/>");
        code.addLine("</th>");
    }


    
    protected void generateListField(ScreenFieldDescriptor screenFieldDescriptor) {
        String form = screenDescriptor.getFormDataName();
        String fieldName = screenFieldDescriptor.getFieldName();
        String fieldVariableName = convertToJavaVariableName(fieldName);
        String tableName = fieldVariableName + "Table";
        String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName.toLowerCase();
        String enumVariable = "enumVariable";
        String screenType = screenDescriptor.getScreenType();
        int numberOfRows = getNumberOfRows(screenFieldDescriptor);
        int tableWidth = getTableWidth(screenFieldDescriptor);
        int numberOfColumns = getNumberOfColumns(screenFieldDescriptor);

        model.findObject(screenDescriptor.getValueObjectClass());

        if ("update".equals(screenType)) { 
            code.addLine("<logic:notEmpty name='" + form + "' property='id'>");
        }
        
        code.addLine("<tr>");
        drawPrompt(screenFieldDescriptor);
        code.addLine("<td align='left'>");

        code.addLine("<table id='" + tableName + "' border='0' width='100%' class='table'>");
        code.addLine("<tr>");                
        List columns = screenFieldDescriptor.getColumns();
        Iterator i = columns.iterator();
        while (i.hasNext()) {
            ScreenColumnDescriptor column = (ScreenColumnDescriptor)i.next();
            
            String heading = column.getHeading();        
            String width = column.getWidth();        
            int row = column.getRow();        //todo: rename to subrow??
            if (row == 0) {             
                code.addLine("<td align='left' width='" + width + "' class='tableheader'>" + heading + "</td>");
            }
        }
        code.addLine("</tr>");
        
        code.addLine("<logic:iterate name='" + form + "' property='" + fieldVariableName + "' id='" + enumVariable + "'>");
        code.addLine("<tr>");
        code.addLine();
        
        if (numberOfRows > 1) { 
            code.addLine("<td width='" + tableWidth + "' colspan='" + numberOfColumns + "'>");
            code.addLine();
            code.addLine("<table border='0' width='100%' class='table'>");
        }

        
        
        for (int row = 0; row < numberOfRows; row++) { 
            
            
            if (numberOfRows > 1) { 
                code.addLine("<tr>");
                code.addLine("<td width='" + tableWidth + "'>");
                code.addLine("<table border='0' width='100%' class='table'>");
                code.addLine("<tr>");
            }
            
            i = columns.iterator();
            while (i.hasNext()) {
                ScreenColumnDescriptor column = (ScreenColumnDescriptor)i.next();

                int columnsRow = column.getRow();
                if (columnsRow == row) {            
                    String subFieldName = column.getFieldName();
                    String subFieldVariableName = null;
                    if (!StringUtil.isEmpty(subFieldName)) {
                        subFieldVariableName = convertToJavaVariableName(subFieldName);
                    }
                    String link = column.getLink();
                    String type = column.getType();
                    String constantValue = column.getConstantValue();
                    String width = column.getWidth();        
                    String minPrecision = column.getMinPrecision();        
                    String maxPrecision = column.getMaxPrecision();        

                    String styleStatement = getStyleStatement(column);

                    if (!StringUtil.isEmpty(link)) {
                        code.add("<td align='left' class='tablebody'>");
                        code.add("<html:link page='" + link + "' paramId='Id' paramName='" + enumVariable + "' paramProperty='id'>");
                        code.add("<c:out value='${" + enumVariable + "." + subFieldVariableName + "}'/>");
                        code.add("</html:link></td>");
                        code.addLine("</td>");                
                    } else if ("Constant".equals(type)) {
                        code.add("<td align='left' class='tablebody'>");
                        if (constantValue != null) { 
                            code.add(constantValue);
                        }
                        code.addLine("</td>");                
                    } else if ("RadioButtons".equals(type)) {
                        drawTableColumnData(width, generateRadioButtons(column));
                    } else if ("Dropdown".equals(type)) {
                        code.addLine("<td align='left' class='tablebody'>");
                        generateDropdown(column);
                        code.addLine("</td>");                
                    } else if ("BooleanRadioButtons".equals(type)) {
                        code.addLine("<td align='left' class='tablebody'>");
                        generateBooleanRadioButtons(column);
                        code.addLine("</td>");                
                    } else if ("BooleanDropDown".equals(type)) {
                        code.addLine("<td align='left' class='tablebody'>");
                        generateBooleanDropDown(column.getFieldName());
                        code.addLine("</td>");                
                    } else  if ("Lookup".equals(type)) { 
                        String lookupVariable = column.getLookupVariable();                        

                        code.addLine("<td align='left' class='tablebody'>");
                        code.addLine("<modelgenerated:selectlookup name='enumVariable'"); 
                        code.addLine("                             propertyPrefix='" + lookupVariable.toLowerCase() + "_'");
                        code.addLine("                             propertyId='id'");
                        code.addLine("                             originalValue='" + lookupVariable + "'");
                        code.addLine("                             lookupList='" + lookupVariable + "'");
                        code.addLine("                             styleClass='select1'");
                        code.addLine("/>");
                        code.addLine("</td>");
                    } else  if ("ChooseObject".equals(type)) {
                        drawTableColumnData(width, generateChooseObject(column));
                    } else  if ("Enum".equals(type)) {
                        drawTableColumnData(width, generateEnum(column));
                    } else  if ("Date".equals(type)) {
                        String existingValue = "<modelgenerated:dateFormat name='" + enumVariable + "' property='" + subFieldVariableName + "'/>";
                        if (column.getSupportsUpdate()) {
                            if (StringUtil.isEmpty(styleStatement)) {
                                styleStatement = "class=\"dateentryshort\"";
                            }
                            String prefix = subFieldVariableName.toLowerCase() + "_";
                            String content= "<input type='text' value=\"" + existingValue + "\" name=\"" + prefix + "<c:out value='${" + enumVariable + ".id}'/>\" " + styleStatement + "/>";                    
                            drawTableColumnData(width, content);
                        } else {                    
                            drawTableColumnData(width, existingValue);
                        }
                    } else {                        
                        String oldValue = null;
                        if ("Double".equals(type)) {
                            if (maxPrecision != null && minPrecision != null ) {
                                oldValue = "<modelgenerated:numberFormat name='" + enumVariable + "' property='" + subFieldVariableName + "' maxPrecision='" + maxPrecision + "' minPrecision='" + minPrecision + "'/>";
                            } else if (maxPrecision != null) {
                                oldValue = "<modelgenerated:numberFormat name='" + enumVariable + "' property='" + subFieldVariableName + "' maxPrecision='" + maxPrecision + "'/>";
                            } else {
                                oldValue = "<modelgenerated:numberFormat name='" + enumVariable + "' property='" + subFieldVariableName + "'/>";
                            }
                        } else {
                            oldValue = "<c:out value='${" + enumVariable + "." + subFieldVariableName + "}'/>";
                        }
                        if (column.getSupportsUpdate()) {
                            code.add("<td align='left' class='tablebody'>");
                            String prefix = subFieldVariableName.toLowerCase() + "_";
                            code.add("<input type='text' value=\"" + oldValue + "\" name=\"" + prefix + "<c:out value='${" + enumVariable + ".id}'/>\"" + styleStatement + "/>");                    
                            code.addLine("</td>");
                        } else {                    
                            code.addLine("<td align='left' class='tablebody'>" + oldValue + "</td>");
                        }
                    }
                }
            }
            if (numberOfRows > 1) { 
                code.addLine("</tr>");

                code.addLine("</table>");
                code.addLine();
                code.addLine("</td");
                code.addLine("</tr>");
            }
            
        }
        
        if (numberOfRows > 1) { 
            code.addLine("</table>");
            code.addLine();
            code.addLine("</td");
            code.addLine();
        }
        
        
        
        code.addLine("</tr>");
        code.addLine("</logic:iterate>");
        code.addLine();
        code.addLine("</table>");
        
        code.addLine("</td>");
        code.addLine("</tr>");
        
        if ("update".equals(screenType)) { 
            code.addLine("</logic:notEmpty>");
        }
        code.addLine();
    }
    
    protected void drawTableColumnData(String width, String content) {
        code.addLine("<td align='left' class='tablebody'>");
        code.addLine(content);        
        code.addLine("</td>");                
    }
    
    
    
    protected String getStyleStatement(ScreenColumnDescriptor column) {
        String styleStatement = "";
        String style = column.getStyle();
        if (!StringUtil.isEmpty(style)) {
            styleStatement = " class='" + style + "'";            
        }
            
        return styleStatement;
    }
    
    protected String generateRadioButtons(ScreenColumnDescriptor column) {
        String fieldName = column.getFieldName();
        String fieldVariableName = convertToJavaVariableName(fieldName);
        String prefix = fieldName.toLowerCase() + "_";
        String enumVariable = "enumVariable";
        
        StringBuffer buf = new StringBuffer();
        
        List choices = column.getChoices();
        Iterator i = choices.iterator();
        while (i.hasNext()) {
            ChoiceDescriptor ChoiceDescriptor = (ChoiceDescriptor)i.next();
            String value = ChoiceDescriptor.getValue();
            String display = ChoiceDescriptor.getDisplay();
            
            buf.append("    <input type='radio' name=\"" + prefix + "<c:out value='${" + enumVariable + ".id}'/>\" value='" + value + "'"); 
            buf.append("\n");
            buf.append("        <logic:equal name='" + enumVariable + "' property='" + fieldVariableName + "' value='" + value + "'>checked=\"checked\"</logic:equal>/>");
            buf.append("        " + display);
            buf.append("\n");
        }
        return buf.toString();
    }
    protected String generateRadioButtons(ScreenFieldDescriptor field) {
        String fieldName = field.getFieldName();
        String fieldVariableName = convertToJavaVariableName(fieldName);

        String form = screenDescriptor.getFormDataName();
        
        StringBuffer buf = new StringBuffer();
        
        List choices = field.getChoices();
        Iterator i = choices.iterator();
        while (i.hasNext()) {
            ChoiceDescriptor ChoiceDescriptor = (ChoiceDescriptor)i.next();
            String value = ChoiceDescriptor.getValue();
            String display = ChoiceDescriptor.getDisplay();
            
            buf.append("    <html:radio name=\"" + form + "\" value='" + fieldVariableName + "' " + value + "'/>" + display); 
            buf.append("\n");
        }
        return buf.toString();
    }
    protected void generateDropdown(ScreenColumnDescriptor column) {
        String fieldName = column.getFieldName();
        String fieldVariableName = convertToJavaVariableName(fieldName);
        String prefix = fieldName.toLowerCase() + "_";
        String enumVariable = "enumVariable";

        // generated code should look like this
        // <html:select name="jobForm" property="status" styleClass="select1">
        //    <html:option key="misc.noselection" value=""/>

        code.addLine("    <select name=\"" + prefix + "<c:out value='${" + enumVariable + ".id}'/>\" class='select1'>"); 
        
        List choices = column.getChoices();
        Iterator i = choices.iterator();
        while (i.hasNext()) {
            ChoiceDescriptor ChoiceDescriptor = (ChoiceDescriptor)i.next();
            String value = ChoiceDescriptor.getValue();
            String display = ChoiceDescriptor.getDisplay();
            
            //code.addLine("        <html:option key='" + display + "' value='" + value + "'/>");
             
            code.add    ("        <option value='" + value + "'");
            code.addLine("<logic:equal name='" + enumVariable + "' property='" + fieldVariableName + "' value='" + value + "'>selected=\"selected\"</logic:equal> >" + display + "</option>");        
        }
        code.addLine("    </select>"); 
    }
    protected void generateBooleanRadioButtons(ScreenColumnDescriptor column) {
        String fieldName = column.getFieldName();
        String fieldVariableName = convertToJavaVariableName(fieldName);
        String prefix = fieldName.toLowerCase() + "_";
        String enumVariable = "enumVariable";
        
        code.addLine("    <input type='radio' name=\"" + prefix + "<c:out value='${" + enumVariable + ".id}'/>\" value='true'"); 
        code.addLine("        <logic:equal name='" + enumVariable + "' property='" + fieldVariableName + "' value='true'>checked=\"checked\"</logic:equal>/>Yes");
        code.addLine("    <input type='radio' name=\"" + prefix + "<c:out value='${" + enumVariable + ".id}'/>\" value='false'"); 
        code.addLine("        <logic:equal name='" + enumVariable + "' property='" + fieldVariableName + "' value='false'>checked=\"checked\"</logic:equal>/>No");
    }
    
    protected void generateBooleanDropDown(ScreenColumnDescriptor column) {
        String fieldName = column.getFieldName();
        String fieldVariableName = convertToJavaVariableName(fieldName);
        String prefix = fieldName.toLowerCase() + "_";
        String enumVariable = "enumVariable";
        
        code.addLine("    <select name=\"" + prefix + "<c:out value='${" + enumVariable + ".id}'/>\" styleClass='select1'>");
        //code.addLine("        <option value=''/>");
        code.addLine("        <option value='true'");
        code.addLine("            <logic:equal name='" + enumVariable + "' property='" + fieldVariableName + "' value='true'>selected=\"selected\"</logic:equal>/>Yes");        
        code.addLine("        <option value='false'");
        code.addLine("            <logic:equal name='" + enumVariable + "' property='" + fieldVariableName + "' value='false'>selected=\"selected\"</logic:equal>/>No");
        code.addLine("    </select>");
    }
    protected void generateBooleanDropDown(String fieldName) {
        String fieldVariableName = convertToJavaVariableName(fieldName);
        
        code.addLine("    <select name='" + screenDescriptor.getFormDataName() + "' property='" + fieldVariableName + "' styleClass='select1'>");
        code.addLine("        <option value='true'");
        code.addLine("            <logic:equal name='" + screenDescriptor.getFormDataName() + "' property='" + fieldVariableName + "' value='true'>selected=\"selected\"</logic:equal>/>Yes");        
        code.addLine("        <option value='false'");
        code.addLine("            <logic:equal name='" + screenDescriptor.getFormDataName() + "' property='" + fieldVariableName + "' value='false'>selected=\"selected\"</logic:equal>/>No");
        code.addLine("    </select>");
    }

    protected String generateChooseObject(ScreenColumnDescriptor column){
        String subFieldName = column.getFieldName();
        String subObjectDisplayField = column.getSubObjectDisplayField();
        String subFieldVariableName = null;
        String styleStatement = getStyleStatement(column);
        if (!StringUtil.isEmpty(subFieldName)) {
            subFieldVariableName = convertToJavaVariableName(subFieldName);
        }
        ClassDescriptor enumClass = new ClassDescriptor(column.getEnumClass()); 

        StringBuffer strBuf = new StringBuffer();
        
        strBuf.append("<select name=\"" + subFieldVariableName.toLowerCase() +  "id_<c:out value='${enumVariable.id}'/>\" " + styleStatement + ">");
        strBuf.append("\n");
        strBuf.append("    <option value=\"\"/>[no selection]</option>");
        strBuf.append("\n");
        strBuf.append("    <c:forEach var=\"" + subFieldVariableName + "\" items=\"${" + subFieldVariableName + "List}\">");
        strBuf.append("\n");
        strBuf.append("        <c:choose>");
        strBuf.append("\n");
        strBuf.append("            <c:when test=\"${" + subFieldVariableName + ".id == enumVariable." + subFieldVariableName + "Id}\">");
        strBuf.append("\n");
        strBuf.append("                <option value=\"<c:out value='${" + subFieldVariableName + ".id}'/>\" selected=\"selected\"><c:out value='${" + subFieldVariableName + "." + subObjectDisplayField + "}'/></option>");
        strBuf.append("\n");
        strBuf.append("            </c:when>");
        strBuf.append("\n");
        strBuf.append("            <c:otherwise>");
        strBuf.append("\n");
        strBuf.append("                <option value=\"<c:out value='${" + subFieldVariableName + ".id}'/>\" ><c:out value='${" + subFieldVariableName + ".fullName}'/></option>\");");
        strBuf.append("\n");
        strBuf.append("            </c:otherwise>");
        strBuf.append("\n");
        strBuf.append("        </c:choose>");
        strBuf.append("\n");
        strBuf.append("    </c:forEach>");
        strBuf.append("\n");
        strBuf.append("</select>");
        strBuf.append("\n");
        
        return strBuf.toString();
    }

    protected String generateEnum(ScreenColumnDescriptor column) {
        String subFieldName = column.getFieldName();
        String subFieldVariableName = null;
        String styleStatement = getStyleStatement(column);
        if (!StringUtil.isEmpty(subFieldName)) {
            subFieldVariableName = convertToJavaVariableName(subFieldName);
        }
        ClassDescriptor enumClass = new ClassDescriptor(column.getEnumClass()); 
        String enumVariablePrefix = enumClass.getJavaVariableName();

        StringBuffer strBuf = new StringBuffer();
        
        strBuf.append("<select name=\"" + subFieldVariableName.toLowerCase() + "_<c:out value='${enumVariable.id}'/>\" " + styleStatement + ">");
        strBuf.append("\n");
        strBuf.append("    <option value=\"\"/>[no selection]</option>");
        strBuf.append("\n");
        strBuf.append("    <c:forEach var=\"" + enumVariablePrefix + "Item\" items=\"${" + enumVariablePrefix + "List}\">");
        strBuf.append("\n");
        strBuf.append("        <c:choose>");
        strBuf.append("\n");
        strBuf.append("            <c:when test=\"${" + enumVariablePrefix + "Item.value == enumVariable." + subFieldVariableName + "Key}\">");
        strBuf.append("\n");
        strBuf.append("                <option value=\"<c:out value='${" + enumVariablePrefix + "Item.value}'/>\" selected=\"selected\"><c:out value='${" + enumVariablePrefix + "Item.display}'/></option>");
        strBuf.append("\n");
        strBuf.append("            </c:when>");
        strBuf.append("\n");
        strBuf.append("            <c:otherwise>");
        strBuf.append("\n");
        strBuf.append("                <option value=\"<c:out value='${" + enumVariablePrefix + "Item.value}'/>\" ><c:out value='${" + enumVariablePrefix + "Item.display}'/></option>");
        strBuf.append("\n");
        strBuf.append("            </c:otherwise>");
        strBuf.append("\n");
        strBuf.append("        </c:choose>");
        strBuf.append("\n");
        strBuf.append("    </c:forEach>");
        strBuf.append("\n");
        strBuf.append("</select>");
        strBuf.append("\n");
        
        return strBuf.toString();
    }
                        


    
    protected void generateViewForm() {
        code.addLine("/* " + screenDescriptor.getJspViewFile());
        code.addLine("* Generated table creation script");
        code.addLine("*/");
    }    
    
    
    public String fullyQualifiedNameToPackage(String fqn) {
        // note this is different than the one in JavaCodeBaseGenerator
        Logger.debug(this, "FQN: " + fqn);
        return fqn.substring(0, fqn.lastIndexOf("/"));
    }

    private String convertToJavaVariableName(String variableName) {
        Assert.check(variableName != null, "variableName != null");
        if (variableName.length() == 1) {
            return variableName.toLowerCase();
        } else {
            return variableName.substring(0,1).toLowerCase() + variableName.substring(1);
        }
    }
    
    private int getNumberOfRows(ScreenFieldDescriptor screenFieldDescriptor) {
        int numberOfRows = 1;
        List columns = screenFieldDescriptor.getColumns();
        Iterator i = columns.iterator();
        while (i.hasNext()) {
            ScreenColumnDescriptor column = (ScreenColumnDescriptor)i.next();
            
            int row = column.getRow() + 1;
            if (row > numberOfRows) {
                numberOfRows = row;
            }
            
        }
        return numberOfRows;
    }
    
    private int getTableWidth(ScreenFieldDescriptor screenFieldDescriptor) {
        int tableWidth = 600;
        /*
        List columns = screenFieldDescriptor.getColumns();
        Iterator i = columns.iterator();
        while (i.hasNext()) {
            ScreenColumnDescriptor column = (ScreenColumnDescriptor)i.next();
            
            
        }
        */
        return tableWidth;
    }
    private int getNumberOfColumns(ScreenFieldDescriptor screenFieldDescriptor) {
        int numberOfColumns = 0;
        List columns = screenFieldDescriptor.getColumns();
        Iterator i = columns.iterator();
        while (i.hasNext()) {
            ScreenColumnDescriptor column = (ScreenColumnDescriptor)i.next();
            
            int row = column.getRow();
            if (row == 0) {
                numberOfColumns++;
            }
            
        }
        return numberOfColumns;
    }
    
    
}
