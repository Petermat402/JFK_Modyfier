package pl.edu.wat.jfk.services;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import pl.edu.wat.jfk.exceptions.WrongPathException;

import java.io.IOException;
import java.util.List;
import java.util.jar.JarEntry;

public class IntefaceService {
    private JarService jarService;
    private ModifierService modifierService;
    private ClassService classService;

    public IntefaceService(JarService jarService, ModifierService modifierService, ClassService classService) {
        this.jarService = jarService;
        this.modifierService = modifierService;
        this.classService = classService;
    }

    public void addInterface(String intefaceName) throws CannotCompileException, IOException, NotFoundException {
        ClassPool classPool = jarService.getClassPool();
        int modifier = modifierService.getModifier(intefaceName);
        intefaceName = modifierService.eliminateRedundantModifierWords(intefaceName);

        CtClass superClass = classService.getSuperClass(intefaceName, classPool);
        List<CtClass> interfaces = classService.getInterfaces(intefaceName, classPool);

        String newInterfaceName = intefaceName.split("implements")[0].split(" extends ")[0];

        CtClass ctClass = classPool.makeInterface(newInterfaceName);

        newInterfaceName = newInterfaceName.replace(".", "/");
        if (superClass != null) {
            ctClass.setSuperclass(superClass);
        }
        if (!interfaces.isEmpty()) {
            ctClass.setInterfaces(interfaces.toArray(new CtClass[interfaces.size()]));
        }
        ctClass.writeFile("./newApplication/");
        jarService.updateJarEntries(new JarEntry(newInterfaceName + ".class"));
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
