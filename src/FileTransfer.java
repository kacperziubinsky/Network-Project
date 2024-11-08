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

    public static void getALLFiles(File curdir)
    {
        File[] files = curdir.listFiles();
        for(File file : files)
        {
            if(file.isFile())
            {
                System.out.println(file.getName());
            }
        }
    }

    public static void sendFile(String finalpath, String filepath) throws Exception {
        // Utwórz katalog, jeśli nie istnieje
        File dir = new File(finalpath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            fis = new FileInputStream("src/ClientData/" + filepath);
            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(new FileOutputStream(finalpath));
            int read = 0;
            while ((read = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            bis.close();
            fis.close();
            bos.close();
            System.out.println("Plik wysłany do: " + finalpath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void recieveFile(String finalpath, String filename) throws Exception {
        try {
            bos = new BufferedOutputStream(new FileOutputStream(finalpath));
            bis = new BufferedInputStream(new FileInputStream(filename));
            int read = 0;
            while ((read = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
                System.out.println("Przesyłanie");
            }
            bis.close();
            bos.close();
            System.out.println("Pobrano plik: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}