package bgu.spl.mics;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	
	private static MessageBusImpl instance = null;
	private Map<Event<?>, Future<?>> futureMap;
	private Map<MicroService, LinkedBlockingQueue<Message>> microserviceToMsgQ; // map microservices to their msg queues
	private Map<Class<? extends Message>, LinkedBlockingQueue<MicroService>> msgToMicroserviceQ; // map msg types to microservices

	private MessageBusImpl(){
		futureMap = new ConcurrentHashMap<>();
		microserviceToMsgQ = new ConcurrentHashMap<>();
		msgToMicroserviceQ = new ConcurrentHashMap<>();
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
		msgToMicroserviceQ.putIfAbsent(type, new LinkedBlockingQueue<>());
		msgToMicroserviceQ.get(type).add(m);
	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		Future f = futureMap.get(e);
		f.resolve(result);
		//futureMap.get(e).resolve(result); TODO
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> q = msgToMicroserviceQ.get(b.getClass());
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
		BlockingQueue<MicroService> q = msgToMicroserviceQ.get(e.getClass());
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
		
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		
		return null;
	}

	public static MessageBusImpl getInstance() {
		if (instance == null){
			instance =  new MessageBusImpl();
		}
		return instance;
	}


}
