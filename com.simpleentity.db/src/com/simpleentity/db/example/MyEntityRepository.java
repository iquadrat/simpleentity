package com.simpleentity.db.example;

import java.io.Closeable;
import java.lang.ref.Reference;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.povworld.collection.None;
import org.povworld.collection.mutable.HashMap;
import org.povworld.collection.persistent.PersistentHashMap;
import org.povworld.collection.persistent.PersistentMap;

import com.simpleentity.db.Database;
import com.simpleentity.db.EntityId;
import com.simpleentity.db.session.ChangeList;
import com.simpleentity.db.session.ChangeListener;
import com.simpleentity.db.session.ReadWriteSession;
import com.simpleentity.db.session.Session;
import com.simpleentity.db.session.SessionListener;
import com.simpleentity.db.session.ChangeList.Creation;
import com.simpleentity.db.session.ChangeList.Deletion;
import com.simpleentity.db.session.ChangeList.EntityChange;
import com.simpleentity.db.session.ChangeList.Modification;
import com.simpleentity.db.session.ChangeList.Visitor;
import com.simpleentity.util.collection.ListenerHandle;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public class MyEntityRepository implements Closeable {

	private static class Indices {
		private PersistentMap<String, EntityId> nameIndex;

		Indices() {
			this.nameIndex = PersistentHashMap.empty();
		}

		Indices(Indices base) {
			this.nameIndex = base.nameIndex;
		}
	}

	private final Object lock = new Object();

	private final HashMap<Session, Indices> indicesBySession = new HashMap<>();
	private final Database database;
	private final ListenerHandle listenerHandle;

	private final Session headSession;

	public MyEntityRepository(Database database) {
		this.database = database;
		this.headSession = this.database.createHeadSession();
		initialize();

		// Object is fully initialized now. Okay to expose 'this' from here on.
		this.listenerHandle = database.addSessionListener(new SessionListener() {
			@Override
			public void forked(Session session) {
				copyIndicesFromBase(session);
			}

			@Override
			public void merged(Session session) {
				copyIndicesFromBase(session);
			}

			@Override
			public void reverted(Session session) {
				copyIndicesFromBase(session);
			}

			@Override
			public void closed(Session session) {
				synchronized (lock) {
					indicesBySession.remove(session);
				}
			}

			@Override
			public void changed(Session session, ChangeList changes) {
				updateIndices(session, changes);
			}
		});
	}

	// Only called by constructor!
	private void initialize() {
		final AtomicReference<Indices> indices = new AtomicReference<>(new Indices());
		try (ReadWriteSession initSession = this.headSession.fork()) {
			initSession.addChangeListener(new ChangeListener() {
				@Override
				public void changed(ChangeList changes) {
					indices.set(updateIndices(indices.get(), changes));
				}
			});
			initSession.commit();
		}
		// No lock as the caller of the constructor is the only thread having access to the object.
		indicesBySession.put(headSession, indices.get());
	}

	private void updateIndices(Session session, ChangeList changes) {
		Indices indices = updateIndices(getIndices(session), changes);
		synchronized (lock) {
			indicesBySession.put(session, indices);
		}
	}

	private static Indices updateIndices(Indices indices, ChangeList changes) {
		MyEntityChangeVisitor visitor = new MyEntityChangeVisitor(indices);
		for (EntityChange<?> change : changes) {
			if (change.getType() != MyEntity.class) {
				continue;
			}
			@SuppressWarnings("unchecked")
			EntityChange<MyEntity> myEntityChange = (EntityChange<MyEntity>) change;
			myEntityChange.accept(visitor);
		}
		return visitor.getIndices();
	}

	private static final class MyEntityChangeVisitor implements Visitor<MyEntity, None> {
		private Indices indices;

		MyEntityChangeVisitor(Indices indices) {
			this.indices = indices;
		}

		private void add(MyEntity entity) {
			indices.nameIndex = indices.nameIndex.with(entity.getName(), entity.getEntityId());
		}

		private void remove(MyEntity entity) {
			indices.nameIndex = indices.nameIndex.without(entity.getName());
		}

		@Override
		public None visit(Creation<? extends MyEntity> creation) {
			add(creation.getNewEntity());
			return None.INSTANCE;
		}

		@Override
		public None visit(Modification<? extends MyEntity> modification) {
			remove(modification.getOldEntity());
			add(modification.getNewEntity());
			return None.INSTANCE;
		}

		@Override
		public None visit(Deletion<? extends MyEntity> deletion) {
			remove(deletion.getOldEntity());
			return None.INSTANCE;
		}

		public Indices getIndices() {
			return new Indices();
		}
	}

	// private <T extends Entity<T>> void handle(EntityChange<T> change) {
	// change.accept(new Visitor<T,None>() {
	//
	// @Override
	// public None visit(Creation<? extends T> creation) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public None visit(Modification<? extends T> modification) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public None visit(Deletion<? extends T> deletion) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// });
	// }

	private void copyIndicesFromBase(Session session) {
		synchronized (lock) {
			Indices base = indicesBySession.get(session.getBase());
			if (base == null) {
				throw new RuntimeException(/* TODO */);
			}
			indicesBySession.put(session, new Indices(base));
		}
	}

	private Indices getIndices(Session session) {
		synchronized (lock) {
			Indices result = indicesBySession.get(session);
			if (result == null) {
				throw new IllegalArgumentException("Invalid session " + session);
			}
			return result;
		}
	}

	@CheckForNull
	public MyEntity getByName(Session session, String name) {
		return resolveOrNull(session, getIndices(session).nameIndex.get(name));
	}

	private MyEntity resolveOrNull(Session session, @Nullable EntityId entityId) {
		if (entityId == null) {
			return null;
		}
		return session.getEntity(MyEntity.class, entityId);
	}

	@Override
	public void close() {
		headSession.close();
		listenerHandle.remove();
	}

}
