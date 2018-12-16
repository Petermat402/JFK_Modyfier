package pl.edu.wat.jfk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import pl.edu.wat.jfk.controllers.PrimaryViewController;
import pl.edu.wat.jfk.services.JarService;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        PrimaryViewController.init(new JarService());
        Parent root = FXMLLoader.load(getClass().getResource("/views/primaryView.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
        /*try {
            File file = new File("E:/MY/STUDIA/JFK_Lab/JFK_Modyfier/to_modify/invaders.jar");
            FileInputStream fileInputStream = new FileInputStream(file);
            //FileOutputStream fileOutputStream = new FileOutputStream(file);
            JarInputStream jarInputStream = new JarInputStream(fileInputStream);
            //JarOutputStream jarOutputStream = new JarOutputStream(fileOutputStream);

            ClassPool classPool = new ClassPool();
            classPool.appendClassPath("E:/MY/STUDIA/JFK_Lab/JFK_Modyfier/to_modify/invaders.jar");
            CtClass alien1 = classPool.get("com.diamond.iain.javagame.entities.Aliens$1");
            StringBuilder code = new StringBuilder("public void doNothing(){")
                    .append("int a = 2;")
                    //.append("this.toString();")
                    .append("}");
            CtMethod method = CtNewMethod.make(code.toString(),alien1);
            alien1.addMethod(method);
            //alien1.getConstructors()[0].getMethodInfo().toString();
            System.out.println(alien1.getConstructors()[0].getMethodInfo().getName());
            System.out.println(alien1.getMethods()[0].getMethodInfo().getAttributes().get(0).getName());
            alien1.writeFile();
            //Class alienToLoad = alien1.toClass();
            JarEntry jarEntry;
            while((jarEntry=jarInputStream.getNextJarEntry())!=null){
                System.out.println(jarEntry.toString());

                if(jarEntry.getName().endsWith(".class")){

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }*/


    }
}
