package com.github.xpenatan.tools.jparser.idl;

import java.util.ArrayList;

/** @author xpenatan */
public class IDLFile {
    public final ArrayList<IDLClass> classArray = new ArrayList<>();

    public IDLClass getClass(String name) {
        int size = classArray.size();
        for(int i = 0; i < size; i++) {
            IDLClass idlClass = classArray.get(i);
            if(idlClass.interfaceName.equals(name)) {
                return idlClass;
            }
        }
        return null;
    }
}