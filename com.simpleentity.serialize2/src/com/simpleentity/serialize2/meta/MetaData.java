package com.simpleentity.serialize2.meta;

import static com.simpleentity.util.BuilderUtil.positiveLong;
import static com.simpleentity.util.BuilderUtil.requiredBuilderField;

import java.util.Arrays;

import javax.annotation.CheckForNull;

import org.povworld.collection.AbstractComparator;
import org.povworld.collection.Collection;
import org.povworld.collection.CollectionUtil;
import org.povworld.collection.Comparator;
import org.povworld.collection.common.PreConditions;
import org.povworld.collection.mutable.ArrayList;

import com.simpleentity.annotation.Positive;
import com.simpleentity.entity.Entity;
import com.simpleentity.entity.EntityBuilder;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.util.StringUtil;

// TODO use polymorphism instead of MetaDataType?
public class MetaData extends Entity<MetaData> {

	private static final Comparator<Entry> ENTRY_COMPARATOR = new AbstractComparator<Entry>() {
		@Override
		public int compare(Entry entry1, Entry entry2) {
			return entry1.getId().compareTo(entry2.getId());
		}

		@Override
		public boolean isIdentifiable(Object object) {
			return object.getClass() == Entry.class;
		}
	};

	private final String domain;

	@Positive
	private final long version;

	private final MetaType metaType;

	private final String className; // TODO Store without domain?

	// Use an array for storing entries to avoid dependency on collection library in BootStrap.
	private final Entry[] entries;

	private MetaData(EntityId id, Builder builder) {
		super(id);
		this.domain = requiredBuilderField("domain", builder.domain);
		this.version = positiveLong("version", builder.version);
		this.metaType = requiredBuilderField("metaType", builder.metaType);
		this.className = requiredBuilderField("relativeClassName", builder.className);
		this.entries = CollectionUtil.toArray(builder.entries, new Entry[builder.entries.size()]);
		Arrays.sort(entries, ENTRY_COMPARATOR);
		if (!domain.isEmpty() && !className.startsWith(domain + ".")) {
			throw new IllegalStateException("Domain must be a prefix of class name but is not: " + className
					+ " does not start with " + domain);
		}
	}

	public String getDomain() {
		return domain;
	}

	public long getVersion() {
		return version;
	}

	public MetaType getMetaType() {
		return metaType;
	}

	public String getClassName() {
		return className;
	}

	public Iterable<Entry> getEntries() {
		return Arrays.asList(entries);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[domain="+getDomain()+", version="+getVersion()+", metaType="+getMetaType()+", class="+getClassName()+", entries={");
		sb.append(StringUtil.join(getEntries(), ", "));
		sb.append("}]");
		return sb.toString();
	}

	@Override
	public Builder toBuilder() {
		return new Builder(this)
			.setClassName(className)
			.setDomain(domain)
			.setMetaType(metaType)
			.setVersion(version)
			.addAllEntries(ArrayList.<Entry>of(entries));
	}

	public static Builder newBuilder() {
		return new Builder(null);
	}

	public static class Builder extends EntityBuilder<MetaData> {

		private final ArrayList<Entry> entries = new ArrayList<>();

		@CheckForNull
		private String className;
		@CheckForNull
		private String domain;
		private long version = -1;
		@CheckForNull
		private MetaType metaType;

		private Builder(@CheckForNull MetaData entity) {
			super(entity);
		}

		public Builder setClassName(String className) {
			this.className = className;
			return this;
		}

		public Builder setDomain(String domain) {
			this.domain = domain;
			return this;
		}

		public Builder setVersion(@Positive long version) {
			this.version = PreConditions.paramPositive(version);
			return this;
		}

		public Builder setMetaType(MetaType type) {
			this.metaType = type;
			return this;
		}

		public Builder addEntry(String id, Type type) {
			// TODO verify that there are no duplicate ids
			this.entries.push(new Entry(id, type));
			return this;
		}

		public Builder addAllEntries(Collection<? extends Entry> entries) {
			// TODO verify that there are no duplicate ids
			this.entries.pushAll(entries);
			return this;
		}

		@Override
		protected MetaData build(EntityId id) {
			return new MetaData(id, this);
		}
	}

}
