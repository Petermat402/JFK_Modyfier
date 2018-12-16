package pl.edu.wat.jfk.services;

import java.lang.reflect.Modifier;
import java.util.HashMap;

public class ModifierService {
    public int getModifier(String declaration) {
        int modifier = 0;
        if(declaration.contains(" public ") || declaration.contains("public ")) {
            modifier += Modifier.PUBLIC;
        }
        if(declaration.contains(" private ") || declaration.contains("private ")) {
            modifier += Modifier.PRIVATE;
        }
        if(declaration.contains(" protected ") || declaration.contains("protected ")) {
            modifier += Modifier.PROTECTED;
        }
        if(declaration.contains(" static ") || declaration.contains("static ")) {
            modifier += Modifier.STATIC;
        }
        if(declaration.contains(" abstract ") || declaration.contains("abstract ")) {
            modifier += Modifier.ABSTRACT;
        }
        if(declaration.contains(" final ") || declaration.contains("final ")) {
            modifier += Modifier.FINAL;
        }
        if(declaration.contains( " strict ") || declaration.contains("strict ")) {
            modifier += Modifier.STRICT;
        }
        if(declaration.contains(" volatile ") || declaration.contains("volatile ")) {
            modifier += Modifier.VOLATILE;
        }
        return modifier;
    }

    public String eliminateRedundantModifierWords(String declaration) {
        String result = declaration;
            result = result.replace("public ", "");
            result = result.replace("private ", "");
            result = result.replace("protected ", "");
            result = result.replace("static ", "");
            result = result.replace("abstract ", "");
            result = result.replace("final ", "");
            result = result.replace("strict ", "");
            result = result.replace("volatile ", "");
            return result;
    }
}
