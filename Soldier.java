import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

public class Soldier implements Runnable{
    
    private static final String CWRUBlock = "129.22";//the IP block belonging to CWRU
    private int thirdAddrLB;//129.22.***.0, the lower bound for the *** section (incremented towards UB)
    private final int startLB;//129.22.***.0, the lower bound for the *** section (constant)
    private int thirdAddrUB;//129.22.***.0, the upper bound for the *** section
    final private Prisoner sharedData;//the shared data used between the threads
    private final int threadNumber;//the unique ID number of this thread, by default a number between 0 and 7

    /**
     * Constructor.  Gotta make me before you run me.
     * @param LB Lower Bound of third IP byte to lookup, as an integer
     * @param UB Upper Bound of third IP byte to lookup, as an integer
     * @param threadNumber the unique index number of this thread
     * @param sharedData the shared data to write results to upon completion
     */
    public Soldier(int LB, int UB, int threadNumber, Prisoner sharedData){
        this.thirdAddrLB = LB;
        this.thirdAddrUB = UB;
        this.sharedData = sharedData;
        this.threadNumber = threadNumber;
        this.startLB = LB;//copy of LB, because we change LB during execution.  Used to calculate percent completeness
    }

    /**
     * entrance to execution
     * because Soldier implements Runnable, 
     * it's designed to be popped into a thread for multithreading 
     * I lookup all IPs between the LB and UB of CWRU's IP space 
     */
    public void run() {
        int fourthAddr;
        ArrayList<String> returnVar = new ArrayList<>();
        for(;thirdAddrLB<thirdAddrUB;thirdAddrLB++){
            for(fourthAddr = 0;fourthAddr<256;fourthAddr++){
                String lookupValue = nslookupIP(String.format("%s.%d.%d",CWRUBlock , thirdAddrLB, fourthAddr));
                if(lookupValue !=null) Collections.addAll(returnVar, lookupValue);//ignore null values.
            }
            synchronized (sharedData){
                double completionAmount = sharedData.updateCompletion(threadNumber, percentComplete());
                System.out.println(completionAmount*100+"% complete with resolutions.");
            }
        }
        //there should be an easier array<=>arrayList conversion technique
        String[] returnArr = returnVar.toArray(new String[returnVar.size()]);
        Bastille.trimResults(returnArr);//remove useless hostnames
        Bastille.formatMAC(returnArr);//parse remaining useful hostnames into their MACs
        synchronized (sharedData) {
            //we only access the shared data once per thread, but I'll be damned if we screw it up
            sharedData.addMACS(returnArr); 
        }
    }

    /**
     *  
     * @param ip IP to lookup, given as a string.  No error checking is done, a clean input is required!
     * @return the hostname of the given IP, or null if no hostname is found.
     */
    private String nslookupIP(String ip){
        byte[] ipByte = new byte[4];
        String[] stringByte = ip.split("\\.");
        for(int i = 0; i< stringByte.length; i++){
            ipByte[i] = (byte) Integer.parseInt(stringByte[i]); //lots of unsafe casting :/
        }
        String hostname;
        try {
            //sleep for a half a second to lower DNS queries --rate limiting is a performance issue.
            Thread.sleep(500l);
            hostname =  InetAddress.getByAddress(ipByte).getHostName();
            if(hostname.equals(ip)) throw new UnknownHostException();
            System.out.println(hostname+" was found at "+ip);
        }catch (UnknownHostException e){
            hostname = null;
        } catch(InterruptedException e){
            System.err.println("An error occurred while sleeping\nignoring...");
            hostname = null;
        }
        return hostname;
    }

    /**
     * @return the percent complete this thread is with its execution.
     *          given as a double value between 0 and 1
     */
    private double percentComplete(){
        double range = thirdAddrUB - startLB;
        double currentPosition = thirdAddrLB - startLB;
        return currentPosition / range;
    }
}
