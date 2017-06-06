/**
 * 
 */
package com.garyz.util;

import org.apache.commons.lang3.StringUtils;

import com.garyz.constant.HttpConstants;

/**
 * @author zengzhiqiang
 * @version 2017年6月6日
 *
 */
public class HttpUtils {

	public static String getContentType(String fileName) {
		String result = "application/octet-stream";

		if (StringUtils.isEmpty(fileName)) {
			return result;
		}

		for (String[] contentType : HttpConstants.CONTENT_TYPE) {
			if (fileName.endsWith(contentType[0])) {
				result = contentType[1];
			}
		}

		return result;
	}
}
