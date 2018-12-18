// echo server
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

class Inventory{ 
    public static int cookies;
    public static int maggi;
    public static int lays;
    public static int choc;
    public static int sand;
    public static int paratha;

    public static String takeCookies(int n){
        if( (cookies - n < 0) ){
            return ("Sorry......Not Enough Cookies!\n");
        }
        cookies -= n;
        if( (cookies < 10) ){
            addToPurchaseList("Cookies");
        }
        return ("true");
    }

    public static String takeMaggi(int n){
        if( (maggi - n < 0) ){
            return ("Sorry......Not Enough Maggi!\n");
        }
        maggi -= n;
        if( (maggi < 10) ){
            addToPurchaseList("Maggi");
        }
        return ("true");
    }

    public static String takeLays(int n){
        if( (lays - n < 0) ){
            return ("Sorry......Not Enough Lays!\n");
        }
        lays -= n;
        if( (lays < 10) ){
            addToPurchaseList("Lays");
        }
        return ("true");
    }

    public static String takeChoc(int n){
        if( (choc - n < 0) ){
            return ("Sorry......Not Enough Chocolates!\n");
        }
        choc -= n;
        if( (choc < 10) ){
            addToPurchaseList("Chocolates");
        }
        return ("true");
    }

    public static String takeSand(int n){
        if( (sand - n < 0) ){
            return ("Sorry......Not Enough Sandwiches!\n");
        }
        sand -= n;
        if( (sand < 10) ){
            addToPurchaseList("Sandwiches");
        }
        return ("true");
    }

    public static String takeParatha(int n){
        if( (paratha - n < 0) ){
            return ("Sorry......Not Enough Parathas!\n");
        }
        paratha -= n;
        if( (paratha < 10) ){
            addToPurchaseList("Parathas");
        }
        return ("true");
    }


    public static void addToPurchaseList(String item){
        try
        {
            String filename= "PurchaseList.txt";
            FileWriter fw = new FileWriter(filename,true); //the true will append the new data
            fw.write(item + "\n");//appends the string to the file
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }
}

class Order{
    String name;
    String date;
    int tea;
    int coffee;
    int cookies;
    int maggi;
    int lays;
    int choc;
    int sand;
    int paratha;

    Order(String n, String d, int t, int cof, int cok, int m, int l, int cho, int s, int p){
        name = n;
        date = d;
        tea = t;
        coffee = cof;
        cookies = cok;
        maggi = m;
        lays = l;
        choc = cho;
        sand = s;
        paratha = p;
    }

    public void makeInvoice(){
        String fileName = name + "_" + date + ".txt";

        List<String> lines = new ArrayList<>();
        lines.add(date + " " + name);
        lines.add("Tea:          " + Integer.toString(tea) + " * 5      " + Integer.toString(tea*5));
        lines.add("Coffee:       " + Integer.toString(coffee) + " * 6      " + Integer.toString(coffee*6));
        lines.add("Cookies:      " + Integer.toString(cookies) + " * 20      " + Integer.toString(cookies*20));
        lines.add("Maggi:        " + Integer.toString(maggi) + " * 15      " + Integer.toString(maggi*15));
        lines.add("Lays:         " + Integer.toString(lays) + " * 10      " + Integer.toString(lays*10));
        lines.add("Chocolate:    " + Integer.toString(choc) + " * 25      " + Integer.toString(choc*25));
        lines.add("Sandwich:     " + Integer.toString(sand) + " * 30      " + Integer.toString(sand*30));
        lines.add("Paratha:      " + Integer.toString(paratha) + " * 18      " + Integer.toString(paratha*18));

        Path file = Paths.get(fileName);
        
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class OrderListClass{
    public static ArrayList<Order> orderList = new ArrayList<Order>();

    public static void addToList(Order orderLatest){
        orderList.add(orderLatest);
    }

    public static int teaWait;
    public static int coffeeWait;
    public static int lastOrderWait;

}

public class Server_X_Client {

public static void main(String args[]){

    Inventory.cookies = 100;
    Inventory.maggi = 100;
    Inventory.lays = 100;
    Inventory.choc = 100;
    Inventory.sand = 100;
    Inventory.paratha = 100;

    OrderListClass.coffeeWait = 0;
    OrderListClass.teaWait = 0;
    OrderListClass.lastOrderWait = 0;

    Socket s=null;
    ServerSocket ss2=null;
    System.out.println("Server Listening......");
    try{
        ss2 = new ServerSocket(4445); // can also use static final PORT_NUM , when defined

    }
    catch(IOException e){
    e.printStackTrace();
    System.out.println("Server error");

    }

    while(true){
        try{
            s= ss2.accept();
            System.out.println("connection Established");
            ServerThread st=new ServerThread(s);
            st.start();

        }

    catch(Exception e){
        e.printStackTrace();
        System.out.println("Connection Error");

    }
    }

}

}

class ServerThread extends Thread{  

    String line=null;
    BufferedReader  is = null;
    PrintWriter os=null;
    Socket s=null;

    public ServerThread(Socket s){
        this.s=s;
    }

    public void run() {
    try{
        is= new BufferedReader(new InputStreamReader(s.getInputStream()));
        os=new PrintWriter(s.getOutputStream());

    }catch(IOException e){
        System.out.println("IO error in server thread");
    }

    try {
        line=is.readLine();
        while(line.compareTo("QUIT")!=0){

            String[] orderQuants = line.split("\\s+");
            
            Order thisOrder = new Order(this.getName(), "date", Integer.parseInt(orderQuants[0]), Integer.parseInt(orderQuants[1]), Integer.parseInt(orderQuants[2]), Integer.parseInt(orderQuants[3]), Integer.parseInt(orderQuants[4]), Integer.parseInt(orderQuants[5]), Integer.parseInt(orderQuants[6]), Integer.parseInt(orderQuants[7]));

            OrderListClass.addToList(thisOrder);

            boolean allThere = true;
            String itemStatus = "";
            String outString = "";

            if (thisOrder.tea > 0) {
                OrderListClass.teaWait += Integer.parseInt(orderQuants[0]);
            }

            if (thisOrder.coffee > 0) {
                OrderListClass.coffeeWait += Integer.parseInt(orderQuants[1]);
            }

            int orderTime = OrderListClass.lastOrderWait;

            if (OrderListClass.teaWait > orderTime){
                orderTime = OrderListClass.teaWait;
            }

            if (OrderListClass.coffeeWait > orderTime){
                orderTime = OrderListClass.coffeeWait;
            }

            orderTime += 2;
            OrderListClass.lastOrderWait = orderTime;

            if (thisOrder.cookies > 0) {
                itemStatus = Inventory.takeCookies(Integer.parseInt(orderQuants[2]));
                if (!(itemStatus.equals("true"))) {
                    allThere = false;
                    // outString += itemStatus;
                    // os.println(itemStatus);
                    // os.flush();
                }
            }

            if (thisOrder.maggi > 0) {
                itemStatus = Inventory.takeMaggi(Integer.parseInt(orderQuants[3]));
                if (!(itemStatus.equals("true"))) {
                    allThere = false;
                    // outString += itemStatus;
                    // os.println(itemStatus);
                    // os.flush();
                }
            }

            if (thisOrder.lays > 0) {
                itemStatus = Inventory.takeLays(Integer.parseInt(orderQuants[4]));
                if (!(itemStatus.equals("true"))) {
                    allThere = false;
                    // outString += itemStatus;
                    // os.println(itemStatus);
                    // os.flush();
                }
            }

            if (thisOrder.choc > 0) {
                itemStatus = Inventory.takeChoc(Integer.parseInt(orderQuants[5]));
                if (!(itemStatus.equals("true"))) {
                    allThere = false;
                    // outString += itemStatus;
                    // os.println(itemStatus);
                    // os.flush();
                }
            }

            if (thisOrder.sand > 0) {
                itemStatus = Inventory.takeSand(Integer.parseInt(orderQuants[6]));
                if (!(itemStatus.equals("true"))) {
                    allThere = false;
                    // outString += itemStatus;
                    // os.println(itemStatus);
                    // os.flush();
                }
            }

            if (thisOrder.paratha > 0) {
                itemStatus = Inventory.takeParatha(Integer.parseInt(orderQuants[7]));
                if (!(itemStatus.equals("true"))) {
                    allThere = false;
                    // outString += itemStatus;
                    // os.println(itemStatus);
                    // os.flush();
                }
            }

            if(!allThere){
                outString += "Cannot Process Order!\n";
                os.println(outString);
                os.flush();
            }else{
                os.println("Order Placed. You will get it at " + orderTime);
                os.flush();
                thisOrder.makeInvoice();
            }

            System.out.println(Inventory.cookies);
            System.out.println(Inventory.maggi);
            System.out.println(Inventory.lays);
            System.out.println(Inventory.choc);
            System.out.println(Inventory.sand);
            System.out.println(Inventory.paratha);
            // System.out.println("Response to Client  :  "+line);
            line=is.readLine();
        }   
    } catch (IOException e) {

        line=this.getName(); //reused String line for getting thread name
        System.out.println("IO Error/ Client "+line+" terminated abruptly");
    }
    catch(NullPointerException e){
        line=this.getName(); //reused String line for getting thread name
        System.out.println("Client "+line+" Closed");
    }

    finally{    
    try{
        System.out.println("Connection Closing..");
        if (is!=null){
            is.close();
            System.out.println(" Socket Input Stream Closed");
        }

        if(os!=null){
            os.close();
            System.out.println("Socket Out Closed");
        }
        if (s!=null){
        s.close();
        line=this.getName();
        System.out.println(line + " Socket Closed ");
        }

        }
    catch(IOException ie){
        System.out.println("Socket Close Error");
    }
    }//end finally
    }
}