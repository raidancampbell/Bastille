import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aidan on 2/11/15.
 *
 * Scans the CWRU IP space, and does a brute-force resolve on all IPs
 * that are owned in our block
 */
public class Bastille {
    private static final String filename = "macs";
    
    public static void main(String[] args){
        String[] existingMacs = readFile();
        if(existingMacs == null) {//we haven't made them yet.
            //make them, and the last thread will pick up from there
            deploySoldiers(8);//divide namespace resolution into 8 threads
        } else {
            getRandomElementFrom(existingMacs);
            System.out.println(getRandomElementFrom(existingMacs));
        }
    }

    /**
     * you give me a string array, I give you a single string, flattened with newlines
     * @param input a string array
     * @return a single newline-delimited string
     */
    public static String flatten(String[] input){
        StringBuilder returnVar = new StringBuilder();
        for(String s: input) returnVar.append(String.format("%s\n", s));
        return returnVar.toString();
    }
    
    public static String[] formatMAC (String[] input){
        ArrayList<String> returnVar = new ArrayList<>();
        
        for(String s: input){
            if(s.startsWith("tmp")){
                StringBuilder temp = new StringBuilder(s.substring(3));
                StringBuilder returnBuilder = new StringBuilder();
                String prefix = "";
                int index = 0;
                while (index < temp.length())
                {
                    returnBuilder.append(prefix);
                    prefix = ":";
                    returnBuilder.append(temp.substring(index,
                            Math.min(index + 2, temp.length())));
                    index += 2;
                }
                returnVar.add(returnBuilder.toString());
            }
        }
        return returnVar.toArray(new String[returnVar.size()]);
    }

    /**
     * returns only the lines containing the desired tmp\W{12} hostname 
     * @param input output of resolveIPSpace
     * @return strings that match the regex
     */
    public static String[] trimResults(String[] input){
        ArrayList<String> returnVar = new ArrayList<>();
        Pattern pattern = Pattern.compile("tmp[0-9a-f]+");//yay regex!
        for(String s: input){
            //valid is tmp, followed by 12 non-whitespace characters
            Matcher matcher = pattern.matcher(s);
            if(matcher.find()) returnVar.add(matcher.group(0));
        }
        return returnVar.toArray(new String[returnVar.size()]);
    }

    /**
     * you give me text, and a filename.  I write the text under that filename
     * @param toWrite text to write
     */
    public static void writeFile(String toWrite){
        try {
            byte[] data = toWrite.getBytes();
            FileOutputStream outputStream = new FileOutputStream(filename);
            Base64.Encoder encoder = Base64.getEncoder();
            encoder.encode(data);
            outputStream.write(data);
        } catch(Exception e){
            System.err.println("Error while writing to file '"+filename+"'!");
        }
        
    }
    
    private static Prisoner deploySoldiers(int threadCount){
        Prisoner sharedData = new Prisoner(threadCount);
        int width = 256 / threadCount;
        for(int i = 0; i < threadCount; i++){
            int  LB = width*i;
            int  UB = LB + width;
            Soldier soldier = new Soldier(LB, UB, sharedData);
            new Thread(soldier).start();
            //soldier.run();//forks a thread into soldier to nslookup the IP space between LB and UB
        }
        return sharedData;
    }//end of addSoldier
    
    public static String[] readFile(){
        try {
            byte[] buffer = new byte[(int) new File(filename).length()];
            BufferedInputStream f = new BufferedInputStream(new FileInputStream(filename));
            int status = f.read(buffer);
            Base64.Decoder decoder = Base64.getDecoder();
            decoder.decode(buffer);
            if(status == -1) throw new IOException("READ STATUS RETURNED -1");
            return new String(buffer).split("\\n");//newline split the stuff.
        } catch (FileNotFoundException e){
            return null;
        } catch(IOException e){
            System.err.println(String.format("ERROR WHILE READING FROM FILE %s", filename));
            e.printStackTrace();
        }
        return null;
    }
    
    public static String getRandomElementFrom(String[] array){
        if(array == null) return null;
        return array[(int) (Math.random()*array.length)];
    }
}//end of class