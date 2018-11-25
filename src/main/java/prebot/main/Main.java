package prebot.main;

public class Main {
    public static void main(String[] args) {
        try {
            new Monster().run();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}