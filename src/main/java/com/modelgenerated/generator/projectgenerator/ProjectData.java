/*
 * ProjectData.java
 *
 * Created on June 6, 2004, 2:30 PM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.projectgenerator;

/**
 *
 * @author  kevind
 */
public class ProjectData {
    String projectName;
    String projectDirectory;
    String jarFileName;
    String warFileName;
    String javaSubDirectory = "java"; // default java
    String junitSubDirectory = "junit"; // default junit
    String webSubDirectory = "webapp"; 
    String dataSubDirectory = "data"; 
    String jarSubDirectory = "jar"; 
    
    
    /** Creates a new instance of ProjectData */
    public ProjectData() {
    }
    
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String newProjectName) {
        projectName = newProjectName;
    }
    
    public String getProjectDirectory() {
        return projectDirectory;
    }
    public void setProjectDirectory(String newProjectDirectory) {
        projectDirectory = newProjectDirectory;
    }
    
    public String getJarFileName() {
        return jarFileName;
    }
    public void setJarFileName(String newJarFileName) {
        jarFileName = newJarFileName;
    }
    
    public String getWarFileName() {
        return warFileName;
    }
    public void setWarFileName(String newWarFileName) {
        warFileName = newWarFileName;
    }
    
    public String getJavaSubDirectory() {
        return javaSubDirectory;
    }
    public void setJavaSubDirectory(String newJavaSubDirectory) {
        javaSubDirectory = newJavaSubDirectory;
    }

    public String getJunitSubDirectory() {
        return junitSubDirectory;
    }
    public void setJunitSubDirectory(String newJunitSubDirectory) {
        junitSubDirectory = newJunitSubDirectory;
    }
    
    public String getWebSubDirectory() {
        return webSubDirectory;
    }
    public void setWebSubDirectory(String newWebSubDirectory) {
        webSubDirectory = newWebSubDirectory;
    }
    
    public String getDataSubDirectory() {
        return dataSubDirectory;
    }
    public void setDataSubDirectory(String newDataSubDirectory) {
        dataSubDirectory = newDataSubDirectory;
    }
    
    public String getJarSubDirectory() {
        return jarSubDirectory;
    }
    public void setJarSubDirectory(String newJarSubDirectory) {
        jarSubDirectory = newJarSubDirectory;
    }
    
    
}
