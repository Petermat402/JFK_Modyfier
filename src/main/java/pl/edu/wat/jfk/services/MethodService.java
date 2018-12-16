package pl.edu.wat.jfk.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javassist.*;
import pl.edu.wat.jfk.exceptions.WrongPathException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

import static java.util.Arrays.asList;

public class MethodService {
    private List<CtMethod> ctMethods = new ArrayList<>();

    private JarService jarService;
    private ModifierService modifierService;

    public MethodService(JarService jarService, ModifierService modifierService) {
        this.modifierService = modifierService;
        this.jarService = jarService;
    }

    public ObservableList<String> getMethods(String classPath) throws WrongPathException {
        classPath = jarService.adjustClassPath(classPath);
        ClassPool classPool = jarService.getClassPool();
        ObservableList<String> methods = FXCollections.observableArrayList();
        try {
            CtClass ctClass = classPool.get(classPath);
            this.ctMethods = asList(ctClass.getDeclaredMethods());
            for (CtMethod method : this.ctMethods) {
                String methodString = method.getLongName().replace(classPath + ".", "");
                methods.add(methodString);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return methods;
    }

    public void addMethod(String classPath, String newMethod) throws WrongPathException, CannotCompileException, NotFoundException, IOException {
        ClassPool classPool = jarService.getClassPool();
        String newClassPath = jarService.adjustClassPath(classPath);
        CtClass ctClass = classPool.get(newClassPath);
        ctClass.defrost();
        CtMethod ctMethod = CtNewMethod.make(newMethod, ctClass);
        ctClass.addMethod(ctMethod);
        ctClass.writeFile("./application/");
        jarService.updateJarEntries(new JarEntry(classPath));
    }

    public void overwriteMethod(String classPath, String methodName, String methodCode) throws WrongPathException, NoSuchMethodException, CannotCompileException, NotFoundException, IOException {
        ClassPool classPool = jarService.getClassPool();
        String newClassPath = jarService.adjustClassPath(classPath);
        CtClass ctClass = classPool.get(newClassPath);
        ctClass.defrost();
        CtMethod methodToOverwrite = findMethod(methodName, newClassPath);
        methodToOverwrite.setBody(methodCode);
        ctClass.writeFile("./application/");
        jarService.updateJarEntries(new JarEntry(classPath));

    }

    public void insertAtBeginning(String classPath, String methodName, String methodCode) throws WrongPathException, NoSuchMethodException, CannotCompileException, NotFoundException, IOException {
        ClassPool classPool = jarService.getClassPool();
        String newClassPath = jarService.adjustClassPath(classPath);
        CtClass ctClass = classPool.get(newClassPath);
        ctClass.defrost();
        CtMethod methodToModify = findMethod(methodName, newClassPath);
        methodToModify.insertBefore(methodCode);
        ctClass.writeFile("./application/");
        jarService.updateJarEntries(new JarEntry(classPath));
    }

    public void insertAtEnd(String classPath, String methodName, String methodCode) throws WrongPathException, NoSuchMethodException, CannotCompileException, NotFoundException, IOException {
        ClassPool classPool = jarService.getClassPool();
        String newClassPath = jarService.adjustClassPath(classPath);
        CtClass ctClass = classPool.get(newClassPath);
        ctClass.defrost();
        CtMethod methodToModify = findMethod(methodName, newClassPath);
        methodToModify.insertAfter(methodCode);
        ctClass.writeFile("./application/");
        jarService.updateJarEntries(new JarEntry(classPath));

    }

    public void removeMethod(String classPath, String methodName) throws WrongPathException, CannotCompileException, NoSuchMethodException, NotFoundException, IOException {
        ClassPool classPool = jarService.getClassPool();
        String newClassPath = jarService.adjustClassPath(classPath);
        CtClass ctClass = classPool.get(newClassPath);
        ctClass.defrost();
        CtMethod methodToRemove = findMethod(methodName, newClassPath);
        ctClass.removeMethod(methodToRemove);
        ctClass.writeFile("./application/");
        jarService.updateJarEntries(new JarEntry(classPath));

    }

    private CtMethod findMethod(String methodName, String newClassPath) throws NoSuchMethodException {
        CtMethod methodToRemove = null;
        for (CtMethod ctMethod : this.ctMethods) {
            String methodNameFromList = ctMethod.getLongName().replace(newClassPath + ".", "");
            if (methodName.equals(methodNameFromList)) {
                methodToRemove = ctMethod;
                break;
            }
        }
        if (methodToRemove == null) {
            throw new NoSuchMethodException();
        }
        return methodToRemove;
    }
}