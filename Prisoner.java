import java.util.ArrayList;
import java.util.Arrays;


public class Prisoner {
    private volatile ArrayList<String> macs;
    private int threadsRemaining;
    private int totalNumber;
    
    public Prisoner(int n){
        this.threadsRemaining = n;
        this.totalNumber = n;
        macs = new ArrayList<>();
    }
    
    public void addMACS(String[] toAdd){
        this.macs.addAll(new ArrayList<>(Arrays.asList(toAdd)));
        threadsRemaining--;
        System.out.println(((double) threadsRemaining /(double)totalNumber)*100+"% complete");
        if(threadsRemaining == 0) executeFinish();
    }
    
    /**
     * Trims, formats, and flattens the macs to a single, newline-delimited string
     * then writes to file with name "macs", gets a random element from the array, 
     * and prints it out
     */
    private void executeFinish(){
        //wake Bastille for a clean implementation
        //or Bastille finishes, and I (a different thread) finish execution.
        String[] macArray = macs.toArray(new String[macs.size()]);
        Bastille.writeFile(Bastille.flatten(macArray));
        //print out the result.
        System.out.println(Bastille.getRandomElementFrom(macArray));
    }
}
