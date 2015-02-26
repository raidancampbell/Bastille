import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by aidan on 2/25/15.
 */
public class Soldier implements Runnable{
    
    private int thirdAddrLB;
    private int thirdAddrUB;
    
    public Soldier(int LB, int UB){
        this.thirdAddrLB = LB;
        this.thirdAddrUB = UB;
    }

    public void run() {
        String firstSection = "129.22";
        int fourthAddr = 0;
        ArrayList<String> returnVar = new ArrayList<>();
        for(;thirdAddrLB<thirdAddrUB;thirdAddrLB++){
            for(fourthAddr = 0;fourthAddr<256;fourthAddr++){
                String[] lookupValue = nslookupIP(String.format("%s.%d.%d",firstSection , thirdAddrLB, fourthAddr));
                for(int i = 0; i<lookupValue.length;i++)returnVar.add(lookupValue[i]);//yay, O(n) runtime!
            }
        }
        String[] returnArr = returnVar.toArray(new String[returnVar.size()]);
        Bastille.trimResults(returnArr);
        Bastille.formatMAC(returnArr);
        //return Bastille.flatten(returnArr);
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
