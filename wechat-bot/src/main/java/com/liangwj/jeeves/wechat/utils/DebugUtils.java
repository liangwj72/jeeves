package com.liangwj.jeeves.wechat.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.liangwj.tools2k.utils.other.LogUtil;

/**
 * 常用工具类
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月8日 下午10:59:55
 * @version 1.0
 *
 */
public class DebugUtils {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DebugUtils.OsNameEnum.class);

	public enum OsNameEnum {
		WINDOWS, LINUX, DARWIN, MAC, OTHER
	}

	/**
	 * 获取系统平台
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月8日 下午10:27:53
	 */
	public static OsNameEnum getOsNameEnum() {
		String os = System.getProperty("os.name").toUpperCase();
		if (os.indexOf(OsNameEnum.DARWIN.toString()) >= 0) {
			return OsNameEnum.DARWIN;
		} else if (os.indexOf(OsNameEnum.WINDOWS.toString()) >= 0) {
			return OsNameEnum.WINDOWS;
		} else if (os.indexOf(OsNameEnum.LINUX.toString()) >= 0) {
			return OsNameEnum.LINUX;
		} else if (os.indexOf(OsNameEnum.MAC.toString()) >= 0) {
			return OsNameEnum.MAC;
		}
		return OsNameEnum.OTHER;
	}

	/**
	 * 将二维码保存为一个临时文件
	 */
	public static void saveQrCodeToFile(byte[] bytes) {

		try {
			File file = File.createTempFile("wechat-qr", ".jpg");

			OutputStream out = new FileOutputStream(file);
			out.write(bytes);
			out.flush();
			out.close();
			String qrPath = file.getAbsolutePath();

			log.debug("二维码文件保存在 {}", qrPath);

			// 打开登陆二维码图片
			OsNameEnum os = getOsNameEnum();
			Runtime runtime = Runtime.getRuntime();
			switch (os) {
			case WINDOWS:
				try {
					runtime.exec("cmd /c start " + qrPath);
				} catch (Exception e) {
				}
				break;
			case MAC:
				try {
					runtime.exec("open " + qrPath);
				} catch (Exception e) {
				}
				break;

			default:
				break;
			}

		} catch (Exception e) {
			LogUtil.traceError(log, e);
		}
	}

}
