package bgu.spl.mics;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	
	private static MessageBusImpl instance = null;
	private Map<Event<?>, Future<?>> futureMap;
	private Map<MicroService, LinkedBlockingQueue<Message>> microserviceQ;
	private Map<Class<? extends Message>, LinkedBlockingQueue<MicroService>> msgQ;

	private MessageBusImpl(){

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
		msgQ.putIfAbsent(type, new LinkedBlockingQueue<>());
		msgQ.get(type).add(m);
	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		Future f = futureMap.get(e);
		f.resolve(result);
		//futureMap.get(e).resolve(result); TODO
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> q = msgQ.get(b.getClass());
		if (q != null) {
			for (MicroService m : q) {
				microserviceQ.get(m).add(b);
			}
			//TODO notify all?
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();
		BlockingQueue<MicroService> q = msgQ.get(e.getClass());
		if (q != null) {
			MicroService next = q.poll();
			if (next != null) {
				microserviceQ.get(next).add(e);
				futureMap.put(e, future);
				q.add(next); //return  to end of the queue
				return future;
			}
		}
        return null;
	}

	@Override
	public void register(MicroService m) {
		microserviceQ.put(m, new LinkedBlockingQueue<>());
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
