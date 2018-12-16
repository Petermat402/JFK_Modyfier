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

public class FieldService {
    private List<CtField> fields = new ArrayList<>();
    private JarService jarService;
    private ModifierService modifierService;

    public FieldService(JarService jarService, ModifierService modifierService) {
        this.jarService = jarService;
        this.modifierService = modifierService;
    }

    public ObservableList<String> getFields(String classPath) throws WrongPathException {
        classPath = jarService.adjustClassPath(classPath);
        ClassPool classPool = jarService.getClassPool();
        ObservableList<String> resultFields = FXCollections.observableArrayList();
        try {
            CtClass ctClass = classPool.get(classPath);
            this.fields = asList(ctClass.getDeclaredFields());

            for (CtField field : this.fields) {
                String fieldType = field.getType().getName();
                resultFields.add(fieldType.substring(fieldType.lastIndexOf(".") + 1) + " " + field.getName());
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return resultFields;
    }

    public void addField(String classPath, String declaration) throws WrongPathException, CannotCompileException, NotFoundException , IOException {
        ClassPool classPool = jarService.getClassPool();
        String newClassPath = jarService.adjustClassPath(classPath);
            String initializer = "";
            if (declaration.contains("=")) {
                initializer = declaration.substring(declaration.indexOf("=") + 1).replace(";", "");
                declaration = declaration.substring(0, declaration.indexOf("="));
            }
            int modifier = modifierService.getModifier(declaration);
            declaration = modifierService.eliminateRedundantModifierWords(declaration);
            String type = declaration.substring(0, declaration.indexOf(" "));
            String name = declaration.substring(declaration.indexOf(" ") + 1);
            name = name.replace(" ", "").replace(";", "");

            CtClass ctClass = classPool.get(newClassPath);
            ctClass.defrost();
            CtField ctField = new CtField(classPool.get(type), name, ctClass);
            ctField.setModifiers(modifier);

            if (initializer.isEmpty()) {
                ctClass.addField(ctField);
            } else {
                ctClass.addField(ctField, initializer);
            }
            ctClass.writeFile("./application/");
            jarService.updateJarEntries(new JarEntry(classPath));
    }

    public void removeField(String classPath, String fieldName) throws WrongPathException, CannotCompileException, NoSuchFieldException, NotFoundException , IOException{
        ClassPool classPool = jarService.getClassPool();
        String newClassPath = jarService.adjustClassPath(classPath);
            CtClass ctClass = classPool.get(newClassPath);
            ctClass.defrost();
            CtField fieldToRemove = null;
            for(CtField field : this.fields) {
                if(fieldName.equals(field.getName())) {
                    fieldToRemove = field;
                    break;
                }
            }
            if(fieldToRemove == null) {
                throw new NoSuchFieldException();
            }
            ctClass.removeField(fieldToRemove);
            ctClass.writeFile("./application/");
            jarService.updateJarEntries(new JarEntry(classPath));
    }

}
