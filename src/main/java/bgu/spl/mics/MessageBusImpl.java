package bgu.spl.mics;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl.mics.application.messages.AttackEvent;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<String, Vector<Class<? extends Message>>> interestsMap;	  // Option to use concurrent Hashmap.
	private ConcurrentHashMap<String, Vector<Message>> queueMap;
	private ConcurrentHashMap<Event<?>, Future<?>> EventToFuture;	// could be a problematic call, cause Future is Generic.
	private Vector<String> names;
	private static String last;		// last will hold the last M-S between Han-Solo and C3PO that was assigned an AttackEvent.

	private static MessageBusImpl msgBus = null;

	public static MessageBusImpl getInstance(){
		if (msgBus == null)
			msgBus = new MessageBusImpl();
		return msgBus;
		}

	private MessageBusImpl(){
		this.names = new Vector<String>(0,1);
		this.interestsMap = new ConcurrentHashMap<>(0);
		this.queueMap = new ConcurrentHashMap<>(0);
		this.EventToFuture = new ConcurrentHashMap<>(0);
		last = null;
	}
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		this.interestsMap.get(m.getName()).add(type);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		this.interestsMap.get(m.getName()).add(type);
    }

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future f = this.EventToFuture.get(e);
		f.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
			for (String name : names) {
				synchronized (this.queueMap.get(name)) {
					if (this.interestsMap.get(name).contains(b.getClass()))
						this.queueMap.get(name).add(b);        // Adds broadcast b to all relevant M-S.
					this.queueMap.get(name).notifyAll();
				}
			}
	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<T>();
		if (e.getClass().equals(AttackEvent.class)){
			// send by round robin manner
			String turn = roundRobin();
			synchronized (this.queueMap.get(turn)) {
				this.queueMap.get(turn).add(e);        // Adds Message (Event in this case) e to the relevant M-S's queue (vector in our implementation)..
				this.EventToFuture.put(e, future);
				this.queueMap.get(turn).notifyAll();
			}
		}
		else		// All Events but AttackEvent
			for (String name : names){
				synchronized (this.queueMap.get(name)) {
					if (this.interestsMap.get(name).contains(e.getClass())) {
						this.queueMap.get(name).add(e);        // Adds message (Event in this case) e to the relevant M-S (only one of those if this is not and AttackEvent). [Make sure there is only one].
						this.EventToFuture.put(e, future);
						this.queueMap.get(name).notifyAll();
					}
				}
			}
		return future;
	}

	@Override
	public void register(MicroService m) {
		if (!this.names.contains(m.getName())){
			this.queueMap.put(m.getName(), new Vector<Message>());
			this.interestsMap.put(m.getName(), new Vector<Class<? extends Message>>());
			this.names.add(m.getName());
		}
	}

	@Override
	public void unregister(MicroService m) {
		if (this.names.contains(m.getName())){
			this.queueMap.remove(m.getName());
			this.interestsMap.remove(m.getName());
			names.remove(m.getName());
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (this.queueMap.get(m.getName())){
			// if there is a message, return it. If not, wait.
			while (this.queueMap.get(m.getName()).isEmpty()) {    // Can also be  a while (style Wait & Notify Design).
				this.queueMap.get(m.getName()).wait();           // maybe m.wait()? wait() -> this msgBus will wait?
			}
		Message msg = queueMap.get(m.getName()).firstElement();

		System.out.println(m.getName() + " got a message: " + msg);

		queueMap.get(m.getName()).remove(0);
		return msg;
		}
	}


	private static String roundRobin(){
		// The idea is to return the correct (the one that was not assigned the last AttackEvent) name (via String) according to the round-Robin manner, between Han-Solo
		// and C3PO. this will be used at sendEvent.
		if (last == null) {
			last = "Han";
			return "Han";        // If no-one was assigned a task yet -> return Han.
		}
		else if (last == "Han"){
			last = "C3PO";
			return "C3PO";		// If the last AttackEvent was handled by Han -> return C3PO.
		}
		else{
			last = "Han";
			return "Han";		// If the last AttackEvent was handled by C3PO -> return Han.
		}
	}
}
