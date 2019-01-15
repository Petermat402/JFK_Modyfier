package pl.edu.wat.jfk.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javassist.*;
import pl.edu.wat.jfk.exceptions.WrongPathException;

import java.io.*;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.*;

public class JarService {

    private String jarPath;
    private JarOutputStream jarOutputStream;
    private JarInputStream jarInputStream;
    private ClassPool classPool;
    private FieldService fieldService;
    private ConstructorService constructorService;
    private MethodService methodService;
    private PackageService packageService;
    private ObservableList<JarEntry> jarEntries;
    private Manifest manifest;
    private ClassPath applicationPath;
    private ClassPath systemPath;

    public JarService() {

    }

    private void unzipJar(String destinationDir, String jarPath) throws IOException {
        File file = new File(jarPath);
        JarFile jar = new JarFile(file);

        // fist get all directories,
        // then make those directory on the destination Path
        for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements(); ) {
            JarEntry entry = enums.nextElement();

            String fileName = destinationDir + File.separator + entry.getName();
            File f = new File(fileName);

            if (fileName.endsWith("/")) {
                f.mkdirs();
            }
        }
        //now create all files
        for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements(); ) {
            JarEntry entry = enums.nextElement();

            String fileName = destinationDir + File.separator + entry.getName();
            File f = new File(fileName);

            if (!fileName.endsWith("/")) {
                BufferedInputStream buffInput = new BufferedInputStream(jar.getInputStream(entry));
                FileOutputStream fos = new FileOutputStream(f);

                byte[] buffer = new byte[1024];
                int count = buffInput.read(buffer);

                while (count != -1) {
                    fos.write(buffer, 0, count);
                    count = buffInput.read(buffer);
                }
                fos.close();
                buffInput.close();
            }
        }
    }

    public ObservableList<JarEntry> openNewJar(String jarPath) throws WrongPathException, IOException {
        ObservableList<JarEntry> jarEntryList = FXCollections.observableArrayList();
        if (jarPath.endsWith(".jar")) {
            closeInputStreams();
            this.jarPath = jarPath;
            unzipJar("./newApplication/", this.jarPath);

            File jarFile = new File(jarPath);
            FileInputStream fileInputStream = new FileInputStream(jarFile);
            jarInputStream = new JarInputStream(fileInputStream);
            this.manifest = jarInputStream.getManifest();
            JarEntry jarEnrty;
            while ((jarEnrty = jarInputStream.getNextJarEntry()) != null) {
                jarEntryList.add(jarEnrty);
            }
            createClassPool();

        } else {
            throw new WrongPathException();
        }
        this.jarEntries = jarEntryList;
        return jarEntryList;
    }

    private void createClassPool() throws IOException {
        try {
            classPool = new ClassPool();
            applicationPath = classPool.appendClassPath("./newApplication/");
            systemPath = classPool.appendSystemPath();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            jarInputStream.close();
        }
    }

    private void closeInputStreams() {
        try {
            if (jarInputStream != null) {
                jarInputStream.close();
            }
            if (jarOutputStream != null) {
                jarOutputStream.close();
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    String adjustClassPath(String classPath) throws WrongPathException {
        if (!classPath.endsWith(".class")) {
            throw new WrongPathException();
        }
        classPath = classPath.replaceAll("/", ".").replace(".class", "");
        return classPath;
    }

    ClassPool getClassPool() {
        return classPool;
    }

    public ObservableList<JarEntry> getJarEntries() {
        return this.jarEntries;
    }

    void updateJarEntries(JarEntry jarEntry) {
        for (int i = 0; i < this.jarEntries.size(); i++) {
            if (this.jarEntries.get(i).getName().equals(jarEntry.getName())) {
                this.jarEntries.set(i, jarEntry);
                return;
            }
        }
        this.jarEntries.add(jarEntry);
    }

    void removeJarEntry(JarEntry jarEntry) {
        for (int i = 0; i < this.jarEntries.size(); i++) {
            if (this.jarEntries.get(i).getName().equals(jarEntry.getName())) {
                this.jarEntries.remove(this.jarEntries.get(i));
                return;
            }
        }
    }

    public void exportJar(String savePath) throws IOException {
        if (this.jarPath != null && this.jarPath.endsWith(".jar")) {
            if (this.jarOutputStream != null) {
                jarOutputStream.close();
            }
            if(this.jarInputStream != null) {
                jarInputStream.close();
            }
            if (this.manifest == null) {
                this.manifest = new Manifest();
                this.manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            }
            System.out.println(this.manifest.toString());
            this.jarOutputStream = new JarOutputStream(new FileOutputStream(savePath), this.manifest);
            this.manifest = null;
            classPool.removeClassPath(applicationPath);
            classPool.removeClassPath(systemPath);
            classPool = null;
            File application = new File("newApplication");
            addToJar(application, this.jarOutputStream);
            this.jarOutputStream.close();
            this.jarEntries.clear();
            deleteDirectory(application);
        }
        this.jarPath = "";
    }

    private void addToJar(File source, JarOutputStream target) throws IOException {
        BufferedInputStream in = null;
        try {
            if (source.isDirectory()) {
                String name = source.getPath().replace("\\", "/");
                if (!name.isEmpty()) {
                    if (!name.endsWith("/"))
                        name += "/";
                    if (!name.equals("newApplication/")) {
                        name = name.replace("newApplication/", "");
                        JarEntry entry = new JarEntry(name);
                        entry.setTime(source.lastModified());
                        target.putNextEntry(entry);
                        target.closeEntry();
                    }
                }
                for (File nestedFile : Objects.requireNonNull(source.listFiles())) {
                    if (!nestedFile.getName().equals("MANIFEST.MF"))
                        addToJar(nestedFile, target);
                }

                return;
            }

            JarEntry entry = new JarEntry(source.getPath().replace("\\", "/").replace("newApplication/", ""));
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));

            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        } finally {
            if (in != null)
                in.close();
        }
    }

    boolean removeFile(String classPath) {
        String filePath = classPath.replace(".", "\\");
        filePath = filePath.replace("\\class", ".class");
        File file = new File("newApplication\\" + filePath);
        return file.delete();
    }

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
