package pl.jakubeeee.integration.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class StringUtils {

    private static Map<String, String> charMap = new HashMap<>();

    static {
        charMap.put("Ą", "\\u0104");
        charMap.put("ą", "\\u0105");
        charMap.put("Ć", "\\u0106");
        charMap.put("ć", "\\u0107");
        charMap.put("Ę", "\\u0118");
        charMap.put("ę", "\\u0119");
        charMap.put("Ł", "\\u0141");
        charMap.put("ł", "\\u0142");
        charMap.put("Ń", "\\u0143");
        charMap.put("ń", "\\u0144");
        charMap.put("Ó", "\\u00D3");
        charMap.put("ó", "\\u00F3");
        charMap.put("Ś", "\\u015A");
        charMap.put("ś", "\\u015B");
        charMap.put("Ź", "\\u0179");
        charMap.put("ź", "\\u017A");
        charMap.put("Ż", "\\u017B");
        charMap.put("ż", "\\u017C");
        charMap.put("–", "\\u2013");
    }

    public static String replacePolishChars(String string) {
        for (val entry : charMap.entrySet()) {
            string = string.replaceAll(entry.getKey(), entry.getValue());
        }
        return string;
    }

}
