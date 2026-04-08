import java.io.*;
import java.util.zip.CRC32;

public class Luku {
    public static void main(String[] args) {
        String tiedosto = ".perusta_v6.bin";
        File f = new File(tiedosto);

        if (!f.exists()) {
            System.out.println("[!] VIRHE: Tonkkaa ei löydy. Perusta on tyhjä.");
            return;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
            double aika = dis.readDouble();
            long vesileima = dis.readLong();

            // Lasketaan vertailu-vesileima tarkistusta varten
            CRC32 tarkistus = new CRC32();
            tarkistus.update(String.valueOf(aika).getBytes());
            long laskettuSignu = tarkistus.getValue();

            System.out.println("--- PERUSTA V6: TONKAN PURKU ---");
            System.out.printf("KERTYNYT AIKA: %.2f sekuntia%n", aika);
            System.out.printf("VESILEIMA:     %X%n", vesileima);

            if (vesileima == laskettuSignu) {
                System.out.println("STATUS:        [EHEÄ] Musta-Vyö pitää.");
            } else {
                System.out.println("STATUS:        [MURRETTU] Varoitus: Integiteetti vaarantunut!");
            }
            System.out.println("--------------------------------");

        } catch (Exception e) {
            System.out.println("[!] VIRHE: Paketti on korruptoitunut tai lukukelvoton.");
        }
    }
}
