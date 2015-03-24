import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

public class Soldier implements Runnable{
    
    private static final String CWRUBlock = "129.22";
    private int thirdAddrLB;
    private int thirdAddrUB;
    final private Prisoner sharedData;
    
    public Soldier(int LB, int UB, Prisoner sharedData){
        this.thirdAddrLB = LB;
        this.thirdAddrUB = UB;
        this.sharedData = sharedData;
    }

    public void run() {
        int fourthAddr;
        ArrayList<String> returnVar = new ArrayList<>();
        for(;thirdAddrLB<thirdAddrUB;thirdAddrLB++){
            for(fourthAddr = 0;fourthAddr<256;fourthAddr++){
                String lookupValue = nslookupIP(String.format("%s.%d.%d",CWRUBlock , thirdAddrLB, fourthAddr));
                if(lookupValue !=null) Collections.addAll(returnVar, lookupValue);
            }
            System.out.println((double)thirdAddrUB/thirdAddrLB+"% complete with resolutions on this thread.");
        }
        String[] returnArr = returnVar.toArray(new String[returnVar.size()]);
        Bastille.trimResults(returnArr);
        Bastille.formatMAC(returnArr);
        synchronized (sharedData) {
            sharedData.addMACS(returnArr);
        }
    }
    
    
    private String nslookupIP(String ip){
        byte[] ipByte = new byte[4];
        String[] stringByte = ip.split("\\.");
        for(int i = 0; i< stringByte.length; i++){
            ipByte[i] = (byte) Integer.parseInt(stringByte[i]); //lots of unsafe casting :/
        }
        String hostname = null;
        try {
            hostname =  InetAddress.getByAddress(ipByte).getHostName();
            if(hostname.equals(ip)) throw new UnknownHostException();
            System.out.println(ip+" was found with hostname "+hostname);
        }catch (UnknownHostException e){
            //System.err.println("Error, IP: "+ip+" has an unknown host.");
        }
        
        return hostname;
    }
}
