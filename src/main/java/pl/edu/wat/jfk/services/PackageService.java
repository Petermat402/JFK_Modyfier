package pl.edu.wat.jfk.services;

import javassist.ClassPool;
import javassist.NotFoundException;

import java.io.File;
import java.util.jar.JarEntry;

public class PackageService {
    JarService jarService;
    public PackageService(JarService jarService) {
        this.jarService = jarService;
    }

    public void addPackage(String packageName) throws NotFoundException {
        ClassPool classPool = jarService.getClassPool();
        String path = ".\\newApplication\\" + packageName.replace(".", "\\");
        File file = new File(path);
        System.out.println(file.mkdirs());
        classPool.appendClassPath(path);
        jarService.updateJarEntries(new JarEntry(packageName.replace(".", "/")));
    }

    public void removePackage(String packagePath) {
        ClassPool classPool = jarService.getClassPool();
        String path = "newApplication\\" + packagePath.substring(0, packagePath.indexOf("/"));
        File file = new File(path);
        jarService.deleteDirectory(file);
        jarService.removeJarEntry(new JarEntry(packagePath));
    }
}
