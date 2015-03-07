import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class Soldier implements Runnable{
    
    private int thirdAddrLB;
    private int thirdAddrUB;
    private Prisoner sharedData;
    
    public Soldier(int LB, int UB, Prisoner sharedData){
        this.thirdAddrLB = LB;
        this.thirdAddrUB = UB;
        this.sharedData = sharedData;
    }

    public void run() {
        String firstSection = "129.22";
        int fourthAddr;
        ArrayList<String> returnVar = new ArrayList<>();
        for(;thirdAddrLB<thirdAddrUB;thirdAddrLB++){
            for(fourthAddr = 0;fourthAddr<256;fourthAddr++){
                String[] lookupValue = nslookupIP(String.format("%s.%d.%d",firstSection , thirdAddrLB, fourthAddr));
                Collections.addAll(returnVar, lookupValue);
            }
        }
        String[] returnArr = returnVar.toArray(new String[returnVar.size()]);
        Bastille.trimResults(returnArr);
        Bastille.formatMAC(returnArr);
        sharedData.addMACS(returnArr);
    }
    
    
    private String[] nslookupIP(String ip){
        ArrayList<String> commands = new ArrayList<>();
        commands.add("sudo");//gotta run it as root to resolve the IPs
        commands.add("nslookup");
        commands.add(ip);
        
        try {
            Process pr = Runtime.getRuntime().exec(commands.toArray(new String[commands.size()]));
            BufferedReader output = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String result;
            String totalResult = "";
            while ((result = output.readLine()) != null) totalResult += result;
            return totalResult.split("\n");//here's the return point
        }
        catch(IOException e) {
            System.err.println("ERROR: error while executing Soldier.");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
