package com.liangwj.tools2k.utils.net;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.liangwj.tools2k.utils.other.FileUtil;

/**
 * 可保存到文件的 CookieStore
 * 
 * <pre>
 * 方便记录cookie内容的httpclient，程序重启后可从文件中读取cookie，继续上次的访问
 * </pre>
 * 
 *
 */
public class FileCookieStore extends BasicCookieStore {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileCookieStore.class);

	private static final long serialVersionUID = -7210937146421718408L;

	private final String filename;

	public FileCookieStore(String filename) throws IOException {
		super();

		Assert.notNull(filename, "filename must not be null");

		this.filename = filename;

		log.debug("创建 FileCookieStore，文件路径为:{}", this.filename);

		this.loadFromFile();

	}

	/**
	 * 从文件夹中加载
	 * 
	 * @throws IOException
	 */
	private void loadFromFile() throws IOException {
		File file = new File(this.filename);
		if (file.exists()) {
			// 如果文件存在
			String jsonStr = FileUtil.readFile(file);
			if (StringUtils.hasText(jsonStr)) {
				// 并且文件有内容
				List<CookieBean> list = JSON.parseArray(jsonStr, CookieBean.class);
				list.forEach(bean -> {
					this.addCookie(bean);
				});

				log.debug("FileCookieStore初始化: 从 {} 读取了 {} 个 cookie", this.filename, this.getCookies().size());
			}
		} else {
			log.debug("FileCookieStore初始化: 文件{} 不存在，无需初始化", this.filename);
		}
	}

	/**
	 * 将所有cookie保存到文件
	 * 
	 * @throws IOException
	 */
	public void saveToFile() throws IOException {

		// 获取原来的cookie
		List<Cookie> srcList = this.getCookies();

		if (srcList.size() > 0) {

			// 做一个格式转换
			List<CookieBean> list = new ArrayList<>(srcList.size());
			srcList.forEach(src -> {
				list.add(new CookieBean(src));
			});

			// 生成json字符串
			String str = JSON.toJSONString(list, JSONWriter.Feature.PrettyFormat);
			// 写到文件
			FileUtil.write(filename, str, true);

			log.debug("FileCookieStore保存: 共将 {} 个 cookie 被保存在文件 {} 中", list.size(), this.filename);
		} else {
			log.debug("没有cookie需要保存到文件中");
		}
	}

}
