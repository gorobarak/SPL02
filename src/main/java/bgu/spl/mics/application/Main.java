package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		//if (args.length <)//TODO
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
				att.getSerials().sort(new Comparator<Integer>() {
					public int compare(Integer o1, Integer o2) {
						return o1 - o2;
					}
				});
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

		} catch (IOException e){
			System.out.println("illegal json");
		}
	} //when constructing the services invoke init() also
}
