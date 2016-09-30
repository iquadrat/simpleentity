package com.simpleentity.serialize2.meta;

import static com.simpleentity.util.BuilderUtil.positiveLong;
import static com.simpleentity.util.BuilderUtil.requiredBuilderField;

import javax.annotation.CheckForNull;

import org.povworld.collection.EntryIterator;
import org.povworld.collection.Map;
import org.povworld.collection.common.PreConditions;
import org.povworld.collection.mutable.HashMap;

import com.simpleentity.annotation.Positive;
import com.simpleentity.entity.Entity;
import com.simpleentity.entity.EntityBuilder;
import com.simpleentity.entity.id.EntityId;

// TODO use polymorphism instead of MetaDataType?
public class MetaData extends Entity<MetaData> {

	private final String domain;

	@Positive
	private final long version;

	private final MetaType metaType;

	private final String className;

	// TODO replace with immutable Map version.
	private final Map<String, Type> entries;

	private MetaData(EntityId id, Builder builder) {
		super(id);
		this.domain = requiredBuilderField("domain", builder.domain);
		this.version = positiveLong("version", builder.version);
		this.metaType = requiredBuilderField("metaType", builder.metaType);
		this.className = requiredBuilderField("relativeClassName", builder.className);
		this.entries = new HashMap<>(builder.entries);
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

	public EntryIterator<String, Type> getEntries() {
		return entries.entryIterator();
	}

	@Override
	public Builder toBuilder() {
		return new Builder(this)
			.setClassName(className)
			.setDomain(domain)
			.setMetaType(metaType)
			.setVersion(version)
			.addAllEntries(entries);
	}

	public static Builder newBuilder() {
		return new Builder(null);
	}

	public static class Builder extends EntityBuilder<MetaData> {

		private final HashMap<String, Type> entries = new HashMap<>();

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
			this.entries.put(id, type);
			return this;
		}

		public Builder addAllEntries(Map<String, Type> entries) {
			this.entries.putAll(entries);
			return this;
		}

		@Override
		protected MetaData build(EntityId id) {
			if (!className.startsWith(domain + ".")) {
				throw new IllegalStateException("Domain must be a prefix of class name but is not: " + className
						+ " does not start with " + domain);
			}
			return new MetaData(id, this);
		}

	}

}
