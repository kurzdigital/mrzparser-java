package com.kurzdigital.mrz;

import java.util.regex.Pattern;
import java.util.HashMap;

public class MrzParser {
	private static final Pattern invalidCharacters = Pattern.compile(
			"[^A-Z0-9<]");
	private static final HashMap<Class, AbstractMrzParser> parsers =
			new HashMap<>();

	public static String purify(String text) {
		return invalidCharacters.matcher(text).replaceAll("");
	}

	public static MrzInfo parse(String text) {
		if (text == null) {
			throw new IllegalArgumentException("text cannot be null");
		}
		switch (text.length()) {
			case 90:
				return getInstance(MrzTd1Parser.class).parse(text);
			case 72:
				if (text.startsWith("IDFRA")) {
					return getInstance(MrzFranceParser.class).parse(text);
				}
				return text.startsWith("V") ?
						getInstance(MrzMrvBParser.class).parse(text) :
						getInstance(MrzTd2Parser.class).parse(text);
			case 88:
				return text.startsWith("V") ?
						getInstance(MrzMrvAParser.class).parse(text) :
						getInstance(MrzTd3Parser.class).parse(text);
			default:
				throw new IllegalArgumentException("invalid length");
		}
	}

	private static AbstractMrzParser getInstance(Class cls) {
		AbstractMrzParser parser = parsers.get(cls);
		if (parser == null) {
			try {
				parser = (AbstractMrzParser)
						cls.getConstructor().newInstance();
			} catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage());
			}
			parsers.put(cls, parser);
		}
		return parser;
	}
}
