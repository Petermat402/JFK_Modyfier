package pl.edu.wat.jfk.services;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.IOException;
import java.util.jar.JarEntry;

public class ClassService {
    private JarService jarService;
    private ModifierService modifierService;

    public ClassService(JarService jarService, ModifierService modifierService) {
        this.jarService = jarService;
        this.modifierService = modifierService;
    }

    public void addClass(String className) throws CannotCompileException, IOException {
        ClassPool classPool = jarService.getClassPool();
        int modifier = modifierService.getModifier(className);
        className = modifierService.eliminateRedundantModifierWords(className);
        CtClass ctClass = classPool.makeClass(className);
        ctClass.setModifiers(modifier);
        ctClass.writeFile("./application/");
        jarService.updateJarEntries(new JarEntry(className + ".class"));
    }
}
