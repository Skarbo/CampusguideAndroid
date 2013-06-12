package com.skarbo.campusguide.mapper.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class Util {

	public interface ImplodeCallback<E> {
		public String createString(E object);
	}

	public static String implode(Object[] input, String glue) {
		return implode(input, glue, null);
	}

	public static String implode(Object[] input, String glue, ImplodeCallback callback) {
		String output = "";
		if (input != null && input.length > 0) {
			StringBuilder sb = new StringBuilder();
			if (input[0] != null) {
				if (callback != null)
					sb.append(callback.createString(input[0]));
				else
					sb.append(input[0].toString());
			}
			for (int i = 1; i < input.length; i++) {
				if (input[i] != null) {
					sb.append(glue);
					if (callback != null)
						sb.append(callback.createString(input[i]));
					else
						sb.append(input[i].toString());
				}
			}
			output = sb.toString();
		}
		return output;
	}

	public static List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			// LatLng p = new LatLng((int) (((double) lat / 1E5) * 1E6), (int)
			// (((double) lng / 1E5) * 1E6));
			LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			poly.add(p);
		}

		return poly;
	}

	public static String retrieveContent(File file) throws IOException {
		return retrieveContent(new FileInputStream(file));
	}

	public static String retrieveContent(InputStream inputStream) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder lines = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			lines.append(line);
		}
		return lines.toString();
	}

}
