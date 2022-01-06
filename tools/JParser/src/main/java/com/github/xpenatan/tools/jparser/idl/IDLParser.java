package com.github.xpenatan.tools.jparser.idl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/** @author xpenatan */
public class IDLParser {

    public static IDLFile parseFile(String path) {
        IDLFile idlFile = new IDLFile();
        File file = new File(path);    //creates a new file instance
        if(file.exists()) {
            try {
                FileReader fr = new FileReader(file);   //reads the file
                BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
                StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters

                ArrayList<String> lines = new ArrayList<>();
                String line;
                while((line = br.readLine()) != null) {
                    lines.add(line);
                }
                fr.close();    //closes the stream and release the resources

                ArrayList<IDLClass> classList = new ArrayList<>();
                parseFile(lines, classList);
                idlFile.classArray.addAll(classList);
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
        return idlFile;
    }

    private static void parseFile(ArrayList<String> lines, ArrayList<IDLClass> classList) {
        ArrayList<String> classLines = new ArrayList<>();
        boolean foundStartClass = false;
        int size = lines.size();
        for(int i = 0; i < size; i++) {
            String line = lines.get(i).trim();
            if(line.startsWith("//") || line.isEmpty())
                continue;

            boolean justAdded = false;

            if(!foundStartClass) {
                if(line.startsWith("interface ") || line.startsWith("[Prefix") || line.startsWith("[JSImplementation") || line.startsWith("[NoDelete")) {
                    foundStartClass = true;
                    classLines.clear();
                    justAdded = true;
                    classLines.add(line);
                }
            }
            if(foundStartClass) {
                if(!justAdded) {
                    classLines.add(line);
                }
                if(line.endsWith("};")) {
                    int nextLineIdx = i + 1;
                    if(nextLineIdx < size) {
                        String nextLine = lines.get(nextLineIdx);
                        if(nextLine.contains(" implements ")) {
                            classLines.add(nextLine.trim());
                            i++; // add i so nextLine is not skipped on next loop
                        }
                    }
                    foundStartClass = false;
                    IDLClass parserLineClass = new IDLClass();
                    parserLineClass.initClass(classLines);
                    classLines.clear();
                    classList.add(parserLineClass);
                }
            }
        }
    }
}