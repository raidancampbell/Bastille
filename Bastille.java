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

    /**
     * begins execution.  Runtime is limited by DNS query restrictions.
     *  starts with dividing the workload among 8 threads, who then resolve
     *  their IP block into hostnames, and write their results to the 'Prisoner' object
     *  Once finished, the hostnames will be parsed for MAC addresses, written to a file,
     *  and a random MAC will be returned.  If execution has already occured, Bastille will
     *  simply open the file and return a random MAC from the file.
     * @param args *unused*
     */
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

    /**
     * *
     * @param input an array of strings conforming to "tmp*mac*"
     * @return MAC address String array 
     */
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
            outputStream.close();
        } catch(Exception e){
            System.err.println("Error while writing to file '"+filename+"'!");
        }
        
    }

    /**
     * creates and starts threadCount number of worker threads for nameserver resolution.
     * @param threadCount number of worker threads to create.  256 is a hard maximum.
     * @return Prisoner, the shared data used by the threads.
     */
    private static Prisoner deploySoldiers(int threadCount){
        if(threadCount<1) threadCount = Math.abs(threadCount);
        if(threadCount>256) threadCount = 256;
        Prisoner sharedData = new Prisoner(threadCount);
        int width = 256 / threadCount;
        for(int i = 0; i < threadCount; i++){
            int  LB = width*i;//Lower Bound of IP space
            int  UB = LB + width;//Upper Bound of IP space
            Soldier soldier = new Soldier(LB, UB, sharedData);
            new Thread(soldier).start();//protip: to actually use multithreading, you need to put it into a thread.
        }
        return sharedData;
    }//end of addSoldier

    /**
     * reads the "macs" file, which is a hardcoded filename.
     * base64 encoding is used to encode the file before writing, and is
     * decoded on reading.  If you're capable of reading the source code, 
     * you're capable of doing this yourself, and I don't need to hide this data from you
     * @return the contents of the file, as a String array
     */
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

    /**
     * get a random element from the given String array
     * @param array array to choose a random element from
     * @return a random element from the given array
     */
    public static String getRandomElementFrom(String[] array){
        if(array == null) return null;
        return array[(int) (Math.random()*array.length)];
    }
}//end of class