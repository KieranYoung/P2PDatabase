package compression;


import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Compress {

    private static final String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String folder = "/p2p_data";
    private static final String files = "/data";
    private static final String zip = "/data.zip";

    public static final String inPath = SDPath + folder + files;
    public static final String outPath = SDPath + folder + zip;

    private static final int BUFFER = Integer.MAX_VALUE;

    public static void createDirectories() {
        File f = new File(inPath);
        if (!f.exists())
            f.mkdirs();
    }

    public static void deleteFiles() {
        File dir = new File(inPath);
        for (File f: dir.listFiles())
            if (!f.isDirectory())
                f.delete();
    }

    public static void deleteZip() {
        File f = new File(outPath);
        f.delete();
    }

    public static void makeTestFile(String string) {
        try {
            String newPath = inPath+"/testfile.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(newPath));
            writer.write(string);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipAll() {
        zipFolder(inPath, outPath);
    }

    public static void unzipAll() {
        try {
            unzip(new File(outPath), new File(inPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void unzip(File zipFile, File targetDirectory) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)))) {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[BUFFER];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                try (FileOutputStream fout = new FileOutputStream(file)) {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                }
	            /* if time should be restored as well
	            long time = ze.getTime();
	            if (time > 0)
	                file.setLastModified(time);
	            */
            }
        }
    }

    private static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            System.out.println("Zip directory: " + srcFile.getName());
            for (File file : files) {
                System.out.println("Adding file: " + file.getName());
                byte[] buffer = new byte[BUFFER];
                FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            System.out.println(ioe.toString());
        }
    }
}
