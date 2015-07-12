package com.chen.library.Utils;

public class ImageUtils {
	public static String GetMediumPictureUrl(String smallPicUrl) {
		char[] tmp = smallPicUrl.toCharArray();
		for (int i = 6; i < tmp.length; i++) {
			if (tmp[i] == 's') {
				tmp[i] = 'm';
				break;

			}
		}
		return new String(tmp, 0, tmp.length);

	}

	public static String GetLargePictureUrl(String smallPicUrl) {
		char[] tmp = smallPicUrl.toCharArray();
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i] == 's') {
				tmp[i] = 'l';
				break;

			}
		}
		return new String(tmp, 0, tmp.length);

	}
}
