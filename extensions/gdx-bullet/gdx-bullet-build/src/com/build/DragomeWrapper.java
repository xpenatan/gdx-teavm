package com.build;

import com.escplay.codegen.XpeCodeWrapper;

/** @author xpenatan */
public class DragomeWrapper implements XpeCodeWrapper {

	String indent;
	String finalStr;
	String tmpStr;
	String strStatic;
	
	StringBuilder builder = new StringBuilder();
	
	boolean block = false;
	boolean first = false;
	
	static String getIdent(String content) {
		String ident = "";
		for(int i = 0; i < content.length();i++) {
			char c1 = content.charAt(i);
			if(c1 == ' ' || c1 == '\t') {
				if(c1 == '\t')
					ident+= "\t";
			}
			else
				break;
		}
		return ident;
	}
	
	@Override
	public String wrap(boolean isStatic, String className, String method, String returnType, String params, String content,	String indentLevel) {
		strStatic = null;
		if (isStatic)
			strStatic = "null";
		else
			strStatic = "this";
		indent = indentLevel + "\t";
		finalStr = "";
		tmpStr = "";
		
		final boolean forceGrouping = true;
		final boolean ignoreJavaGrouping = true;
		
		block = forceGrouping;
		first = forceGrouping;
		String[] lines = content.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if(line.trim().isEmpty())
				continue;

			if(line.contains("#J") == false) {
				builder.append(line);
				removeSpaces();
				line = builder.toString();
				builder.setLength(0);
			}
			String idnt = getIdent(line);
			boolean removeBlock = false;
			boolean javaCode = false;
			boolean ignoreReturn = false;
			boolean containsReturn = false;
			
			if (line.contains("#B")) {
				line = line.replace("#B", "");
				if(block == false) {
					first = true;
					block = true;
				}
			}
			if (line.contains("#E") && line.contains("#EVAL") == false) {
				line = line.replace("#E", "");
				if(!forceGrouping && block)
					removeBlock = true;
			}
			
			if (line.contains("#I")) {
				line = line.replace("#I", "");
				ignoreReturn = true;
			}
			
			if (line.contains("#J")) {
				line = line.replace("#J", "");
				line = line.trim();
				if (line.isEmpty())
					continue;
				line = (idnt + line + "\n");
				javaCode = true;
			} 
			else if(line.contains("#P")) {
				
				javaCode = true;
				line = line.replace("#P", "").trim();
				String str1 = null;
				String str2 = null;
				
				if(line.contains(",")) {
					String[] split = line.split(",");
					str1 = split[0].trim();
					str2 = split[1].trim();
				}
				else {
					str1 = line.replace("this.", "");
					str2 = line;
				}
				line = idnt + "com.dragome.commons.javascript.ScriptHelper.put(\"" + str1 + "\"," + str2 + "," + strStatic  + ");" + "\n";
			}
			else if(line.contains("#EVAL")) {
				
				if(line.contains("=")) {
					String evalType = "";
					if(line.contains("#EVALFLOAT")) {
						line = line.replace("#EVALFLOAT", "");
						evalType = "Float";
					}
					else if(line.contains("#EVALDOUBLE")) {
						line = line.replace("#EVALDOUBLE", "");
						evalType = "Double";
					}
					else if(line.contains("#EVALINT")) {
						line = line.replace("#EVALINT", "");
						evalType = "Int";
					}
					else if(line.contains("#EVALLONG")) {
						line = line.replace("#EVALLONG", "");
						evalType = "Long";
					}
					else if(line.contains("#EVALBOOLEAN")) {
						line = line.replace("#EVALBOOLEAN", "");
						evalType = "Boolean";
					}
					else if(line.contains("#EVALCHAR")) {
						line = line.replace("#EVALCHAR", "");
						evalType = "Char";
					}
					else if(line.contains("#EVALCASTING")) {
						line = line.replace("#EVALCASTING", "");
						evalType = "Casting";
					}
					else
						line = line.replace("#EVAL", "");
					javaCode = true;
					String[] split = line.split("=");
					
					if(evalType.equals("Casting")) {
						String str1 = split[0].trim();
						String str2 = split[1].trim();
						String str3 = split[2].trim();
						line = idnt + str1 + " = com.dragome.commons.javascript.ScriptHelper.eval" + evalType + "(\"" + str2 + "\"," + str3  + "," + strStatic  + ");" + "\n";
					}
					else {
						String str1 = split[0].trim();
						String str2 = split[1].trim();
						String str3 = "";
						
						if(split.length == 3)
							str3 = "(" + split[2].trim() + ")";
						line = idnt + str1 + " = " + str3 + "com.dragome.commons.javascript.ScriptHelper.eval" + evalType + "(\"" + str2 + "\"," + strStatic  + ");" + "\n";
					}
				}
				else
					throw new IllegalArgumentException("#EVAL is wrong");
			}
			else {
				if (line.contains("return") && ignoreReturn == false) {
					containsReturn = true;
					if (block) {
						if (tmpStr.isEmpty() == false) {
							if(block && ignoreJavaGrouping == false)
								helper(false, first);
							else {
								helper(true, true);
								finalStr += indent;
							}
						} 
						else
							first = true;
					}

					line = line.replace("return", "");
					line = line.trim();
					if (line.isEmpty())
						continue;
					if (returnType.equals("int"))
						line = (idnt + "return com.dragome.commons.javascript.ScriptHelper.evalInt(\"" + line + "\"," + strStatic + ");" + "\n");
					else if (returnType.equals("float"))
						line = (idnt + "return com.dragome.commons.javascript.ScriptHelper.evalFloat(\"" + line + "\"," + strStatic + ");" + "\n");
					else if (returnType.equals("long"))
						line = (idnt + "return com.dragome.commons.javascript.ScriptHelper.evalLong(\"" + line + "\","	+ strStatic + ");" + "\n");
					else if (returnType.equals("boolean"))
						line = (idnt + "return com.dragome.commons.javascript.ScriptHelper.evalBoolean(\"" + line + "\"," + strStatic + ");" + "\n");
					else if (returnType.equals("double"))
						line = (idnt + "return com.dragome.commons.javascript.ScriptHelper.evalDouble(\"" + line + "\"," + strStatic + ");" + "\n");
				} else if (block == false) {
					line = line.trim();
					if (line.isEmpty())
						continue;
					line = (idnt + "com.dragome.commons.javascript.ScriptHelper.evalNoResult(\"" + line + "\"," + strStatic + ");" + "\n");
				}
				else if(first) {
					first = false;
					indent = idnt;
				}
			}
			
			boolean skipBlock = true;
			
			if(javaCode && ignoreJavaGrouping)
				skipBlock = false;
			
			if (block && skipBlock) {
				line = line.replace("\n", "").trim();
				if (javaCode) {
					if (tmpStr.isEmpty() == false) {
						helper(false, first);
						finalStr += line;
					} else {
						if (first) {
							first = false;
							finalStr += (indent + line);
						} else {
							finalStr += line;
						}
					}
				} else {
					if(containsReturn) {
						if (first) {
							first = false;
							finalStr += (indent + line);
						} 
						else
							finalStr += line;
					}
					else
						tmpStr += line;
				}
				if (removeBlock) {
					block = false;
					if (tmpStr.isEmpty() == false) {
						if(i == lines.length-1)
							helper(false, first);
						else
							helper(true, first);
					}
				}
			} else {
				if (tmpStr.isEmpty() == false) {
					if(javaCode == false && i == lines.length-1)
						helper(false, first);
					else
						helper(true, true);
				}
				if (i + 1 == lines.length)
					line = line.replace("\n", "");
				finalStr += line;
			}
			
			if(i == lines.length-1 && tmpStr.isEmpty() == false)
				helper(false, true);
		}

		if(params.isEmpty() == false && finalStr.contains("ScriptHelper")) {
			String[] split = params.split(",");
			for(int i = 0; i < split.length;i++) {
				String local = split[i].trim();
				String[] splitted = local.split(" ");
				String type = splitted[0];
				String varName = splitted[1];
				if(type.equals("Object") || type.startsWith("int") || type.startsWith("long") || type.startsWith("float") || type.startsWith("double") || type.startsWith("boolean") || type.startsWith("char") ) 
				{ // auto put only for primitives
					String cmd = indent + "com.dragome.commons.javascript.ScriptHelper.put(\"" + varName + "\"," + varName + "," + strStatic + ");" + "\n";
					finalStr = cmd + finalStr;
				}
			}
		}
		
		return finalStr;
	}
	
	public void helper(boolean newline, boolean spaces) {
		String newLine = newline ? "\n":"";
		String toIndent = indent;
		if (spaces == false)
			toIndent = "";
		first = false;
		tmpStr = (toIndent + "com.dragome.commons.javascript.ScriptHelper.evalNoResult(\"" + tmpStr + "\"," + strStatic + ");" + newLine);
		finalStr += (tmpStr);
		tmpStr = "";
	}
	
	void removeSpaces() {
		for (int j = 0; j < builder.length(); j++) {
			char c1 = builder.charAt(j);

			if (c1 == '=') {
				char c0 = builder.charAt(j - 1);
				if (c0 == ' ') {
					builder.deleteCharAt(j - 1);
					j--;
				}
				char c2 = builder.charAt(j + 1);
				if (c2 == ' ') {
					builder.deleteCharAt(j + 1);
					j--;
				}
			}
			if (c1 == ',') {
				char c0 = builder.charAt(j - 1);
				if (c0 == ' ') {
					builder.deleteCharAt(j - 1);
					j--;
				}
				char c2 = builder.charAt(j + 1);
				if (c2 == ' ') {
					builder.deleteCharAt(j + 1);
					j--;
				}
			}
			if (c1 == ';') {
				char c0 = builder.charAt(j - 1);
				if (c0 == ' ') {
					builder.deleteCharAt(j - 1);
					j--;
				}
				if (j + 1 < builder.length()) {
					char c2 = builder.charAt(j + 1);
					if (c2 == ' ') {
						builder.deleteCharAt(j + 1);
						j--;
					}
				}
			}
		}
	}
}
