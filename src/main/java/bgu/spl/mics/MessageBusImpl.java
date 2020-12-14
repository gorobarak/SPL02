package bgu.spl.mics;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	
	private static class instanceHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	} // will be initialized only when called getInstance()
	private Map<Event<?>, Future<?>> futureMap;
	private Map<MicroService, LinkedBlockingQueue<Message>> microserviceToMsgQ; // map microservices to their msg queues
	private Map<Class<? extends Message>, LinkedBlockingQueue<MicroService>> msgTypeToSubsQ; // map msg types to microservices

	private MessageBusImpl(){
		futureMap = new ConcurrentHashMap<>();
		microserviceToMsgQ = new ConcurrentHashMap<>();
		msgTypeToSubsQ = new ConcurrentHashMap<>();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subscribeMessage(type, m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		subscribeMessage(type, m);
    }

    private void subscribeMessage(Class<? extends Message> type, MicroService m) {
		msgTypeToSubsQ.putIfAbsent(type, new LinkedBlockingQueue<>()); //first subscription to this msg type
		try {
			msgTypeToSubsQ.get(type).put(m); //add microservice to msg type queue
		} catch (InterruptedException ignored) {}
	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		Future f = futureMap.get(e);
		f.resolve(result);
		//futureMap.get(e).resolve(result); TODO
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> q = msgTypeToSubsQ.get(b.getClass());
		if (q != null) { // q == null means no subscribers to this msg type
			for (MicroService m : q) {
				try {
					microserviceToMsgQ.get(m).put(b);
				} catch (InterruptedException ignored) {}
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();
		LinkedBlockingQueue<MicroService> q = msgTypeToSubsQ.get(e.getClass());
		if (q != null) {
			synchronized (q) { //to ensure round robin when this dequeue a subscriber all other sendEvent must wait from this to finish
				MicroService next = q.poll(); // the next microservice that should receive this kind of event
				if (next != null) { //next == null means the q is empty. could happen when all subscribers called unregister.
					try {
						futureMap.put(e, future);
						microserviceToMsgQ.get(next).put(e);
						q.put(next); //return to end of the queue
					} catch (InterruptedException ignored) { }
					return future;
				}
			}
		}
        return null;
	}

	@Override
	public void register(MicroService m) {
		microserviceToMsgQ.putIfAbsent(m, new LinkedBlockingQueue<>()); //ifAbsent - in case someone tries to register a microservice that is already registered
	}

	@Override
	public void unregister(MicroService m) {
		for (Map.Entry<Class<? extends Message>, LinkedBlockingQueue<MicroService>> e : msgTypeToSubsQ.entrySet()) {
			synchronized (e.getValue()){ //to prevent a situation in which the microservice gets re-inserted in sendEvent
				e.getValue().remove(m); // go over all msg types and remove m from q (if exists)
			}
		}
		microserviceToMsgQ.remove(m); // delete m's queue
		//no need to handle remaining msgs in q
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		LinkedBlockingQueue<Message> msgQ = microserviceToMsgQ.get(m);
		if (msgQ == null){
			throw new IllegalStateException("microservice wasn't registered");
		}
		return msgQ.take(); //waits for a message in case queue is empty
	}

	public static MessageBusImpl getInstance() {
		return instanceHolder.instance;
	}


}
