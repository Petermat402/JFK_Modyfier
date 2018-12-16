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

public class ConstructorService {
    private List<CtConstructor> constructors = new ArrayList<>();
    private JarService jarService;
    public ConstructorService(JarService jarService) {
        this.jarService = jarService;
    }

    public ObservableList<String> getConstructors(String classPath) throws WrongPathException {
        classPath = jarService.adjustClassPath(classPath);
        ClassPool classPool = jarService.getClassPool();
        ObservableList<String> constructors = FXCollections.observableArrayList();
        try {
            CtClass ctClass = classPool.get(classPath);
            this.constructors = asList(ctClass.getDeclaredConstructors());
            for (CtConstructor constructor : this.constructors) {
                constructors.add(constructor.getLongName().replace(classPath + ".", ""));
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return constructors;
    }

    public void addConstructor(String classPath, String newConstructor) throws WrongPathException, NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = jarService.getClassPool();
        String newClassPath = jarService.adjustClassPath(classPath);
        CtClass ctClass = classPool.get(newClassPath);
        ctClass.defrost();
        CtConstructor ctConstructor = CtNewConstructor.make(newConstructor, ctClass);
        ctClass.addConstructor(ctConstructor);
        ctClass.writeFile("./application/");
        jarService.updateJarEntries(new JarEntry(classPath));
    }

    public void removeConstructor(String classPath, String constructorName) throws WrongPathException, NotFoundException, CannotCompileException, IOException, NoSuchMethodException {
        ClassPool classPool = jarService.getClassPool();
        String newClassPath = jarService.adjustClassPath(classPath);
        CtClass ctClass = classPool.get(newClassPath);
        ctClass.defrost();
        CtConstructor ctConstructor = findConstructor(constructorName, newClassPath);
        ctClass.removeConstructor(ctConstructor);
        ctClass.writeFile("./application/");
        jarService.updateJarEntries(new JarEntry(classPath));
    }

    public void overwriteConstructor(String classPath, String constructorName, String constructorCode) throws WrongPathException, NotFoundException, CannotCompileException, IOException, NoSuchMethodException {
        ClassPool classPool = jarService.getClassPool();
        String newClassPath = jarService.adjustClassPath(classPath);
        CtClass ctClass = classPool.get(newClassPath);
        ctClass.defrost();
        CtConstructor ctConstructor = findConstructor(constructorName, newClassPath);
        ctConstructor.setBody(constructorCode);
        ctClass.writeFile("./application/");
        jarService.updateJarEntries(new JarEntry(classPath));
    }

    private CtConstructor findConstructor(String constructorName, String newClassPath) throws NoSuchMethodException {
        CtConstructor constructorToFind = null;
        for (CtConstructor ctConstructor : this.constructors) {
            String methodNameFromList = ctConstructor.getLongName().replace(newClassPath + ".", "");
            if (constructorName.equals(methodNameFromList)) {
                constructorToFind = ctConstructor;
                break;
            }
        }
        if (constructorToFind == null) {
            throw new NoSuchMethodException();
        }
        return constructorToFind;
    }
}
