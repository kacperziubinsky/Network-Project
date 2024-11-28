import java.io.*;

public class FileTransfer {
    static FileInputStream fis = null;
    static BufferedInputStream bis = null;
    static BufferedOutputStream bos = null;
    static byte[] buffer = new byte[1024];

    public static void main(String[] args) {
        File curdir = new File(".");
        getALLFiles(curdir);
    }

    public static void getALLFiles(File curdir) {
        File[] files = curdir.listFiles();
        if (files == null) {
            System.out.println("No files found or directory does not exist.");
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                System.out.println(file.getName());
            }
        }
    }

    public static void sendFile(String finalpath, String filepath, String path) throws Exception {
        File dir = new File(finalpath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File inputFile = new File(path + File.separator + filepath);
        File outputFile = new File(dir, filepath);

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            fis = new FileInputStream(inputFile);
            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(new FileOutputStream(outputFile));

            int read;
            while ((read = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            bos.flush();

            System.out.println("Plik wysłany do: " + outputFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) bis.close();
            if (fis != null) fis.close();
            if (bos != null) bos.close();
        }
        if (inputFile.delete()) {
            System.out.println("File deleted successfully: " + inputFile.getAbsolutePath());
        } else {
            System.out.println("Failed to delete the file: " + inputFile.getAbsolutePath());
            System.out.println("File exists: " + inputFile.exists());
            System.out.println("File can write: " + inputFile.canWrite());
            System.out.println("File can read: " + inputFile.canRead());
        }
        File[] updatedFiles = new File(path).listFiles();
        if (updatedFiles != null) {
            for (File file : updatedFiles) {
                System.out.println("Updated file: " + file.getName());
            }
        }
    }
    }
