package com.simpleentity.db.example;

import com.simpleentity.db.transaction.ReadWriteSession;

public class Main {
	
	public void crud(ReadWriteSession session) {
		MyEntity e1 = 
				MyEntity
					.newBuilder()
						.setName("foobar")
						.build(session);
		
		e1 = e1
			.modify()
				.setName("Hello World!")
				.build(session);

		e1.delete(session);
	}

}
