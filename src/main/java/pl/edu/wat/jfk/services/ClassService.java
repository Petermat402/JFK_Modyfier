package pl.edu.wat.jfk.services;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import pl.edu.wat.jfk.exceptions.WrongPathException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

public class ClassService {
    private JarService jarService;
    private ModifierService modifierService;

    public ClassService(JarService jarService, ModifierService modifierService) {
        this.jarService = jarService;
        this.modifierService = modifierService;
    }

    public void addClass(String className) throws CannotCompileException, IOException, NotFoundException {
        ClassPool classPool = jarService.getClassPool();
        int modifier = modifierService.getModifier(className);
        className = modifierService.eliminateRedundantModifierWords(className);

        CtClass superClass = getSuperClass(className, classPool);
        List<CtClass> interfaces = getInterfaces(className, classPool);

        String newClassName = className.split("implements")[0].split(" extends ")[0];
        CtClass ctClass = classPool.makeClass(newClassName);
        newClassName = newClassName.replace(".","/");
        ctClass.setModifiers(modifier);
        if (superClass != null) {
            ctClass.setSuperclass(superClass);
        }
        if(!interfaces.isEmpty()) {
            ctClass.setInterfaces(interfaces.toArray(new CtClass[interfaces.size()]));
        }
        ctClass.writeFile("./newApplication/");
        jarService.updateJarEntries(new JarEntry(newClassName + ".class"));
    }

    public void removeClass(String classPath) throws WrongPathException, NotFoundException {
        ClassPool classPool = jarService.getClassPool();
        String newClassPath = jarService.adjustClassPath(classPath);
        CtClass ctClass = classPool.get(newClassPath);
        ctClass.defrost();
        ctClass.detach();
        jarService.removeFile(classPath);
        jarService.removeJarEntry(new JarEntry(classPath));
    }

    protected CtClass getSuperClass(String classString, ClassPool classPool) throws NotFoundException {
        CtClass extendedClass = null;
        if (classString.contains("extends")) {
            String superClass = classString.substring(classString.indexOf("extends ") + 8);
            if (superClass.contains("implements")) {
                superClass = superClass.substring(0, superClass.indexOf("implements"));
            }
            extendedClass = classPool.get(superClass.replace(" ", ""));

        }
        return extendedClass;
    }

    protected List<CtClass> getInterfaces(String classString, ClassPool classPool) throws NotFoundException {
        List<CtClass> interfacesToImplement = new ArrayList<>();
        if(classString.contains("implements")) {
            String interfaces = classString.substring(classString.indexOf("implements ") + 11);
            if(interfaces.contains("extends")) {
                interfaces = interfaces.substring(0, interfaces.indexOf("extends"));
            }
            interfaces = interfaces.replace(" ", "");
            while(interfaces.contains(",")) {
                String interfaceName = interfaces.substring(0,interfaces.indexOf(","));
                interfaces = interfaces.replace(interfaceName + ",", "");
                interfacesToImplement.add(classPool.get(interfaceName));
            }
            interfacesToImplement.add(classPool.get(interfaces));
        }
        return interfacesToImplement;
    }
}
