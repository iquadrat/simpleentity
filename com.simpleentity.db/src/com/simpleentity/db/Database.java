package com.simpleentity.db;

import com.simpleentity.db.session.Session;
import com.simpleentity.db.session.SessionListener;
import com.simpleentity.util.collection.ListenerHandle;

public interface Database {
	
	public Session createHeadSession();
	
	public ListenerHandle addSessionListener(SessionListener sessionListener);
	
}
