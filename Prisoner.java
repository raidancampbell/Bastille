import java.util.ArrayList;
import java.util.Arrays;

/*
    To keep with the silly metaphor, the Prisoner object represents the shared data.
    The Soldier threads will access the Prisoner object to add to it, and the final addition
    will trigger some cleanup and finishing execution stuff to occur.
 */
public class Prisoner {
    private volatile ArrayList<String> macs;//current arrayList of uncleansed MAC addresses
    private int threadsRemaining;//number of threads that haven't finished execution
    private final double totalThreads;//holds an int, but rendered as double for fraction usage.
    private double[] threadCompletionAmounts;//index-aligned completion amount for each worker thread
    
    //constructor.  Just feed me the number of threads, so I can know when we're done.
    public Prisoner(int numberOfThreads){
        this.threadsRemaining = numberOfThreads;
        macs = new ArrayList<>();
        threadCompletionAmounts = new double[numberOfThreads];
        totalThreads = numberOfThreads;
    }

    /**
     * adds the given strings to the current list.
     * *The given string array isn't required to be cleansed*
     * the final product will be cleaned upon finishing
     * @param toAdd String array of individual MAC addresses
     */
    public void addMACS(String[] toAdd){
        this.macs.addAll(new ArrayList<>(Arrays.asList(toAdd)));
        threadsRemaining--;
        if(threadsRemaining == 0) executeFinish();
    }
    
    /**
     * Trims, formats, and flattens the macs to a single, newline-delimited string
     * then writes to file with name "macs", gets a random element from the array, 
     * and prints it out
     */
    private void executeFinish(){
        String[] macArray = macs.toArray(new String[macs.size()]);
        macArray = Bastille.formatMAC(Bastille.trimResults(macArray));//eliminate non TMP*mac* hosts, then parse MAC
        Bastille.writeFile(Bastille.flatten(macArray));
        //print out the result.
        System.out.println(Bastille.getRandomElementFrom(macArray));
    }

    /**
     * computes the total completion amount of all worker threads, combined
     * @param threadNumber number of worker threads
     * @param newCompletionAmount given thread's completion amount
     * @return the sum of the weighted averages of each thread's completion amount
     */
    public double updateCompletion(int threadNumber, double newCompletionAmount){
        this.threadCompletionAmounts[threadNumber] = newCompletionAmount;
        double returnVar = 0d;
        for(Double d: threadCompletionAmounts) returnVar += (d/totalThreads);
        return returnVar;
    }
}
