import java.util.ArrayList;
import java.util.Collections;


public class Prisoner {
    private volatile String[] macs;
    private int numberFinished;
    private int totalNumber;
    
    public Prisoner(int n){
        this.numberFinished = n;
        this.totalNumber = n;
    }
    
    public void addMACS(String[] toAdd){
        ArrayList<String> newMacs = new ArrayList<>();
        Collections.addAll(newMacs, macs);
        Collections.addAll(newMacs, toAdd);
        this.macs = newMacs.toArray(new String[newMacs.size()]);
        numberFinished--;
        System.out.println(((double)numberFinished/(double)totalNumber)*100+"% complete");
        if(numberFinished == 0) executeFinish();
    }
    
    /**
     * Trims, formats, and flattens the macs to a single, newline-delimited string
     * then writes to file with name "macs", gets a random element from the array, 
     * and prints it out
     */
    private void executeFinish(){
        //wake Bastille for a clean implementation
        //or Bastille finishes, and I (a different thread) finish execution.
        macs = Bastille.formatMAC(Bastille.trimResults(macs));
        Bastille.writeFile(Bastille.flatten(macs));
        //print out the result.
        Bastille.getRandomElementFrom(macs);
        System.out.println(Bastille.getRandomElementFrom(macs));
    }
}
