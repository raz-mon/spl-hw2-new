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


/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		Gson gson = new Gson();
		try {
			Reader reader = new FileReader(args[0]);
			input in = gson.fromJson(reader, input.class);
			Diary diary = Diary.getInstance();
			simulate(in);
			outputConfig(diary, args[1]);
		}
		catch(Exception e){
			System.out.println("problem accured");
		}
	}

	public static void simulate(input in){
		Ewoks ewks = Ewoks.getInstance(in.getEwoks());		// Ewoks is single-tone -> only one instance (this one) will be used through-out the program.

		HanSoloMicroservice Han = new HanSoloMicroservice();
		C3POMicroservice C3PO = new C3POMicroservice();
		R2D2Microservice R2D2 = new R2D2Microservice(in.getR2D2());
		LandoMicroservice Lando = new LandoMicroservice(in.getLando());
		LeiaMicroservice Leia = new LeiaMicroservice(in.getAttacks());

		Thread t1 = new Thread(Han);
		Thread t2 = new Thread(C3PO);
		Thread t3 = new Thread(R2D2);
		Thread t4 = new Thread(Lando);
		Thread t5 = new Thread(Leia);

		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();

		try{
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
		}catch(Exception e) {System.out.println("problem accured with joining threads");}		// In order to get a right answer at outputConfig.
	}

	/**
	 *The purpose of this method is to generate a output file in the desired location.
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

