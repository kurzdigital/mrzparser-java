package com.kurzdigital.mrz;

import java.util.regex.Pattern;

public class MrzParser {
	private static final Pattern invalidCharacters = Pattern.compile(
			"[^A-Z0-9<]");

	public static String purify(String text) {
		return invalidCharacters.matcher(text).replaceAll("");
	}

	public static MrzInfo parse(String text) {
		if (text == null) {
			throw new IllegalArgumentException("text cannot be null");
		}
		switch (text.length()) {
			case 90:
				return MrzTd1Parser.parse(text);
			case 72:
				if (text.startsWith("IDFRA")) {
					return MrzFranceParser.parse(text);
				}
				return text.startsWith("V") ?
						MrzMrvBParser.parse(text) :
						MrzTd2Parser.parse(text);
			case 88:
				return text.startsWith("V") ?
						MrzMrvAParser.parse(text) :
						MrzTd3Parser.parse(text);
			default:
				throw new IllegalArgumentException("invalid length");
		}
	}
}
