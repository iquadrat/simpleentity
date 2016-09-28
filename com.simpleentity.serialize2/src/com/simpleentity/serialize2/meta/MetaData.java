package com.simpleentity.serialize2.meta;

import static com.simpleentity.util.BuilderUtil.positiveLong;
import static com.simpleentity.util.BuilderUtil.requiredBuilderField;

import javax.annotation.CheckForNull;

import org.povworld.collection.common.PreConditions;
import org.povworld.collection.immutable.ImmutableArrayList;
import org.povworld.collection.immutable.ImmutableList;

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

	private final ImmutableList<Entry> entries;

	@CheckForNull
	private final EntityId elementTypeId;

	private MetaData(EntityId id, Builder builder) {
		super(id);
		this.domain = requiredBuilderField("domain", builder.domain);
		this.version = positiveLong("version", builder.version);
		this.metaType = requiredBuilderField("metaType", builder.metaType);
		this.className = requiredBuilderField("relativeClassName", builder.className);
		this.entries = builder.entries.build();
		this.elementTypeId = builder.elementTypeId;
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

	public ImmutableList<Entry> getEntries() {
		return entries;
	}

	@CheckForNull
	public EntityId getElementTypeId() {
		return elementTypeId;
	}

	@Override
	public EntityBuilder<MetaData> toBuilder() {
		return new Builder(); // FIXME
	}

	public EntityBuilder<MetaData> newBuilder() {
		return new Builder();
	}

	public static class Builder extends EntityBuilder<MetaData> {

		private final ImmutableArrayList.Builder<Entry> entries = ImmutableArrayList.newBuilder();

		@CheckForNull
		private String className;
		@CheckForNull
		private String domain = null;
		private long version = -1;
		@CheckForNull
		private MetaType metaType = null;
		@CheckForNull
		private EntityId elementTypeId;


		public void setClassName(String className) {
			this.className = className;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public void setVersion(@Positive long version) {
			this.version = PreConditions.paramPositive(version);
		}

		public void setMetaType(MetaType type) {
			this.metaType = type;
		}

		public void setElementTypeId(EntityId elementTypeId) {
			this.elementTypeId = elementTypeId;
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
