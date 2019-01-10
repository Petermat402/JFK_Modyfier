package pl.edu.wat.jfk.services;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import pl.edu.wat.jfk.exceptions.WrongPathException;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;

public class IntefaceService {
    private JarService jarService;
    private ModifierService modifierService;

    public IntefaceService(JarService jarService, ModifierService modifierService) {
        this.jarService = jarService;
        this.modifierService = modifierService;
    }

    public void addInterface(String intefaceName) throws CannotCompileException, IOException {
        ClassPool classPool = jarService.getClassPool();
        int modifier = modifierService.getModifier(intefaceName);
        intefaceName = modifierService.eliminateRedundantModifierWords(intefaceName);
        CtClass ctClass = classPool.makeInterface(intefaceName);
        ctClass.setModifiers(modifier);
        ctClass.writeFile("./application/");
        jarService.updateJarEntries(new JarEntry(intefaceName + ".class"));
    }

    public void removeInterface(String classPath) throws WrongPathException, NotFoundException {
        ClassPool classPool = jarService.getClassPool();
        String newClassPath = jarService.adjustClassPath(classPath);
        CtClass ctClass = classPool.get(newClassPath);
        ctClass.defrost();
        ctClass.detach();
        jarService.removeFile(classPath);
        jarService.removeJarEntry(new JarEntry(classPath));
    }
}
