public class Kuittaus {
    public static void main(String[] args) {
        System.out.println("[!] ANY BUTTON PAINETTU: Käynnistetään protokollat...");

        // Suoritetaan algoritmit sarjassa
        Algoritmit.tarkistaIntegiteetti();
        Algoritmit.lakaiseLokit();
        Algoritmit.varmistaYdin();
        
        System.out.println("[+] KAIKKI JÄRJESTELMÄT KUITATTU.");
    }
}
