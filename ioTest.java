import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;

class Student {
	long RollNumber;
	String Name;
	String Webmail;
	int Marks;
	String Teacher;

	public Student(long r, String n, String w, int m, String t) {
		RollNumber = r;
		Name = n;
		Webmail = w;
		Marks = m;
		Teacher = t;
	}

	public void UpdateMarks(String ta, boolean add, int diff) {
		if ((Teacher.equals("CC")) && !(ta.equals("CC"))) { 
			System.out.println("Can't Update...Only CC can do this!");
			return;
		}
		if (add == true) {
			Marks = Marks + diff;
		}
		if (add == false) {
			Marks = Marks - diff;
		}
		Teacher = ta;

		if (add == true) {
			System.out.println(Teacher + " increased " + diff + " for " + RollNumber);
		}else {
			System.out.println(Teacher + " decreased " + diff + " for " + RollNumber);
		}
	}
}

class UpdateBlock{
	String Teacher;
	long RollNumber;
	boolean upDown;
	int Marks;

	public UpdateBlock(String t, long r, boolean ud, int m) {
		Teacher = t;
		RollNumber = r;
		upDown = ud;
		Marks = m;
	}
}

class ThreadNow extends Thread {
   private Thread t;
   private String threadName;
   Student studNow;
   UpdateBlock updtNow;

   ThreadNow(String name, Student st, UpdateBlock up) {
      threadName = name;
      studNow = st;
      updtNow = up;
   }
   
   public void run() {
      synchronized(studNow) {
         studNow.UpdateMarks(updtNow.Teacher, updtNow.upDown, updtNow.Marks);
      }
      System.out.println("Thread " +  threadName + " exiting.");
   }

   public void start () {
      System.out.println("Starting " +  threadName );
      if (t == null) {
         t = new Thread (this, threadName);
         t.start ();
      }
   }
}

public class ioTest {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scannerFile = new Scanner(new File("/Users/kushalksvs/Desktop/prog_lab/Stud_Info.txt"));
        scannerFile.useDelimiter(",");

        HashMap<Long, Student> mapper = new HashMap<Long, Student>();

        while(scannerFile.hasNext()) {
        	long r = Long.parseLong(scannerFile.next().replace("\n", ""));
        	String n = scannerFile.next();
        	String w = scannerFile.next();
        	int m = Integer.parseInt(scannerFile.next());
        	String t = scannerFile.next().replace("\n", "");

        	mapper.put(r, new Student(r, n, w, m, t));

        	System.out.println("Stud: " + r + " | " + n + " | " + w + " | " + m + " | " + t);
        }
        scannerFile.close();

        Scanner scannerConsole = new Scanner(System.in);

        ArrayList<UpdateBlock> updates = new ArrayList<UpdateBlock>();

		int intent = 1;

		while (intent > 0){

			System.out.print("What to do ?  0-> Exit\n              1-> View\n              2-> Update\n ");
			intent = Integer.parseInt(scannerConsole.next());

			if (intent == 1) {
				System.out.println("Updating Data!");

				if (updates != null && updates.isEmpty()) {
					System.out.println("No updates to be done!");
					continue;
				}

				int listSize = updates.size();

				ArrayList<ThreadNow>threads = new ArrayList<ThreadNow>();

				for (int i = 0; i < listSize; i++) {
					UpdateBlock temp = updates.get(i);
					Student studHere = mapper.get(temp.RollNumber);

					threads.add(new ThreadNow( Integer.toString(i+1), studHere, temp));
				}

				for (int i = 0; i < listSize; i++) {
					ThreadNow curr = threads.get(i);
					curr.start();
				}

				for (int i = 0; i < threads.size(); i++) {
					ThreadNow curr = threads.get(i);
					try {
						curr.join();
			     	} catch ( Exception e) {
			        	System.out.println("Interrupted");
			     	}
				}

				updates.clear();

				for (Long key : mapper.keySet()) {
					System.out.println("Stud: " + mapper.get(key).RollNumber + " | " + mapper.get(key).Name + " | " + mapper.get(key).Webmail + " | " + mapper.get(key).Marks + " | " + mapper.get(key).Teacher);
				}

			}else if (intent == 2) {
				System.out.print("Enter Teacher's Name: ");
		        String teacherNow = scannerConsole.next();
		        System.out.print("Enter Student Roll Number: ");
		        Student ss = mapper.get(Long.parseLong(scannerConsole.next()));
		        System.out.print("Update Mark: 1-> Increase\n             2-> Decrease\n");
		        int incDec = Integer.parseInt(scannerConsole.next());
		        boolean updateChoice = true;
		        if (incDec == 2) {
		        	updateChoice = false;
		        }
		        if (updateChoice == true) {
		        	System.out.print("Mark to add: ");
		        }else{
		        	System.out.print("Mark to deduct: ");
		        }
		        int markDiff = Integer.parseInt(scannerConsole.next());

		        updates.add(new UpdateBlock(teacherNow, ss.RollNumber, updateChoice, markDiff));
		        System.out.println(updates.size() + " updates pending.");
			}
		}
		System.out.print("Exiting....\n");
    }

}