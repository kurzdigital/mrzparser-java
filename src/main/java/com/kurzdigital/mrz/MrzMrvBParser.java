package com.kurzdigital.mrz;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MrzMrvBParser extends AbstractMrzParser {
	private static final Pattern firstLinePattern = Pattern.compile(
			// document code
			"([V]{1}[A-Z0-9<]{1})" +
					// issuing state (ISO 3166-1 alpha-3 code with modifications)
					"([A-Z<]{3})" +
					// last name, then '<<', then first name(s)
					"([A-Z<]{31})");
	private static final Pattern secondLinePattern = Pattern.compile(
			// document number
			"([A-Z0-9<]{9})" +
					// check digit over digits document number
					"([0-9<]{1})" +
					// nationality (ISO 3166-1 alpha-3 code with modifications)
					"([A-Z<]{3})" +
					// date of birth (YYMMDD)
					"([0-9<]{6})" +
					// check digit over digits date of birth
					"([0-9]{1})" +
					// sex (M, F or < for male, female or unspecified)
					"([MFX<]{1})" +
					// expiration date of passport (YYMMDD)
					"([0-9]{6})" +
					// check digit over digits expiration date
					"([0-9]{1})" +
					// optional data by the issuing state
					"([A-Z0-9<]{8})");

	@Override
	public MrzInfo parse(String text) {
		String documentCode;
		String issuingState;
		String names[];
		int endOfFirstLine;

		// first line
		{
			Matcher m = firstLinePattern.matcher(text);
			if (!m.find() || m.groupCount() < 3) {
				throw new IllegalArgumentException(
						MrzMrvBParser.class.getSimpleName() +
						": cannot parse line 1");
			}

			documentCode = m.group(1);
			issuingState = m.group(2);
			names = splitName(m.group(3));

			endOfFirstLine = m.end() - m.start();
		}

		String documentNumber;
		String documentNumberCheckDigit;
		String nationality;
		String dateOfBirth;
		String dateOfBirthCheckDigit;
		String sex;
		String dateOfExpiry;
		String dateOfExpiryCheckDigit;
		String optionalData;

		// second line
		{
			Matcher m = secondLinePattern.matcher(text);
			if (!m.find(endOfFirstLine) || m.groupCount() < 9) {
				throw new IllegalArgumentException(
						MrzMrvBParser.class.getSimpleName() +
						": cannot parse line 2");
			}

			documentNumber = m.group(1);
			documentNumberCheckDigit = m.group(2);
			nationality = m.group(3);
			dateOfBirth = m.group(4);
			dateOfBirthCheckDigit = m.group(5);
			sex = m.group(6);
			dateOfExpiry = m.group(7);
			dateOfExpiryCheckDigit = m.group(8);
			optionalData = m.group(9);
		}

		if (!check(documentNumberCheckDigit, documentNumber) ||
				!check(dateOfBirthCheckDigit, dateOfBirth) ||
				!check(dateOfExpiryCheckDigit, dateOfExpiry)) {
			throw new IllegalArgumentException(
					MrzMrvBParser.class.getSimpleName() +
					": checksum mismatch");
		}

		return new MrzInfo(
				documentCode,
				issuingState,
				names[0],
				names[1],
				nationality,
				documentNumber,
				dateOfBirth,
				sex,
				dateOfExpiry);
	}
}
