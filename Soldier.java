import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

public class Soldier implements Runnable{
    
    private static final String CWRUBlock = "129.22";//the IP block belonging to CWRU
    private int thirdAddrLB;
    private final int startLB;
    private int thirdAddrUB;
    final private Prisoner sharedData;

    /**
     * Constructor.  Gotta make me before you run me.
     * @param LB Lower Bound of third IP byte to lookup, as an integer
     * @param UB Upper Bound of third IP byte to lookup, as an integer
     * @param sharedData the shared data to write results to upon completion
     */
    public Soldier(int LB, int UB, Prisoner sharedData){
        this.thirdAddrLB = LB;
        this.thirdAddrUB = UB;
        this.sharedData = sharedData;
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
            System.out.println(percentComplete()*100+"% complete with resolutions on this thread.");
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
            hostname = null;
        }
        return hostname;
    }

    /**
     * @return the percent complete this thread is with its execution.
     *          given as a double value between 0 and 1
     */
    private double percentComplete(){
        //TODO: fix this.
        /*
        thirdAddrUB, and startLB are constant per thread.
        thirdAddrLB is incremented up to thirdAddrUB.
        apparently I can't think well enough to turn this
        into a percent complete.
         */
        int range = thirdAddrUB - startLB;
        int currentPosition = thirdAddrUB-thirdAddrLB;
        return currentPosition / range;
    }
}
