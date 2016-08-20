package com.simpleentity.db.session;

public interface SessionListener {
	public void changed(Session session, ChangeList changes);

	public void closed(Session session);

	public void forked(Session session);

	public void merged(Session session);

	public void reverted(Session session);

}
