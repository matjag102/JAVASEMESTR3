import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        try{
            DecisionSystem decSystem = new DecisionSystem("fertilityDiagnosis");

            System.out.println(decSystem.printSymbols()+"\n");
            System.out.println(decSystem.printMaxMin()+"\n");
            System.out.println(decSystem.printDifferentValuesCount()+"\n");
            System.out.println(decSystem.printDifferentValuesSets()+"\n");
            System.out.println(decSystem.printStdDeviation()+"\n");

        }catch(java.io.FileNotFoundException e){
            System.out.println("File not found");
        }
    }
}

class DecisionSystem{
    String name;
    Map<String, Integer> symbols;
    Map<String, Map<String, Double>> minMaxValues;
    Map<String, Integer> differentValuesCount;
    Map<String, Set<String>> differentValuesSet;
    Map<String, Map<String, Double>> stdDeviation;
    File dataFile;
    Scanner data;
    File dataTypesFile;
    Scanner dataTypes;

    public String printSymbols(){
        String result = "Symbol\tSize\n";
        for ( String key : symbols.keySet() ) {
            result += key+"\t\t"+symbols.get(key)+"\n";
        }
        return result;
    }

    public String printMaxMin(){
        String result = "Attribute\tMin Value\tMax Value\n";
        for ( String key : minMaxValues.keySet() ) {
            result += key+"\t\t\t"+minMaxValues.get(key).get("min")+"\t\t\t"+minMaxValues.get(key).get("max")+"\n";
        }
        return result;
    }

    public String printDifferentValuesCount(){
        String result = "Attribute\tAmount of different values\n";
        for ( String key : differentValuesCount.keySet() ) {
            result += key+"\t\t\t"+differentValuesCount.get(key)+"\n";
        }
        return result;
    }

    public String printDifferentValuesSets(){
        String result = "Attribute\tSets of different values\n";
        for ( String key : differentValuesSet.keySet() ) {
            result += key+"\t\t\t"+differentValuesSet.get(key)+"\n";
        }
        return result;
    }

    public String printStdDeviation(){
        String result = "Attribute\tStandard deviation\n";
        for ( String key : stdDeviation.get("Attributes").keySet() ) {
            result += key+"\t\t\t"+stdDeviation.get("Attributes").get(key)+"\n";
        }
        result += "\nSymbol\t\tStandard deviation\n";
        for ( String key : stdDeviation.get("Symbols").keySet() ) {
            result += key+"\t\t\t"+stdDeviation.get("Symbols").get(key)+"\n";
        }
        return result;
    }

    private void countSymbols() throws FileNotFoundException {
        String[] line;
        String symbol;

        while(data.hasNextLine()){
            line = data.nextLine().split("\\s+");
            symbol = line[line.length-1];
            if(!symbols.containsKey(symbol)){
                symbols.put(symbol, 1);
            }
            else{
                symbols.put(symbol, symbols.get(symbol)+1);
            }
        }


        data = new Scanner(dataFile);
    }

    private void countMinMax() throws FileNotFoundException {
        String[] dataLine;
        String[] dataTypeLine;
        int i;
        Double parsedAttr;

        minMaxValues=new HashMap<>();

        while(data.hasNextLine()){
            dataLine = data.nextLine().split("\\s+");
            i=1;

            for(String value: dataLine){
                if(i >= dataLine.length)
                    break;
                dataTypeLine = dataTypes.nextLine().split("\\s+");

                if(dataTypeLine[1].equals("n")) {
                    parsedAttr = Double.parseDouble(value);
                    if(!minMaxValues.containsKey("a"+i)){
                        minMaxValues.put("a"+i, new HashMap<>());
                        minMaxValues.get("a"+i).put("min", parsedAttr);
                        minMaxValues.get("a"+i).put("max", parsedAttr);
                    }
                    else{
                        if(minMaxValues.get("a"+i).get("min") > parsedAttr){
                            minMaxValues.get("a"+i).put("min", parsedAttr);
                        }
                        else if(minMaxValues.get("a"+i).get("max") < parsedAttr){
                            minMaxValues.get("a"+i).put("max", parsedAttr);
                        }
                    }
                }

                i++;
            }

            dataTypes = new Scanner(dataTypesFile);
        }

        data = new Scanner(dataFile);
    }

    private void searchDifferentValues() throws FileNotFoundException {
        String[] line;
        int i;

        differentValuesCount = new HashMap<>();
        differentValuesSet = new HashMap<>();

        while(data.hasNextLine()){
            line = data.nextLine().split("\\s+");
            i=1;

            for(String value: line){
                if (i >= line.length)
                    break;
                if(!differentValuesSet.containsKey("a"+i)){
                    differentValuesCount.put("a"+i, 1);
                    differentValuesSet.put("a"+i, new HashSet<>());
                    differentValuesSet.get("a"+i).add(value);
                }
                else{
                    if(!differentValuesSet.get("a"+i).contains(value)){
                        differentValuesCount.put("a"+i, differentValuesCount.get("a"+i)+1);
                        differentValuesSet.get("a"+i).add(value);
                    }
                }

                i++;
            }
        }


        data = new Scanner(dataFile);
    }

    private void calcStdDeviation() throws FileNotFoundException {
        int objNo = 0, i;
        Double parsedAttr;
        String[] dataLine;
        String[] dataTypeLine;
        Map<String, List<Double>> objAttributes = new HashMap<>();
        Map<String, Double> meanAttr = new HashMap<>();
        Map<String, List<Double>> symbolAttributes = new HashMap<>();
        Map<String, Double> meanSymbol = new HashMap<>();

        while(data.hasNextLine()){
            objNo++;
            dataLine = data.nextLine().split("\\s+");
            i=1;

            for(String value: dataLine){
                if(i >= dataLine.length)
                    break;
                dataTypeLine = dataTypes.nextLine().split("\\s+");

                if(dataTypeLine[1].equals("n")) {
                    parsedAttr = Double.parseDouble(value);
                    if(!objAttributes.containsKey("a"+i)){
                        objAttributes.put("a"+i, new ArrayList<>());
                    }
                    if(!symbolAttributes.containsKey(dataLine[dataLine.length-1])){
                        symbolAttributes.put(dataLine[dataLine.length-1], new ArrayList<>());
                    }
                    objAttributes.get("a"+i).add(parsedAttr);
                    symbolAttributes.get(dataLine[dataLine.length-1]).add(parsedAttr);
                }

                i++;
            }

            dataTypes = new Scanner(dataTypesFile);
        }

        objAttributes.forEach((k, v)-> meanAttr.put(k, 0.0));
        for(String key: objAttributes.keySet()){
            for(Double value: objAttributes.get(key)){
                meanAttr.put(key, meanAttr.get(key)+value);
            }
        }

        symbolAttributes.forEach((k, v)-> meanSymbol.put(k, 0.0));
        for(String key: symbolAttributes.keySet()){
            for(Double value: symbolAttributes.get(key)){
                meanSymbol.put(key, meanSymbol.get(key)+value);
            }
        }

        int finalObjNo = objNo;
        meanAttr.forEach((k, v) -> meanAttr.put(k, v/finalObjNo));
        meanSymbol.forEach((k, v) -> meanSymbol.put(k, v/symbolAttributes.get(k).size()));

        Map<String, Double> varianceAttr = new HashMap<>();
        Map<String, Double> varianceSymbol = new HashMap<>();

        for(String key: meanAttr.keySet()){
            for(Double value: objAttributes.get(key)){
                if(varianceAttr.containsKey(key))
                    varianceAttr.put(key, varianceAttr.get(key)+Math.pow(value-meanAttr.get(key), 2));
                else
                    varianceAttr.put(key, Math.pow(value-meanAttr.get(key), 2));
            }
        }

        for(String key: meanSymbol.keySet()){
            for(Double value: symbolAttributes.get(key)){
                if(varianceSymbol.containsKey(key))
                    varianceSymbol.put(key, varianceSymbol.get(key)+Math.pow(value-meanSymbol.get(key), 2));
                else
                    varianceSymbol.put(key, Math.pow(value-meanSymbol.get(key), 2));
            }
        }

        varianceAttr.forEach((k, v) -> varianceAttr.put(k, v/finalObjNo));
        varianceSymbol.forEach((k, v) -> varianceSymbol.put(k, v/symbolAttributes.get(k).size()));

        stdDeviation = new HashMap<>();
        Map<String, Double> stdDeviationAttr = new HashMap<>();
        Map<String, Double> stdDeviationSymbol = new HashMap<>();

        varianceAttr.forEach((k, v) -> stdDeviationAttr.put(k, Math.sqrt(v)));
        varianceSymbol.forEach((k, v) -> stdDeviationSymbol.put(k, Math.sqrt(v)));

        stdDeviation.put("Attributes", stdDeviationAttr);
        stdDeviation.put("Symbols", stdDeviationSymbol);

        data = new Scanner(dataFile);
    }

    DecisionSystem(String name) throws java.io.FileNotFoundException{
        this.name = name;
        symbols = new HashMap<>();

        dataFile = new File("dane\\"+name+".txt");
        data = new Scanner(dataFile);

        dataTypesFile = new File("dane\\"+name+"-type.txt");
        dataTypes = new Scanner(dataTypesFile);

        countSymbols();
        countMinMax();
        searchDifferentValues();
        calcStdDeviation();
    }
}