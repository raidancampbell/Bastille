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
        if(numberFinished == 0){
            //call Bastille to start.
        }
    }
    
    public boolean isFinished(){ return numberFinished == 0;}
    
    public String[] getData(){
        return this.macs;
        
    }
    

}
