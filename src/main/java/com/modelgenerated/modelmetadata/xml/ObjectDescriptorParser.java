/*
 * ObjectDescriptorParser.java
 *
 * Created on November 3, 2002, 9:09 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.xml;

import com.modelgenerated.generator.GeneratorException;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.FieldDescriptor;
import com.modelgenerated.modelmetadata.FieldTypeEnum;
import com.modelgenerated.modelmetadata.IndexDescriptor;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.QueryDescriptor;
import com.modelgenerated.modelmetadata.QueryTypeEnum;
import com.modelgenerated.modelmetadata.ReferenceDescriptor;
import com.modelgenerated.modelmetadata.ReferenceTypeEnum;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.DomUtil;
import com.modelgenerated.util.StringUtil;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This object describes a value object, its interface, DAO and database table.
 *
 * It parses an XML file that looks likes this.
 * 
 * <ObjectDescriptor>
 *     <Interface>com.modelgenerated.domain.party.Company</Interface>
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
public class ObjectDescriptorParser {
    protected ObjectDescriptor objectDescriptor;
    protected String valueObjectInterface;

    /** Creates a new instance of ValueObjectDescription */
    public ObjectDescriptorParser() {
    }
    
    
    public ObjectDescriptor parse(Model model, String objectDescriptorFileLocation) {
        try {
            objectDescriptor = new ObjectDescriptor(model);
    
            System.out.println("objectDescriptorFileLocation: " + objectDescriptorFileLocation);
            URL configURL = new URL(objectDescriptorFileLocation);          
            InputStream configInputStream = configURL.openStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();                       
            Document doc = db.parse(configInputStream);

            Element root = doc.getDocumentElement();
            System.out.println("nodename: " + root.getNodeName());
            
            String valueObjectInterface = DomUtil.getChildElementText(root, "Interface");
            Assert.check(valueObjectInterface != null, "valueObjectInterface != null");
            
            String implementationName = DomUtil.getChildElementText(root, "Implementation");
            //Assert.check(implementationName != null, "implementationName != null");
            
            String listInterfaceName = DomUtil.getChildElementText(root, "ListInterface");
            String listImplementationName = DomUtil.getChildElementText(root, "ListImplementation");
            
            String daoInterfaceName = DomUtil.getChildElementText(root, "DAOInterface");
            String daoImplementationName = DomUtil.getChildElementText(root, "DAOImplementation");
            
            String testDataClass = DomUtil.getChildElementText(root, "TestDataClass");
            
            String tableName = DomUtil.getChildElementText(root, "TableName");
            //Assert.check(tableName != null, "tableName!= null");
            
            String tableAlias = DomUtil.getChildElementText(root, "TableAlias");
            //Assert.check(tableName != null, "tableName!= null");

            // this should not be required.
            String baseInterface = DomUtil.getChildElementText(root, "BaseInterface");
            //Assert.check(baseInterface != null, "baseInterface != null");            

            String description = DomUtil.getChildElementText(root, "Description");

            String implementsInterface = DomUtil.getChildElementText(root, "Implements");
            if (!StringUtil.isEmpty(implementsInterface)) {
                List<ClassDescriptor> implementsList = new ArrayList<ClassDescriptor>();
    	        String[] classNameArray = implementsInterface.split(",");
    	        Arrays.stream(classNameArray).forEach(x -> implementsList.add(new ClassDescriptor(x)));
                objectDescriptor.setImplementsList(implementsList);                
            }

            boolean cloneable = false;
            String cloneableStr = DomUtil.getChildElementText(root, "Cloneable");
            if (cloneableStr != null) {
                cloneable = cloneableStr.equals("yes") ? true : false;
            }

            boolean persisted = true;
            String persistedStr = DomUtil.getChildElementText(root, "Persisted");
            if (persistedStr != null) {
                persisted = persistedStr.equals("yes") ? true : false;
            }

            boolean supportsAudit = true;
            String supportsAuditStr = DomUtil.getChildElementText(root, "SupportsAudit");
            if (supportsAuditStr != null) {
                supportsAudit = supportsAuditStr.equals("yes") ? true : false;
            }
            boolean createdModifiedFields = false;
            String createdModifiedFieldsStr = DomUtil.getChildElementText(root, "CreatedModifiedFields");
            if (createdModifiedFieldsStr != null) {
                createdModifiedFields = createdModifiedFieldsStr.equals("yes") ? true : false;
            }
            boolean multiTenant = false;
            String multiTenantStr = DomUtil.getChildElementText(root, "MultiTenant");
            if (multiTenantStr == null || multiTenantStr.equals("yes") ) {
            	multiTenant = true;
            }


            if (valueObjectInterface != null) {
                objectDescriptor.setValueObjectInterface(new ClassDescriptor(valueObjectInterface));
            }            
            if (implementationName != null) {
                objectDescriptor.setImplementationName(new ClassDescriptor(implementationName));
            }            
            if (listInterfaceName != null) {
                objectDescriptor.setListInterface(new ClassDescriptor(listInterfaceName));
            }            
            if (listImplementationName != null) {
                objectDescriptor.setListImplementation(new ClassDescriptor(listImplementationName));
            }            
            if (daoInterfaceName != null) {
                objectDescriptor.setDAOInterface(new ClassDescriptor(daoInterfaceName));
            }            
            if (daoImplementationName != null) {
                objectDescriptor.setDAOImplementation(new ClassDescriptor(daoImplementationName));
            }
            if (testDataClass != null) {
                objectDescriptor.setTestDataClass(new ClassDescriptor(testDataClass));
            }
            

            objectDescriptor.setTableName(tableName);
            objectDescriptor.setTableAlias(tableAlias);
            objectDescriptor.setBaseInterface(baseInterface);
            objectDescriptor.setDescription(description);
            objectDescriptor.setCloneable(cloneable);
            objectDescriptor.setPersisted(persisted);
            objectDescriptor.setSupportsAudit(supportsAudit);
            objectDescriptor.setCreatedModifiedFields(createdModifiedFields);
            objectDescriptor.setMultiTenant(multiTenant);

            loadFields(root, objectDescriptorFileLocation);
            loadReferences(root, objectDescriptorFileLocation);
            loadQueries(root, objectDescriptorFileLocation);
            loadIndicies(root, objectDescriptorFileLocation);
            
            objectDescriptor.setMethods(MethodParser.loadMethods(root));
            objectDescriptor.setListMethods(MethodParser.loadListMethods(root));
            
            return objectDescriptor;
            
        } catch (MalformedURLException e) {
            throw new GeneratorException("Bad url: " + objectDescriptorFileLocation, e);
        } catch (ParserConfigurationException e) {
            throw new GeneratorException("Error parsing Object Descriptor file: " + objectDescriptorFileLocation, e);
        } catch (SAXException e) {
            throw new GeneratorException("Error parsing Object Descriptor file: " + objectDescriptorFileLocation, e);
        } catch (IOException e) {
            throw new GeneratorException("Couldn't parse Object Descriptor input stream: " + objectDescriptorFileLocation, e);
        }
    }

    private void loadFields(Element root, String objectDescriptorFileLocation) {
        List<FieldDescriptor> fieldList = new ArrayList<FieldDescriptor>(); // keep list in original order and map to find duplicates.
        Map<String,FieldDescriptor> fields = new HashMap<String,FieldDescriptor>();

        Element fieldsElement = DomUtil.getChildElement(root, "Fields");
        NodeList fieldsNodeList = fieldsElement.getElementsByTagName("Field");
        for (int i = 0; i < fieldsNodeList.getLength(); i++) {
            Element elem = (Element)fieldsNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals("Field"), "nodeName.equals(Field)");


            String name = DomUtil.getChildElementText(elem, "Name"); 
            String description = DomUtil.getChildElementText(elem, "Description"); 
            String columnName = DomUtil.getChildElementText(elem, "ColumnName");
            String type = DomUtil.getChildElementText(elem, "Type");
            String joinField = DomUtil.getChildElementText(elem, "JoinField");
            String alias = DomUtil.getChildElementText(elem, "Alias");
            String tableName = DomUtil.getChildElementText(elem, "TableName");
            String version = DomUtil.getChildElementText(elem, "Version");
            String sql = DomUtil.getChildElementText(elem, "Sql");
            String fieldClass = DomUtil.getChildElementText(elem, "Class");
            String persisted = DomUtil.getChildElementText(elem, "Persisted");
            String nullable = DomUtil.getChildElementText(elem, "Nullable");
            String useInTest = DomUtil.getChildElementText(elem, "UseInTest");
            String sizeString = DomUtil.getChildElementText(elem, "Size");
            String referenceType = DomUtil.getChildElementText(elem, "ReferenceType");
            String coalesce1 = DomUtil.getChildElementText(elem, "Coalesce1");
            String coalesce2 = DomUtil.getChildElementText(elem, "Coalesce2");
            String coalesce3 = DomUtil.getChildElementText(elem, "Coalesce3");
            String coalesce4 = DomUtil.getChildElementText(elem, "Coalesce4");
            int size = parseSize(sizeString);                

            FieldDescriptor fieldDescriptor = new FieldDescriptor(objectDescriptor);
            fieldDescriptor.setName(name);
            fieldDescriptor.setDescription(description);
            fieldDescriptor.setColumnName(columnName);
            fieldDescriptor.setType(FieldTypeEnum.getFieldType(type));
            fieldDescriptor.setJoinField(joinField);
            fieldDescriptor.setAlias(alias);
            fieldDescriptor.setTableName(tableName);
            fieldDescriptor.setVersion(version);
            fieldDescriptor.setSql(sql);
            fieldDescriptor.setClassDescriptor(new ClassDescriptor(fieldClass));
            fieldDescriptor.setSize(size);
            if (persisted == null) {
        		fieldDescriptor.setPersisted(true);
            } else {
                fieldDescriptor.setPersisted(persisted.equals("yes") ? true : false);
            }
            if (nullable == null) {
                fieldDescriptor.setNullable(true);
            } else {
                fieldDescriptor.setNullable(nullable.equals("yes") ? true : false);
            }
            if (useInTest == null) {
                fieldDescriptor.setUseInTest(true);
            } else {
                fieldDescriptor.setUseInTest(nullable.equals("yes") ? true : false);
            }
            if (referenceType == null) {
                fieldDescriptor.setReferenceType(ReferenceTypeEnum.ONE_TO_ONE);
            } else {
                fieldDescriptor.setReferenceType(ReferenceTypeEnum.getReferenceType(referenceType));
            }
            fieldDescriptor.setCoalesce1(coalesce1);
            fieldDescriptor.setCoalesce2(coalesce2);
            fieldDescriptor.setCoalesce3(coalesce3);
            fieldDescriptor.setCoalesce4(coalesce4);

            Object previous = fields.put(name, fieldDescriptor); 
            Assert.check(previous == null, "Duplicate field \"" + name + "\" found in " + objectDescriptorFileLocation);
            
            fieldList.add(fieldDescriptor); 
        }
        objectDescriptor.setFields(fieldList);
        
    }
    
    private void loadReferences(Element root, String objectDescriptorFileLocation) {
        Map<String,ReferenceDescriptor> references = new HashMap<String,ReferenceDescriptor>();
        
        Element referencesElement = DomUtil.getChildElement(root, "References");
        if (referencesElement == null) {
            return;
        }
        NodeList referencesNodeList = referencesElement.getElementsByTagName("Reference");
        if (referencesNodeList == null) {
            return;
        }
        for (int i = 0; i < referencesNodeList.getLength(); i++) {
            Element elem = (Element)referencesNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals("Reference"), "nodeName.equals(\"Reference\")");


            String name = DomUtil.getChildElementText(elem, "Name"); 
            String description = DomUtil.getChildElementText(elem, "Description"); 
            String type = DomUtil.getChildElementText(elem, "Type");
            String fieldClass = DomUtil.getChildElementText(elem, "Class");

            String targetClass = DomUtil.getChildElementText(elem, "TargetClass");
            String targetMethod = DomUtil.getChildElementText(elem, "TargetMethod");
            String searchCriteria = DomUtil.getChildElementText(elem, "SearchCriteria");

            ReferenceDescriptor referenceDescriptor = new ReferenceDescriptor(objectDescriptor,
                    name,
                    description,
                    ReferenceTypeEnum.getReferenceType(type),
                    new ClassDescriptor(fieldClass),
                    new ClassDescriptor(targetClass),
                    targetMethod,
                    StringUtil.isEmpty(searchCriteria) ? null : new ClassDescriptor(searchCriteria));

            Object previous = references.put(name, referenceDescriptor); 
            Assert.check(previous == null, "Duplicate reference \"" + name + "\" found in " + objectDescriptorFileLocation);

        }
        objectDescriptor.setReferences(references);
    }

    private void loadQueries(Element root, String objectDescriptorFileLocation) {
        Map<String,QueryDescriptor> queries = new HashMap<String,QueryDescriptor>();

        Element queriesElement = DomUtil.getChildElement(root, "Queries");
        if (queriesElement == null) {
            return;
        }
        NodeList queryNodeList = queriesElement.getElementsByTagName("Query");
        if (queryNodeList == null) {
            return;
        }
        for (int i = 0; i < queryNodeList.getLength(); i++) {
            Element elem = (Element)queryNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals("Query"), "nodeName.equals(Query)");


            String type = DomUtil.getChildElementText(elem, "Type");
            String fieldName = DomUtil.getChildElementText(elem, "FieldName"); 
            String methodName = DomUtil.getChildElementText(elem, "MethodName");
            String orderBy = DomUtil.getChildElementText(elem, "OrderBy");

            QueryDescriptor queryDescriptor = new QueryDescriptor();
            queryDescriptor.setType(QueryTypeEnum.getFieldType(type));
            queryDescriptor.setFieldName(fieldName);
            queryDescriptor.setMethodName(methodName);
            queryDescriptor.setOrderBy(orderBy);

            Object previous = queries.put(methodName, queryDescriptor); 
            Assert.check(previous == null, "Duplicate Query \"" + methodName + "\" found in " + objectDescriptorFileLocation);
        }
        
        objectDescriptor.setQueries(queries);
        
    }
    
    private void loadIndicies(Element root, String objectDescriptorFileLocation) {
        Map<String,IndexDescriptor> indicies = new HashMap<String,IndexDescriptor>();            

        Element indiciesElement = DomUtil.getChildElement(root, "Indicies");
        if (indiciesElement == null) {
            return;
        }
        NodeList indexNodeList = indiciesElement.getElementsByTagName("Index");
        if (indexNodeList == null) {
            return;
        }
        for (int i = 0; i < indexNodeList.getLength(); i++) {
            Element elem = (Element)indexNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals("Index"), "nodeName.equals('Index')");


            String indexName = DomUtil.getChildElementText(elem, "IndexName"); 
            String columns = DomUtil.getChildElementText(elem, "Columns");
            String uniqueText = DomUtil.getChildElementText(elem, "Unique");
            boolean unique = false;
            if (uniqueText != null) {
            	unique = "yes".equals(uniqueText.toLowerCase());            	
            }

            IndexDescriptor indexDescriptor = new IndexDescriptor();
            indexDescriptor.setIndexName(indexName);
            indexDescriptor.setColumns(columns);
            indexDescriptor.setUnique(unique);

            Object previous = indicies.put(indexName, indexDescriptor); 
            Assert.check(previous == null, "Duplicate Index \"" + indexName + "\" found in " + objectDescriptorFileLocation);
        }
        
        objectDescriptor.setIndicies(indicies);
    }
    
    
    private int parseSize(String sizeString) {
        try {
            if (sizeString == null) {
                return 0;
            }
            NumberFormat nf = NumberFormat.getInstance();
            Number numberSize = nf.parse(sizeString);                
            return numberSize.intValue();
        } catch (ParseException e) {
            throw new RuntimeException("Bad Field size found while parsing: " + valueObjectInterface, e);                        
        }
    }

    
}
