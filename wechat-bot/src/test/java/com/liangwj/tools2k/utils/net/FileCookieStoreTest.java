package com.liangwj.tools2k.utils.net;

import java.io.IOException;

import org.apache.http.impl.cookie.BasicClientCookie2;
import org.junit.jupiter.api.Test;

public class FileCookieStoreTest {

	private final String filename = "target/my-cookie.json";

	@Test
	public void testWrite() throws IOException {
		FileCookieStore obj = new FileCookieStore(this.filename);

		obj.addCookie(new BasicClientCookie2("n1", "v1"));
		obj.addCookie(new BasicClientCookie2("n2", "v2"));
		obj.saveToFile();
	}

}
