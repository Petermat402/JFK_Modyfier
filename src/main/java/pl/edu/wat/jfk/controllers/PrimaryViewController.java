package pl.edu.wat.jfk.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import pl.edu.wat.jfk.exceptions.WrongPathException;
import pl.edu.wat.jfk.services.*;

import java.io.IOException;
import java.util.jar.JarEntry;

public class PrimaryViewController {

    private static JarService jarService;
    private static FieldService fieldService;
    private static ConstructorService constructorService;
    private static MethodService methodService;
    private static ClassService classService;
    private static PackageService packageService;

    @FXML
    private ListView classList;
    @FXML
    private TextField jarPath;
    @FXML
    private ListView fieldList;
    @FXML
    private ComboBox choiceBar;
    @FXML
    private ComboBox classChoice;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button overwriteButton;
    @FXML
    private TextArea textArea;
    @FXML
    private Label actionLabel;
    @FXML
    private Button addBeginningButton;
    @FXML
    private Button addEndButton;
    @FXML
    private Circle indicatorCircle;
    @FXML
    private Button addClassButton;
    @FXML
    private Button removeClassButton;


    public static void init(JarService jarService1) {
        jarService = jarService1;
        ModifierService modifierService = new ModifierService();
        fieldService = new FieldService(jarService, modifierService);
        constructorService = new ConstructorService(jarService);
        methodService = new MethodService(jarService, modifierService);
        packageService = new PackageService(jarService);
        classService = new ClassService(jarService, modifierService);
    }

    private void addChoices() {
        ObservableList<String> choices = FXCollections.observableArrayList();
        ObservableList<String> classChoices = FXCollections.observableArrayList();

        choices.add("Constructors");
        choices.add("Fields");
        choices.add("Methods");
        choiceBar.setItems(choices);

        classChoices.add("Class");
        classChoices.add("Interface");
        classChoices.add("Package");
        classChoice.setItems(classChoices);
    }
    private void handleException(Exception e) {
        indicatorCircle.setFill(Color.RED);
        System.err.println(e.toString() + "\n\n");
        e.printStackTrace();
    }

    private void handleSuccess() {
        indicatorCircle.setFill(Color.GREEN);
    }

    public void loadJar() {
        progressIndicator.setVisible(true);
        try {
            classList.setItems(jarService.openNewJar(jarPath.getText()));
            handleSuccess();
        } catch (WrongPathException | IOException e) {
            handleException(e);
        }
        addChoices();
    }

    public void chooseField() {
        fieldList.getItems().clear();
        try {
            JarEntry jarEntry = (JarEntry) classList.getSelectionModel().getSelectedItems().get(0);
            actionLabel.setText((String)choiceBar.getSelectionModel().getSelectedItem());
        switch (choiceBar.getSelectionModel().getSelectedIndex()) {
                case 0: {
                    switchEnablenessButtons(0);
                    fieldList.setItems(constructorService.getConstructors(jarEntry.getName()));
                    return;
                }
                case 1: {
                    switchEnablenessButtons(1);
                    fieldList.setItems(fieldService.getFields(jarEntry.getName()));
                    return;
                }
                case 2: {
                    switchEnablenessButtons(2);
                    fieldList.setItems(methodService.getMethods(jarEntry.getName()));
                }
            }
        }catch (WrongPathException e) {
           handleException(e);
        }
    }

    private void switchEnablenessButtons(int choiceBarIndex) {
        switch(choiceBarIndex){
            case 0 : {
                overwriteButton.setDisable(false);
                addBeginningButton.setDisable(true);
                addEndButton.setDisable(true);
                return;
            }
            case 1 : {
                overwriteButton.setDisable(true);
                addBeginningButton.setDisable(true);
                addEndButton.setDisable(true);
                return;
            }
            case 2 : {
                overwriteButton.setDisable(false);
                addBeginningButton.setDisable(false);
                addEndButton.setDisable(false);
            }
        }
    }

    public void addAction() {
        switch (choiceBar.getSelectionModel().getSelectedIndex()) {
            case 0: {
                addConstructor();
                break;
            }
            case 1 : {
                addField();
                break;
            }
            case 2 : {
                addMethod();
                break;
            }
        }
    }
    public void removeAction() {
        switch (choiceBar.getSelectionModel().getSelectedIndex()) {
            case 0: {
                removeConstructor();
                break;
            }
            case 1 : {
                removeField();
                break;
            }
            case 2 : {
                removeMethod();
                break;
            }
        }
    }
    public void overwriteAction() {
        switch (choiceBar.getSelectionModel().getSelectedIndex()) {
            case 0: {
                overwriteConstructor();
                break;
            }
            case 1 : {
                return;
            }
            case 2 : {
                overwriteMethod();
                break;
            }
        }
    }

    public void addClassAction() {
        switch (classChoice.getSelectionModel().getSelectedIndex()) {
            case 0: {
                addClass();
                break;
            }
            case 1 : {
                addInterface();
                break;
            }
            case 2 : {
                addPackage();
                break;
            }
        }
    }
    public void removeClassAction() {
        switch (classChoice.getSelectionModel().getSelectedIndex()) {
            case 0: {
                removeClass();
                break;
            }
            case 1 : {
                removeInterface();
                break;
            }
            case 2 : {
                removePackage();
                break;
            }
        }
    }

    public void addAtBeginningMethod() {
        try {
            JarEntry jarEntry = (JarEntry) classList.getSelectionModel().getSelectedItems().get(0);
            methodService.insertAtBeginning(jarEntry.getName(), (String) fieldList.getSelectionModel().getSelectedItem(), textArea.getText());
            classList.setItems(jarService.getJarEntries());
            handleSuccess();
        } catch (WrongPathException | CannotCompileException | NoSuchMethodException | IOException | NotFoundException e) {
            handleException(e);
        }
    }
    public void addAtEndMethod() {
        try {
            JarEntry jarEntry = (JarEntry) classList.getSelectionModel().getSelectedItems().get(0);
            methodService.insertAtEnd(jarEntry.getName(), (String) fieldList.getSelectionModel().getSelectedItem(), textArea.getText());
            classList.setItems(jarService.getJarEntries());
            handleSuccess();
        } catch (WrongPathException | CannotCompileException | NoSuchMethodException | IOException | NotFoundException e) {
            handleException(e);
        }
    }

    private void addConstructor() {
        try{
            JarEntry jarEntry = (JarEntry) classList.getSelectionModel().getSelectedItems().get(0);
            constructorService.addConstructor(jarEntry.getName(), textArea.getText());
            classList.setItems(jarService.getJarEntries());
            handleSuccess();
        } catch (WrongPathException | CannotCompileException | IOException | NotFoundException e){
            handleException(e);
        }

    }
    private void removeConstructor() {
        try{
            JarEntry jarEntry = (JarEntry) classList.getSelectionModel().getSelectedItems().get(0);
            constructorService.removeConstructor(jarEntry.getName(), (String) fieldList.getSelectionModel().getSelectedItem());
            classList.setItems(jarService.getJarEntries());
            handleSuccess();
        } catch (WrongPathException | CannotCompileException | IOException | NotFoundException | NoSuchMethodException e){
            handleException(e);
        }
    }
    private void overwriteConstructor() {
        try{
            JarEntry jarEntry = (JarEntry) classList.getSelectionModel().getSelectedItems().get(0);
            constructorService.overwriteConstructor(jarEntry.getName(), (String) fieldList.getSelectionModel().getSelectedItem(), textArea.getText());
            classList.setItems(jarService.getJarEntries());
            handleSuccess();
        } catch (WrongPathException | CannotCompileException | IOException | NotFoundException | NoSuchMethodException e){
            handleException(e);
        }
    }
    private void addField() {
        JarEntry jarEntry = (JarEntry) classList.getSelectionModel().getSelectedItems().get(0);
        try {
            fieldService.addField(jarEntry.getName(), textArea.getText());
            classList.setItems(jarService.getJarEntries());
            handleSuccess();
        } catch (WrongPathException | CannotCompileException | IOException | NotFoundException e) {
            handleException(e);
        }
    }
    private void removeField() {
        JarEntry jarEntry = (JarEntry) classList.getSelectionModel().getSelectedItems().get(0);
        try {
            String selectedField = (String) fieldList.getSelectionModel().getSelectedItems().get(0);
            fieldService.removeField(jarEntry.getName(), selectedField.substring(selectedField.lastIndexOf(" ") + 1));
            classList.setItems(jarService.getJarEntries());
            handleSuccess();
        } catch (WrongPathException | CannotCompileException | NoSuchFieldException | IOException | NotFoundException e) {
            handleException(e);
        }
    }
    private void addMethod() {
        try{
            JarEntry jarEntry = (JarEntry) classList.getSelectionModel().getSelectedItems().get(0);
            methodService.addMethod(jarEntry.getName(), textArea.getText());
            classList.setItems(jarService.getJarEntries());
            handleSuccess();
        } catch (WrongPathException | CannotCompileException | IOException | NotFoundException e){
            handleException(e);
        }
    }
    private void removeMethod() {
        try {
            JarEntry jarEntry = (JarEntry) classList.getSelectionModel().getSelectedItems().get(0);
            methodService.removeMethod(jarEntry.getName(), (String) fieldList.getSelectionModel().getSelectedItem());
            classList.setItems(jarService.getJarEntries());
            handleSuccess();
        } catch (WrongPathException | CannotCompileException | NoSuchMethodException | IOException | NotFoundException e) {
            handleException(e);
        }
    }
    private void overwriteMethod() {
        try {
            JarEntry jarEntry = (JarEntry) classList.getSelectionModel().getSelectedItems().get(0);
            methodService.overwriteMethod(jarEntry.getName(), (String) fieldList.getSelectionModel().getSelectedItem(), textArea.getText());
            classList.setItems(jarService.getJarEntries());
            handleSuccess();
        } catch (WrongPathException | CannotCompileException | NoSuchMethodException | IOException | NotFoundException e) {
            handleException(e);
        }
    }

    private void addClass() {
        try {
            classService.addClass(textArea.getText());
            handleSuccess();
        } catch (CannotCompileException | IOException e) {
            handleException(e);
        }
    }
    private void removeClass() {}
    private void addInterface() {}
    private void removeInterface() {}
    private void addPackage() {}
    private void removePackage() {}

    public void exportJar() {
        try {
            jarService.exportJar();
            handleSuccess();
        } catch (IOException e) {
            handleException(e);
        }
    }



}
