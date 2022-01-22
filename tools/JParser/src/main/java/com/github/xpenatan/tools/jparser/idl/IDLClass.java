package com.github.xpenatan.tools.jparser.idl;

import java.util.ArrayList;

/** @author xpenatan */
public class IDLClass {
    public String name;
    public String prefixName;
    public String jsImplementation;

    public final ArrayList<String> classLines = new ArrayList<>();
    public final ArrayList<IDLConstructor> constructors = new ArrayList<>();
    public final ArrayList<IDLMethod> methods = new ArrayList<>();
    public final ArrayList<IDLAttribute> attributes = new ArrayList<>();

    public void initClass(ArrayList<String> lines) {
        classLines.addAll(lines);
        setInterfaceName();
        setPrefixName();
        setJsImplementation();
        setAttributesAndMethods();
    }

    private void setAttributesAndMethods() {
        for(int i = 1; i < classLines.size(); i++) {
            String line = classLines.get(i);
            if(line.contains("attribute ")) {
                IDLAttribute attribute = new IDLAttribute();
                attribute.initAttribute(line);
                attributes.add(attribute);
            }
            else {
                if(line.startsWith("void " + name)) {
                    IDLConstructor constructor = new IDLConstructor();
                    constructor.initConstructor(line);
                    constructors.add(constructor);
                }
                else {
                    if(line.contains("(") && line.contains(")")) {
                        IDLMethod method = new IDLMethod();
                        method.initMethod(line);
                        methods.add(method);

                        int totalOptionalParams = method.getTotalOptionalParams();
                        if(totalOptionalParams > 0) {
                            for(int j = 0; j < totalOptionalParams; j++) {
                                IDLMethod clone = method.clone();
                                clone.removeLastParam(j+1);
                                methods.add(clone);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setInterfaceName() {
        String line = searchLine("interface ", true, false);
        if(line != null) {
            name = line.split(" ")[1];
        }
    }

    private void setJsImplementation() {
        String prefix = "[JSImplementation=";
        String line = searchLine(prefix, true, false);
        if(line != null) {
            jsImplementation = line.replace(prefix, "").replace("]", "").replace("\"", "");
        }
    }

    private void setPrefixName() {
        String prefix = "[Prefix =";
        String line = searchLine(prefix, true, false);
        if(line != null) {
            prefixName = line.replace(prefix, "").replace("]", "").replace("\"", "");
        }
    }

    private String searchLine(String text, boolean startsWith, boolean endsWith) {
        for(int i = 0; i < classLines.size(); i++) {
            String line = classLines.get(i);

            if(startsWith) {
                if(line.startsWith(text)) {
                    return line;
                }
            }
            else if(endsWith) {
                if(line.endsWith(text)) {
                    return line;
                }
            }
            else {
                if(line.contains(text)) {
                    return line;
                }
            }
        }
        return null;
    }
}