package com.simpleentity.db;

import com.simpleentity.db.transaction.Session;
import com.simpleentity.db.transaction.SessionListener;
import com.simpleentity.util.collection.ListenerHandle;
import com.simpleentity.util.collection.ListenerList;

public class Database {
	
	private final ListenerList<SessionListener> transactionListeners = new ListenerList<>();

	public ListenerHandle addSessionListener(SessionListener transactionListener) {
		return transactionListeners.add(transactionListener);
	}
	
	public Session getHeadSession() {
		return null; // TODO
	}
	
}
