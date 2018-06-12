public class Tester {
    public static void main(String[] args) {
        Date test = new Date(9,30,2020);
        test.addDays(1);
        System.out.println(test.toString());
        test.addDays(-1);
        System.out.println(test.toString());
    }
}
