package com.simpleentity.db.log;

public class Options {

	private final String dbRoot;
	private final String dbPrefix;
	private final long fileSplitSize;

	public Options(String dbRoot, String dbPrefix, long fileSplitSize) {
		this.dbRoot = dbRoot;
		this.dbPrefix = dbPrefix;
		this.fileSplitSize = fileSplitSize;
	}

	public String getDbRoot() {
		return dbRoot;
	}

	public String getDbPrefix() {
		return dbPrefix;
	}

	public long getFileSplitSize() {
		return fileSplitSize;
	}

}
