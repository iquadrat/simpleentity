package com.simpleentity.db.example;

import com.simpleentity.db.session.ReadWriteSession;

public class Main {
	
	public void crud(ReadWriteSession session) {
		MyEntity e1 = 
				session.create(
						MyEntity.newBuilder()
							.setName("foobar"));
		
		e1 = session.modify(
				e1.toBuilder()
					.setName("Hello World!"));

		session.delete(e1);
	}

}
