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
	private ConcurrentHashMap<Class<? extends Event<?>>, Vector<String>> roundRobin;

	private static MessageBusImpl msgBus = null;

	public static MessageBusImpl getInstance(){
		if (msgBus == null)
			msgBus = new MessageBusImpl();
		return msgBus;
		}

	/**
	 * CTR.
	 */
	private MessageBusImpl(){
		this.names = new Vector<String>(0,1);
		this.interestsMap = new ConcurrentHashMap<>(0);
		this.queueMap = new ConcurrentHashMap<>(0);
		this.EventToFuture = new ConcurrentHashMap<>(0);
		this.roundRobin = new ConcurrentHashMap<>(0);
		last = null;
	}
	
	@Override
	/**
	 * Adds the relevant interest to the interests HashMap.
	 */
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		this.interestsMap.get(m.getName()).add(type);
		synchronized(roundRobin) {
			if (!this.roundRobin.containsKey(type)) {
				this.roundRobin.put(type, new Vector<>(0, 1));
			}
			if (!this.roundRobin.get(type).contains(m.getName()))
				this.roundRobin.get(type).add(m.getName());        // If this queue doesn't contain this name, add it to the end of the queue (vector).
		}
	}

	@Override
	/**
	 * Adds the relevant interest to the interests HashMap.
	 */
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		this.interestsMap.get(m.getName()).add(type);
    }

	@Override
	/**
	 * Resolve the relevant future.
	 */
	public <T> void complete(Event<T> e, T result) {
		Future f = this.EventToFuture.get(e);
		f.resolve(result);
		EventToFuture.remove(e);
	}

	@Override
	/**
	 * Send a message to all M-S's that are interested in this message-type.
	 */
	public void sendBroadcast(Broadcast b) {
		synchronized (names) {
			for (String name : names) {
				synchronized (this.queueMap.get(name)) {
					if (this.interestsMap.get(name).contains(b.getClass())) {		// Enter ony if the Micro-Service is interested in this message-type.
						this.queueMap.get(name).add(b);        // Adds broadcast b to all relevant M-S.
						this.queueMap.get(name).notifyAll();	// Notify all threads on the monitor of this message-queue.
					}
				}
			}
		}
	}
	
	@Override
	/**
	 * Send an event, to the desired Micro-Service.
	 */
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<T>();
		String turn = roundRobin(e);
		if (turn!=null) {
			synchronized (this.queueMap.get(turn)) {
				this.queueMap.get(turn).add(e);        // Adds Message (Event in this case) e to the relevant M-S's queue (vector in our implementation)..
				this.EventToFuture.put(e, future);        // Save couple (e,future), in order to connect between them later when event is completed.
				this.queueMap.get(turn).notifyAll();        // Notify all threads on the monitor of this message-queue.
			}
			return future;
		}
		return null;
	}

	@Override
	/**
	 * Register the given Micro-Service to the Message-Bus, by creating a message-queue, an interests-Map and adding it to the names that are registered until now.
	 */
	public void register(MicroService m) {
		synchronized(names) {
			if (!this.names.contains(m.getName())) {
				this.queueMap.put(m.getName(), new Vector<Message>());
				this.interestsMap.put(m.getName(), new Vector<Class<? extends Message>>());
				this.names.add(m.getName());
			}
		}
	}

	@Override
	/**
	 * unregister this M-S from this Message-Bus, by removing it from all data-bases.
	 */
	public void unregister(MicroService m) {
		if (this.names.contains(m.getName())){
			this.queueMap.remove(m.getName());
			this.interestsMap.remove(m.getName());
			names.remove(m.getName());
			m.terminate();
		}
	}

	@Override
	/**
	 * Micro-Service m waits for a message. It waits if the it's message-queue is empty.
	 */
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (this.queueMap.get(m.getName())){
			while (this.queueMap.get(m.getName()).isEmpty()) {		// if there is a message, return it. If not, wait.
				this.queueMap.get(m.getName()).wait();
			}
		Message msg = queueMap.get(m.getName()).firstElement();
		queueMap.get(m.getName()).remove(0);
		return msg;
		}
	}

	/**
	 * This method decides who between HanSolo and C3PO will get an attackEvent, in the round-robin manner.
	 * @return
	 */
	private String roundRobin(Event e){
		String out = null;
		synchronized (roundRobin) {
			if (roundRobin.containsKey(e.getClass()) && !roundRobin.get(e.getClass()).isEmpty()) {
				out = roundRobin.get(e.getClass()).remove(0);
				while (!this.names.contains(out) && !roundRobin.get(e.getClass()).isEmpty()) {
					out = roundRobin.get(e.getClass()).remove(0);
				}
				if (this.names.contains(out)) {
					roundRobin.get(e.getClass()).add(out);        // add the returned M-S to the end of the Vector, and increment the size one.
				} else
					out = null;
			}
			return out;
		}
	}
}
