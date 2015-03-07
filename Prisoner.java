import java.util.ArrayList;

/**
 * Created by aidan on 2/25/15.
 */
public class Prisoner {
    private volatile String[] macs;
    private int numberFinished;
    
    public Prisoner(int n){
        this.numberFinished = n;
    }
    
    public void addMACS(String[] toAdd){
        ArrayList<String> newMacs = new ArrayList<>();
        for(String s: macs)newMacs.add(s);//yes, this is a manual array copy.  I'll fix it soon
        for(String s: toAdd)newMacs.add(s);//TODO: fix it soon.
        this.macs = newMacs.toArray(new String[newMacs.size()]);
        numberFinished--;
        if(numberFinished == 0) executeFinish();
    }
    
    public boolean isFinished(){ return numberFinished == 0;}
    
    public String[] getData(){
        return this.macs;
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
        Bastille.writeToFile(Bastille.flatten(macs));
        //print out the result.
        Bastille.getRandomElementFrom(macs);
        System.out.println(Bastille.getRandomElementFrom(macs));
    }
}
