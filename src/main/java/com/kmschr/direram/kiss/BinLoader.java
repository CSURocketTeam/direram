package com.kmschr.direram.kiss;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class BinLoader {

    private static BinLoader instance = null;
    private final Path tempDir = Files.createTempDirectory(null);

    private BinLoader() throws IOException { }

    public static BinLoader getBinLoader() {
        if (instance == null) {
            try {
                instance = new BinLoader();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    // copy executable from resources to temporary directory for running
    public String copyExe(String exeName) throws IOException {
        String exePath = "";
        File exeFile = new File(tempDir.toAbsolutePath().toString() + "\\" + exeName);
        InputStream exeStream = getClass().getResourceAsStream("/bin/" + exeName);
        FileUtils.copyInputStreamToFile(exeStream, exeFile);
        exePath = exeFile.getAbsolutePath();
        return exePath;
    }

}
