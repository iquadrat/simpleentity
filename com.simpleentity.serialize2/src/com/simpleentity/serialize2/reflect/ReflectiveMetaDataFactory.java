package com.simpleentity.serialize2.reflect;

import java.util.Arrays;
import java.util.List;

import com.simpleentity.annotation.Version;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaDataFactory;
import com.simpleentity.serialize2.meta.MetaDataRepository;
import com.simpleentity.serialize2.meta.MetaDataUtil;

public class ReflectiveMetaDataFactory implements MetaDataFactory {

	private static final long DEFAULT_VERSION = 0;
	// private static class DomainInfo extends ValueObject {
	// private final String name;
	// private final long version;
	// public DomainInfo(String name, long version) {
	// this.name = name;
	// this.version = version;
	// }
	// }

	public ReflectiveMetaDataFactory() {
	}

	@Override
	public MetaData create(Class<?> class_, EntityId id, MetaDataRepository repository) {
		String domain = getDomain(class_);
		long version = getVersion(domain);
		return MetaDataUtil.getMetaDataByReflection(class_, domain, version, id, repository);
	}

	private long getVersion(String domain) {
		try {
			// TODO cache?
			Class<?> class_ = Class.forName(domain + ".package-info");
			Version version = class_.getPackage().getAnnotation(Version.class);
			if (version == null) {
				return DEFAULT_VERSION;
			}
			return version.value();
		} catch (ClassNotFoundException e) {
			return DEFAULT_VERSION;
		}
	}

	private String getDomain(Class<?> class_) {
		String name = class_.getName();
		List<String> parts = Arrays.asList(name.split("\\."));
		if (parts.size() >= 2) {
			return parts.get(0) + "." + parts.get(1);
		}
		return parts.get(0);
	}

}
