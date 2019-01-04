package pl.edu.wat.jfk.services;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import pl.edu.wat.jfk.exceptions.WrongPathException;

import java.io.File;
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

    public void removeClass(String classPath) throws WrongPathException {
        ClassPool classPool = jarService.getClassPool();
        //classPool.removeClassPath();
        File file = new File(classPath);
        System.out.println(file.delete());
        String newClasspath = jarService.adjustClassPath(classPath);
        jarService.removeJarEntry(new JarEntry(classPath));

    }
}
