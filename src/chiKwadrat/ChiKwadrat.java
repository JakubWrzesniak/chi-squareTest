package chiKwadrat;


import java.io.*;
import java.nio.charset.IllegalCharsetNameException;

public class ChiKwadrat {
    private String[][] dataO; //Tabela liczbeności obserwujących
    private Double[][] dataE; //Tabela liczebności oczekiwanych
    private String fileName; //nazwa pliku
    private double statisticValue; // wartośc statystyczna
    private int degreesOfFreedom; // ilosc stopni swobody
    private double result; //wartośc prawdopodbienstwa

    public ChiKwadrat(String fileName){
        this.fileName = fileName;
        readData(); //Odczytanie danych s pliku
        setTotal(); //Obliczenia sum
        countExpectedValue(); //Obliczenie wartości oczekiwanych
        statisticValue = statisticValue(); //Obliczenie wartości statystycznej
        result = probabilityDensityFunction(statisticValue,degreesOfFreedom); // obliczenie wartości prawdopodobieństwa
    }



    public double probabilityDensityFunction(double statisticValue, int degreesOfFreedom){

        if(statisticValue>1000 || degreesOfFreedom>1000){ //Jezżeli wartosć statystyczna albo ilośc stopni swobody są większe od 1000
            double q = norm((Math.pow(statisticValue / degreesOfFreedom, 1.0/3.0) + 2.0 / (9.0*degreesOfFreedom) - 1.0) / Math.sqrt( 2.0/(9.0*degreesOfFreedom))) / 2.0;
            if(statisticValue>degreesOfFreedom)
                return q;
            return 1-q;
        }
        double p = Math.exp(-0.5 * statisticValue);
        if((degreesOfFreedom % 2) == 1)
            p = p * Math.sqrt(2 * statisticValue / Math.PI);
        var k = degreesOfFreedom;
        while(k >= 2) {
            p = p * statisticValue / k;
            k = k - 2;
        }
        var t = p;
        var a = degreesOfFreedom;
        while(t > 1e-15 * p) {
            a = a + 2;
            t = t * statisticValue / a;
            p = p + t;
        }
        return 1 - p;
    }

    public double norm(double z){
        double q = z*z;
        if(Math.abs(z)>7)
            return (1.0 - (1.0 / q) + (3.0 / (q * q))) * Math.exp(-q/2.0) / (Math.abs(z) * Math.sqrt(Math.PI / 2.0));
        return probabilityDensityFunction(q,1);
    }

    public void readData(){
        String line = "";
        String[] lineCharacters;
        int row = 0;
        int column = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
            CreateTable();
            while(true){
                line = br.readLine();
                if(line == null )
                    return;
                lineCharacters = line.split(",");
                for (int i = 0; i < lineCharacters.length; i++) {
                    dataO[row][i] = lineCharacters[i];
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CreateTable(){
        int rows = 0;
        String[] lineCharacter = new String[0];

        try(BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {

            String line = br.readLine();
            lineCharacter = line.split(",");

            while (line!=null){
                line = br.readLine();
                rows++;
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            dataO = new String[rows+1][lineCharacter.length+1];
            dataE = new Double[rows-1][lineCharacter.length-1];
            degreesOfFreedom = (rows-2)*(lineCharacter.length-2);
        }
    }

    private void countExpectedValue(){
        double total = Double.parseDouble(dataO[dataO.length-1][dataO[0].length-1]);
        for(int i = 1 ; i < dataO.length-1; i++){
            for(int  j =1 ; j <dataO[i].length-1; j++){
                dataE[i-1][j-1] =roundToDecimal(Double.parseDouble(dataO[i][dataO[i].length-1])
                        *Double.parseDouble(dataO[dataO.length-1][j]) / total,2);
            }
        }
    }
    private double roundToDecimal(double num, int dec) {
        int multi = (int) Math.pow(10, dec);
        int temp = (int) Math.round(num * multi);
        return (double) temp / multi;
    }

    private void setTotal(){
        //Watwienie do tabali nazwy ostatniej koolumny i ostatniego wiersza
        dataO[0][dataO[0].length-1] = "SUM";
        dataO[dataO.length-1][0] = "SUM";

        int sum = 0;

        //Obliczanie wartości po kolumnach i powuerszach
        for(int i =1 ; i < dataO.length-1 ; i++){
            for(int j = 1 ; j< dataO[i].length ; j++){
                if( j == dataO[i].length -1 ){
                    dataO[i][j] = String.valueOf(sum);
                    sum = 0;
                }
                else
                    sum += Integer.parseInt(dataO[i][j]);
            }
        }
        for(int j = 1; j < dataO[0].length; j++){
            for(int i = 1; i < dataO.length; i++){
                if( i == dataO.length -1 ){
                    dataO[i][j] = String.valueOf(sum);
                    sum = 0;
                }
                else
                    sum += Integer.parseInt(dataO[i][j]);
            }
        }
    }

    private double statisticValue(){
        double result = 0.0;
        //Obliczanie sumy całkowitej
        for(int i = 0 ; i < dataE.length; i++){
            for(int j =0 ;j < dataE[0].length; j++){
                result += Math.pow(Double.parseDouble(dataO[i+1][j+1])-dataE[i][j],2)/dataE[i][j];
            }
        }
        return result;
    }

    public double getResult() {
        return result;
    }

    public void showData(){
        System.out.println("Tabela liczebności obserwowanych:");
        for(int j = 0; j < dataO[0].length; j++ ){
            System.out.print(dataO[0][j]+"  ");
        }
        System.out.print("\n");
        for(int i = 1; i < dataO.length; i++ ){
            for(int j = 0; j < dataO[i].length; j++ ){
                System.out.print(dataO[i][j]+"     ");
            }
            System.out.print("\n");
        }
        System.out.println("Tabela liczebności oczekiwnaych:");
        for(int i = 0; i < dataE.length; i++ ){
            for(int j = 0; j < dataE[i].length; j++ ){
                System.out.print(dataE[i][j]+"    ");
            }
            System.out.print("\n");
        }
        System.out.println("statistic Value "+ statisticValue);
        System.out.println("degreesOfFreedom "+ degreesOfFreedom);
    }
}
