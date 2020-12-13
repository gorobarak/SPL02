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
		msgTypeToSubsQ.putIfAbsent(type, new LinkedBlockingQueue<>());
		msgTypeToSubsQ.get(type).add(m);
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
		if (q != null) {
			for (MicroService m : q) {
				microserviceToMsgQ.get(m).add(b);
			}
			//TODO notify all?
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();
		BlockingQueue<MicroService> q = msgTypeToSubsQ.get(e.getClass());
		if (q != null) {
			MicroService next = q.poll();
			if (next != null) {
				microserviceToMsgQ.get(next).add(e);
				futureMap.put(e, future);
				q.add(next); //return  to end of the queue
				return future;
			}
		}
        return null;
	}

	@Override
	public void register(MicroService m) {
		microserviceToMsgQ.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		for (Map.Entry<Class<? extends Message>, LinkedBlockingQueue<MicroService>> e : msgTypeToSubsQ.entrySet()) {
			e.getValue().remove(m); // go over all msg types and remove m from q
		}
		microserviceToMsgQ.remove(m); // delete m's queue
		//TODO synchronized?
		//TODO delete events from the q itself?
		//TODO resolve events in his queue? mark them as done?
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
