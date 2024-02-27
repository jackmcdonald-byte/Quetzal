package NNUE.nnue_probe.nnue;


import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


public class NNUEJNIBridge {


    public static final String NET_NAME = "nn-6b4236f2ec01.nnue";

    private static final String FILE_SCHEME = "file";


    public static void loadLib() {

        try {

            String libName = System.mapLibraryName("JNNUE");
            Path jarfile = Paths.get(NNUEJNIBridge.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File libFile = jarfile.getParent().resolve(libName).toFile();
//            if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Looking for " + libName + " at location " + libFile);
            if (libFile.exists()) {
                System.load(libFile.getAbsolutePath());
//                if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump(libName + " is now loaded");
            } else {
                URL classpathLibUrl = NNUEJNIBridge.class.getClassLoader().getResource(libName);
//                if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Looking for " + libName + " at location " + classpathLibUrl);
                if (classpathLibUrl != null && FILE_SCHEME.equalsIgnoreCase(classpathLibUrl.toURI().getScheme()) && Paths.get(classpathLibUrl.toURI()).toFile().exists()){
                    File classpathLibFile = Paths.get(classpathLibUrl.toURI()).toFile();
                    System.load(classpathLibFile.getAbsolutePath());
//                    if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Loaded " + libName + " located in the resources directory");
                } else {
//                    if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Looking for " + libName + " at java.library.path: " + System.getProperty("java.library.path"));
                    System.loadLibrary("JNNUE");
//                    if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Loaded " + libName + " located in the java library path");
                }
            }

        } catch (Throwable t) {

//            if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Unable to load JNNUE library " + t);
        }
    }


    public static void init() {

        init(NET_NAME);
    }


    public static native void init(String filename);

    public static native int eval(String fen);

    public static native int eval(int color, int[] pieces, int[] squares);

    public static void setup() {
        File dll = new File("src/NNUE/nnue_probe/JNNUE.dll");

        System.load(dll.getAbsolutePath());

        //File nnue = new File("nn-04cf2b4ed1da.nnue");
        //File nnue = new File("nn-53d4417222df.nnue");//user: vdv 21-01-01 21:24:06
        File nnue = new File("nn-6b4236f2ec01.nnue");//user: vdv 21-05-01 10:24:00

        NNUEJNIBridge.init(nnue.getName());
    }
}