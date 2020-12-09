package bgu.spl.mics.application;
import java.io.FileReader;
import java.io.FileWriter;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.input;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.util.concurrent.CountDownLatch;


/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {

	public static CountDownLatch countDownLatch = new CountDownLatch(4);

	public static void main(String[] args) {
		Gson gson = new Gson();
		try {
			Reader reader = new FileReader(args[0]);
			input in = gson.fromJson(reader, input.class);		// Read in-to in the data in the input Json file.
			Diary diary = Diary.getInstance();					// Initialize diary instance. (Resolves the problem of multi-thread safe singleton if we do this here).
			simulate(in);										// run simulate. The progam will continue to the next line only when all of the simulation is done.
			outputConfig(diary, args[1]);						// Make output Json-file with the data in the diary. put it in args[1] (place & name).
		}
		catch(Exception e){ System.out.println("problem accured"); }
	}

	/**
	 * Here the whole program will run. Initialize all Micro-Services. Start their threads and make sure all are done (closed) before exiting the program.
	 * @param in
	 */
	public static void simulate(input in){
		Ewoks ewks = Ewoks.getInstance(in.getEwoks());		// Ewoks is single-tone -> only one instance (this one) will be used through-out the program. So it is initialized here.

		HanSoloMicroservice Han = new HanSoloMicroservice();
		C3POMicroservice C3PO = new C3POMicroservice();
		R2D2Microservice R2D2 = new R2D2Microservice(in.getR2D2());
		LandoMicroservice Lando = new LandoMicroservice(in.getLando());
		LeiaMicroservice Leia = new LeiaMicroservice(in.getAttacks());

		Thread t1 = new Thread(Han);		// Start a new thread with runnable Object Han.
		Thread t2 = new Thread(C3PO);
		Thread t3 = new Thread(R2D2);
		Thread t4 = new Thread(Lando);
		Thread t5 = new Thread(Leia);

		t1.start();
		t2.start();
		t3.start();
		t4.start();
		try{
			countDownLatch.await();		// Wait for all M-S's to finish initialize method.
		} catch (Exception e) {System.out.println("problem accured with countDownLatch await()."); }
		t5.start();		// Start Liea after all other M-S's have started.

		try{
			t1.join();					// Main thread waits for thread t1 to finish it's actions before continuing to the next row.
			t2.join();
			t3.join();
			t4.join();
			t5.join();
		}catch(Exception e) {System.out.println("problem accured with joining threads");}		// In order to get a right answer at outputConfig.
	}

	/**
	 * The purpose of this method is to generate a output file in the desired location, with the desired data.
	 * @param diary
	 * @param outPath
	 */
	public static void outputConfig(Diary diary, String outPath){
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		try{
			FileWriter writer = new FileWriter(outPath);
			g.toJson(diary,writer);
			writer.flush();
			writer.close();
		} catch(Exception e) {System.out.println("problem with generating output file");}
	}
}

