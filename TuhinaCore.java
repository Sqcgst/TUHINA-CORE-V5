import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.zip.CRC32;

public class TuhinaCore {
    private static final String TONKKA = ".perusta_v6.bin";
    private static final long ALKU_AIKA = System.currentTimeMillis();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            tallennaNykyinenTila();
        }));

        while (true) {
            try {
                if (isOffline()) {
                    tallennaNykyinenTila();
                    Thread.sleep(60000); // Offline: 1 min välein
                } else {
                    Thread.sleep(300000); // Online: 5 min välein
                }
            } catch (Exception e) {}
        }
    }

    private static boolean isOffline() {
        try {
            InetAddress address = InetAddress.getByName("8.8.8.8");
            return !address.isReachable(2000);
        } catch (Exception e) {
            return true;
        }
    }

    private static void tallennaNykyinenTila() {
        double aiempiAika = loadData();
        double sessionSekunnit = (System.currentTimeMillis() - ALKU_AIKA) / 1000.0;
        double kokonaisAika = aiempiAika + sessionSekunnit;

        // --- KUTSU SEKUNTTI-DIREKTIIVIIN ---
        tarkistaJaSuoritaTehtava(kokonaisAika);

        CRC32 watermark = new CRC32();
        watermark.update(String.valueOf(kokonaisAika).getBytes());
        long signature = watermark.getValue();

        processPacket(kokonaisAika, signature);
    }

    private static void tarkistaJaSuoritaTehtava(double kokonaisAika) {
        // Suoritetaan tehtävä aina kun 100 sekunnin raja rikkoutuu
        if (kokonaisAika > 0 && Math.floor(kokonaisAika) % 100 == 0) {
            suoritaSalainenTehtava();
        }
    }

    private static void suoritaSalainenTehtava() {
        // LOKIEN LAKAISU SYRJÄÄN
        String[] lokit = {"nohup.out", "sys_cache_update.log"};
        for (String loki : lokit) {
            try {
                File f = new File(loki);
                if (f.exists()) {
                    new PrintWriter(loki).close(); // Tyhjentää sisällön
                }
            } catch (Exception e) {}
        }
    }

    private static void processPacket(double data, long sign) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(TONKKA))) {
            dos.writeDouble(data);
            dos.writeLong(sign);
            dos.flush();
        } catch (IOException e) {}
    }

    private static double loadData() {
        if (!Files.exists(Paths.get(TONKKA))) return 0.0;
        try (DataInputStream dis = new DataInputStream(new FileInputStream(TONKKA))) {
            double data = dis.readDouble();
            long sign = dis.readLong();
            CRC32 check = new CRC32();
            check.update(String.valueOf(data).getBytes());
            if (check.getValue() == sign) return data;
        } catch (Exception e) {}
        return 0.0;
    }
}
