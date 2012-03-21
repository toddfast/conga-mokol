package com.conga.tools.mokol.util;

import java.text.BreakIterator;
import java.util.Locale;

/**
 *
 * 
 * @author Todd Fast
 */
public class StringUtil {

	/**
	 * Can't instantiate
	 *
	 */
    private StringUtil() {
		super();
    }


    /**
	 *
	 * 
     */
    public static String[] wrap(String text, int width) {
		String wrappedText=wordWrap(text,width,Locale.getDefault());
		return wrappedText.split("\n");
	}


    /**
     * Reformats a string where lines that are longer than <tt>width</tt>
     * are split apart at the earliest wordbreak or at maxLength, whichever is
     * sooner. If the width specified is less than 5 or greater than the input
     * Strings length the string will be returned as is.
     * <p/>
     * Please note that this method can be lossy - trailing spaces on wrapped
     * lines may be trimmed.
     *
     * @param text the String to reformat.
     * @param width the maximum length of any one line.
     * @return a new String with reformatted as needed.
     */
    private static String wordWrap(String text, int width, Locale locale) {
        // protect ourselves
        if (text == null) {
            return "";
        }
        else if (width < 5) {
            return text;
        }
        else if (width >= text.length()) {
            return text;
        }

        StringBuilder input = new StringBuilder(text);
        boolean endOfLine = false;
        int lineStart = 0;

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '\n') {
                lineStart = i + 1;
                endOfLine = true;
            }

            // handle splitting at width character
            if (i > lineStart + width - 1) {
                if (!endOfLine) {
                    int limit = i - lineStart - 1;
                    BreakIterator breaks =
						BreakIterator.getLineInstance(locale);
                    breaks.setText(input.substring(lineStart, i));
                    int end = breaks.last();

                    // if the last character in the search string isn't a space,
                    // we can't split on it (looks bad). Search for a previous
                    // break character
                    if (end == limit + 1) {
                        if (!Character.isWhitespace(
								input.charAt(lineStart+end))) {
                            end = breaks.preceding(end - 1);
                        }
                    }

                    // if the last character is a space, replace it with a \n
                    if (end != BreakIterator.DONE && end == limit + 1) {
                        input.replace(lineStart + end,
							lineStart + end + 1,"\n");
                        lineStart = lineStart + end;
                    }
                    // otherwise, just insert a \n
                    else if (end != BreakIterator.DONE && end != 0) {
                        input.insert(lineStart + end, '\n');
                        lineStart = lineStart + end + 1;
                    }
                    else {
                        input.insert(i, '\n');
                        lineStart = i + 1;
                    }
                }
                else {
                    input.insert(i, '\n');
                    lineStart = i + 1;
                    endOfLine = false;
                }
            }
        }

        return input.toString();
    }
}
