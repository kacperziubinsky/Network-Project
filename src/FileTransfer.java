import java.io.*;

public class FileTransfer {
    public FileTransfer() {

    }

    private static FileInputStream fis = null;
    private static BufferedInputStream bis = null;
    private static BufferedOutputStream bos = null;
    private static byte[] buffer = new byte[1024];
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
    public static void sendFile(String finalpath,String filepath) throws Exception {
        try {
            fis = new FileInputStream(filepath);
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
    public static void recieveFile(String finalpath,String filename) throws Exception
    {
        try {
            bos = new BufferedOutputStream(new FileOutputStream(finalpath));
            bis = new BufferedInputStream(new FileInputStream(filename));
            int read = 0;
            while ((read = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            bis.close();
            bos.close();
            System.out.println("Pobrano plik: " + filename);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
