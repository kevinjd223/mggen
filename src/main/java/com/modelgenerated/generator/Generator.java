/*
 * Generator.java
 *
 * Created on February 17, 2003, 5:33 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator;

import com.modelgenerated.foundation.config.ConfigLocator;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.generator.dataaccessgenerator.DAOInterfaceGenerator;
import com.modelgenerated.generator.dataaccessgenerator.DAOGenerator;
import com.modelgenerated.generator.dataaccessgenerator.DAOTestGenerator;
import com.modelgenerated.generator.dataaccessgenerator.EnumGenerator;
import com.modelgenerated.generator.dataaccessgenerator.MssqlCreateTableGenerator;
import com.modelgenerated.generator.dataaccessgenerator.MssqlCreateTableScriptGenerator;
import com.modelgenerated.generator.dataaccessgenerator.MssqlRecreateIndexesGenerator;
import com.modelgenerated.generator.dataaccessgenerator.MysqlCreateTableGenerator;
import com.modelgenerated.generator.dataaccessgenerator.MysqlCreateTableBatScriptGenerator;
import com.modelgenerated.generator.dataaccessgenerator.MysqlCreateTableScriptGenerator;
import com.modelgenerated.generator.dataaccessgenerator.MysqlRecreateIndexesGenerator;
import com.modelgenerated.generator.dataaccessgenerator.ObjectFieldSizeGenerator;
import com.modelgenerated.generator.dataaccessgenerator.TestDataGenerator;
import com.modelgenerated.generator.dataaccessgenerator.ValueObjectInterfaceGenerator;
import com.modelgenerated.generator.dataaccessgenerator.ValueObjectGenerator;
import com.modelgenerated.generator.dataaccessgenerator.ValueObjectListInterfaceGenerator;
import com.modelgenerated.generator.dataaccessgenerator.ValueObjectListGenerator;
import com.modelgenerated.generator.servicegenerator.ServiceGenerator;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.enums.EnumDescriptor;
import com.modelgenerated.modelmetadata.service.ServiceDescriptor;
import com.modelgenerated.modelmetadata.xml.ModelParser;
import com.modelgenerated.tools.EnvironmentPrinter;
import com.modelgenerated.util.Assert;

import java.util.Iterator;
import java.util.Properties;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
/**
 *
 * @author  kevind
 */
public class Generator {
    
    /** Creates a new instance of Generator */
    public Generator() {
    }
    
    public static void main(java.lang.String[] args) {
    	generateAll();    	
    }
    
    public static void generateAll() {    
        GeneratorEventListListener eventListener = new GeneratorEventListListener();
        try {
            EnvironmentPrinter.printEnvironment();
            GeneratorConfig generatorConfig = (GeneratorConfig)ConfigLocator.findConfig(GeneratorConfig.CONFIG_NAME);
            String javaRoot = generatorConfig.getJavaCodeFolder();
            String sqlRoot = generatorConfig.getDatabaseFolder();
            String testRoot = generatorConfig.getJunitFolder();
            String dataRoot = generatorConfig.getTestDataFolder();
            String objectSizePath = generatorConfig.getObjectFieldSizePath();
            String modelFilePath = generatorConfig.getModelLocation();;
			String modelFileUrl = "file:////" + modelFilePath;
            
            //DataAccessConfig dataAccessConfig = (DataAccessConfig)ConfigLocator.findConfig(DataAccessConfig.CONFIG_NAME);
            System.out.println("#&#&#&#&# modelFilePath: " + modelFilePath);
            Logger.debug(Generator.class.toString(), "modelFilePath: " + modelFilePath);
            
            ModelParser modelParser = new ModelParser();
            Model model = modelParser.parse(modelFileUrl);

            // java object generation
            generate(new ValueObjectInterfaceGenerator(), eventListener, model, javaRoot);
            generate(new ValueObjectGenerator(), eventListener, model, javaRoot);
            generate(new DAOInterfaceGenerator(), eventListener, model, javaRoot);
            generate(new DAOGenerator(), eventListener, model, javaRoot);
            generate(new ValueObjectListInterfaceGenerator(), eventListener, model, javaRoot);
            generate(new ValueObjectListGenerator(), eventListener, model, javaRoot);
            generate(new TestDataGenerator(), eventListener, model, dataRoot);

            // sql script generation
            Logger.debug(Generator.class.toString(), "**** Generate Tables ****");
            generate(new MssqlCreateTableGenerator(), eventListener, model, sqlRoot);
            generate(new MysqlCreateTableGenerator(), eventListener, model, sqlRoot);
            
            MysqlCreateTableScriptGenerator mysqlCreateTableScriptGenerator = new MysqlCreateTableScriptGenerator();
            mysqlCreateTableScriptGenerator.generate(model, sqlRoot);

            MysqlCreateTableBatScriptGenerator mysqlCreateTableBatScriptGenerator = new MysqlCreateTableBatScriptGenerator();
            mysqlCreateTableBatScriptGenerator.generate(model, sqlRoot);

            MssqlCreateTableScriptGenerator mssqlCreateTableScriptGenerator = new MssqlCreateTableScriptGenerator();
            mssqlCreateTableScriptGenerator.generate(model, sqlRoot);
            
            (new MssqlRecreateIndexesGenerator()).generate(model, sqlRoot);
            (new MysqlRecreateIndexesGenerator()).generate(model, sqlRoot);

            // test class generation
            Logger.debug(Generator.class.toString(), "**** Generate DAO Tests ****");
            Logger.debug(Generator.class.toString(), "testRoot:" + testRoot);
            if (generatorConfig.getGenerateDAOTests()) {
                generate(new DAOTestGenerator(), eventListener, model, testRoot);
            }

            Logger.debug(Generator.class.toString(), "**** Object Sizes ****");
            Logger.debug(Generator.class.toString(), "objectSizePath:" + objectSizePath);
            ObjectFieldSizeGenerator objectFieldSizeGenerator = new ObjectFieldSizeGenerator();
            objectFieldSizeGenerator.generate(model, objectSizePath);
            
            generateServices(eventListener, model);
             
            generateEnums(eventListener, model, javaRoot);
             

        } catch (Throwable e) {
            e.printStackTrace();
            //System.exit(-1);
        }
        System.out.println("CODE GENERATION EVENTS: ");
        System.out.println("------------------------");
        boolean errors = false;
        List eventList = eventListener.getList();
        Iterator i = eventList.iterator();
        while (i.hasNext()) {
            GeneratorEvent event = (GeneratorEvent)i.next();            
            System.out.println(event.getData());            
            if (event.getSeverity() == GeneratorEventSeverityEnum.ERROR) {
                errors = true;
            }
        }
        if (errors) {
            System.exit(-1);
        }
    }

    
    private static Properties parameterizeProperties(java.lang.String[] args) {
        // assumes the parameters are name-value pairs
        Properties properties = new Properties(); 
        for (int i = 0; i < args.length; i++) { 
            StringTokenizer tokenizer = new StringTokenizer(args[i], "=");
            Assert.check (tokenizer.countTokens() == 2, "tokenizer.countTokens() != 2");
            properties.setProperty(tokenizer.nextToken(), tokenizer.nextToken());
        }
        return properties;
    }
    
    private static void generate(CodeGenerator codeGenerator, GeneratorEventListener eventListener, Model model, String targetRoot) {
        codeGenerator.setEventListener(eventListener);
        
        Map objectDescriptorMap = model.getObjects();
        
        Iterator i = objectDescriptorMap.values().iterator();
        while (i.hasNext()) {
            ObjectDescriptor objectDescriptor = (ObjectDescriptor)i.next();
            
            codeGenerator.generate(targetRoot, objectDescriptor);            
        }       
    }
    
    private static void generateServices(GeneratorEventListener eventListener, Model model) {
        Map serviceMap = model.getServices();
        
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        
        Iterator i = serviceMap.values().iterator();
        while (i.hasNext()) {
            ServiceDescriptor serviceDescriptor = (ServiceDescriptor)i.next();
            
            serviceGenerator.generate(model, serviceDescriptor);            
            
        }       
    }
    
    private static void generateEnums(GeneratorEventListener eventListener, Model model, String javaRoot) {
        EnumGenerator enumGenerator = new EnumGenerator();
        
        Map enumMap = model.getEnums();
        Iterator i = enumMap.values().iterator();
        while (i.hasNext()) {
            EnumDescriptor enumDescriptor = (EnumDescriptor)i.next();
            
            enumGenerator.generate(javaRoot, enumDescriptor);
        }       
    }
    
}
