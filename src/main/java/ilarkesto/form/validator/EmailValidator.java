package ilarkesto.form.validator;

import ilarkesto.email.EmailAddress;
import ilarkesto.form.ValidationException;

/**
 * @author <A href="mailto:wi@koczewski.de">Witoslaw Koczewski </A>
 * @version 1 $Revision: 1.5 $ $Author: wko $ $Date: 2006/10/26 17:56:53 $
 */
public class EmailValidator implements Validator {

	public static final EmailValidator THIS = new EmailValidator();

	private static final String MSG = "Die Eingabe muss eine g\u00FCltige Email-Adresse sein. Beispiel: \"wi@koczewski.de\" oder \"Witoslaw Koczewski <wi@koczewski.de>\"";

	@Override
	public String validate(String s) throws ValidationException {
		s = s.trim();
		if (s.length() < 5) throw new ValidationException(MSG);
		int idx = s.indexOf('@');
		if (idx < 1) throw new ValidationException(MSG);

		// if (idx >= s.length() - 3) throw new ValidationException(MSG);
		// int idx2 = s.lastIndexOf('.');
		// if (idx2 < 3) throw new ValidationException(MSG);
		// if (idx2 <= idx) throw new ValidationException(MSG);
		// if (idx2 >= s.length() - 1) throw new ValidationException(MSG);

		EmailAddress.parseList(s);
		return s;
	}

}
