package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.CheckAttackStatusBroadcast;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.messages.AttackEvent;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private Diary diary = Diary.getInstance();
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class,(terminateBroadcast) -> {
            diary.setLeiaTerminate(System.currentTimeMillis());
            terminate();});
        for (Attack att : attacks){
            sendEvent(new AttackEvent(att));
        } //ATTTTTACKKKK!!!
        sendBroadcast(new CheckAttackStatusBroadcast()); //com'n are the attacks finished already??
    }
}
