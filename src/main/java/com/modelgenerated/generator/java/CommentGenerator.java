/*
 * Created on Feb 2, 2006
 *
 */
package com.modelgenerated.generator.java;

import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.util.StringUtil;

/**
 * @author kevin
 *
 */
public class CommentGenerator {


    public static void writeJavaDocComment(CodeBuffer code, String indent, String description) {
        if (!StringUtil.isEmpty(description)) {
            code.addLine(indent + "/**");
            String delimiter = "\n";
            String[] descriptionList = description.split(delimiter);
            for (int indx = 0; indx < descriptionList.length; indx++) {
            	String line = descriptionList[indx].trim().replaceAll("\\[p\\]", "<p>");
            	line = line.trim().replaceAll("\\[/p\\]", "</p>");
            	line = line.trim().replaceAll("\\[br\\]", "<br>");
                code.addLine(indent + "  * " + line);
            }
            code.addLine(indent + "  */");
        }
    }


}
