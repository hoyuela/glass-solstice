package com.discover.mobile.card.common.utils;

import java.io.File;

public class PDFObject {

	private File file;
	private boolean success;
	private String title;
	private String message;

	public File getFile() {
		return file;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public PDFObject(File file, boolean result) {
		super();
		this.file = file;
		this.success = result;
	}

	public PDFObject(File file, boolean result, String title, String message) {
		super();
		this.file = file;
		this.success = result;
		this.title = title;
		this.message = message;
	}
}
