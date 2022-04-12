# Java MRZ Parser

Parse a string containing a [Machine Readable Zone][mrz] (Type 1, 2 and 3).

## How to use

Invoke `MrzParser.parse()` with some `String`:

	MrzInfo mrzInfo = MrzParser.parse("I<UTOERIKSSON<<ANNA<MARIA<<<<…");

The string may only consist of the permitted characters for an [MRZ][mrz]
(A-Z, 0-9 and the filler character `<`).

`MrzParser.parse()` throws an `IllegalArgumentException` if the given
`String` could not be parsed.

Otherwise it always returns a `MrzInfo` object containing these members:

	public class MrzInfo {
		public final String documentCode;
		public final String issuingState;
		public final String primaryIdentifier;
		public final String secondaryIdentifier;
		public final String nationality;
		public final String documentNumber;
		public final String dateOfBirth;
		public final String sex;
		public final String dateOfExpiry;
		…
	}

### Filter impure strings

If your input contains white space or line breaks, you can use
`MrzParser.purify()` to filter a string of (any) invalid characters
before giving it to `MrzParser.parse()`:

	String pure = MrzParser.purify("I  <\nUTO"); // => "I<UTO"

[mrz]: https://en.wikipedia.org/wiki/Machine-readable_passport
