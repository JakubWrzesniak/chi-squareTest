package chiKwadrat;

public class main {
    public static void main(String[] args){
        ChiKwadrat data4 = new ChiKwadrat("z4data4.csv");
        data4.showData();
        System.out.println("probability value p = "+data4.getResult());
    }

}
