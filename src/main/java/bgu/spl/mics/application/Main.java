package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		Gson gson = new Gson();
		try{
			JsonReader reader = new JsonReader(new FileReader(args[0]));
			Input input = gson.fromJson(reader,Input.class);

			/**
			 * initializing services
			 * */
			LandoMicroservice lando = new LandoMicroservice(input.getLando());
			R2D2Microservice r2d2 = new R2D2Microservice(input.getR2D2());
			for (Attack att : input.getAttacks()){
				Collections.sort(att.getSerials());
			} //sort serials
			LeiaMicroservice leia = new LeiaMicroservice(input.getAttacks());
			AtomicInteger totalAttacks = new AtomicInteger(0);
			AtomicInteger numOfAttacks = new AtomicInteger(input.getAttacks().length);
			Object C3P0andHanSoloLock = new Object();
			AtomicBoolean wasDeactivateSent = new AtomicBoolean(false);

			C3POMicroservice c3po = new C3POMicroservice();
			c3po.init(numOfAttacks,wasDeactivateSent,totalAttacks,C3P0andHanSoloLock);

			HanSoloMicroservice hanSolo = new HanSoloMicroservice();
			hanSolo.init(numOfAttacks,wasDeactivateSent,totalAttacks,C3P0andHanSoloLock);

			/**
			 * initializing passiveObjects
			 */
			Ewoks ewoks = Ewoks.getInstance();
			ewoks.init(input.getEwoks());

			Diary dairy = Diary.getInstance();
			dairy.initTotalAttacks(totalAttacks);

			/**
			 * initializing threads
			 */

			Thread leiaThread = new Thread(leia);
			Thread c3poThread = new Thread(c3po);
			Thread hanSoloThread = new Thread(hanSolo);
			Thread r2d2Thread = new Thread(r2d2);
			Thread landoThread = new Thread(lando);
			leiaThread.start();
			c3poThread.start();
			hanSoloThread.start();
			r2d2Thread.start();
			landoThread.start();

			//wait for simulation to finish
			leiaThread.join();
			c3poThread.join();
			hanSoloThread.join();
			r2d2Thread.join();
			landoThread.join();

			CreateOutput(args[1]);

		} catch (IOException e){
			System.out.println("illegal json");
			e.printStackTrace();
		}
		catch (InterruptedException ignored){}
	} //when constructing the services invoke init() also

	public static void CreateOutput(String path){
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			Diary diary = Diary.getInstance();
			FileWriter writer = new FileWriter(path);
			gson.toJson(diary, writer);
			//writer.flush();
			writer.close();
		} catch (IOException ioe) {}
	}
}
