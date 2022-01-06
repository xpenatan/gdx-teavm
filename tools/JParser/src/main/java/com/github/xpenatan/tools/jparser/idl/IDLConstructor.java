package com.github.xpenatan.tools.jparser.idl;

import java.util.ArrayList;

public class IDLConstructor {
    public String line;
    public String paramsLine;

    public final ArrayList<IDLParameter> parameters = new ArrayList<>();


    public void initConstructor(String line) {
        this.line = line;
        paramsLine = IDLMethod.setParameters(line, parameters);
    }


}
