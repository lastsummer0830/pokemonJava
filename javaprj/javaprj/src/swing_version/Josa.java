package swing_version;

/**
 * 한글 조사 처리.
 *
 * 조사는 앞 글자의 받침(종성) 유무로 갈린다. 한글 음절은 유니코드에서 '가'(0xAC00)부터
 * (초성 19 × 중성 21 × 종성 28) 순서로 규칙적으로 배열돼 있어서,
 * (글자 - 0xAC00) % 28 로 종성 번호가 나온다. 0이면 받침 없음.
 *
 * 예) 파이리 → '리'는 종성 0 → 받침 없음 → "파이리는"
 *     코일   → '일'은 종성 8(ㄹ)  → 받침 있음 → "코일은"
 *
 * 포켓몬 이름은 데이터에서 오기 때문에 문장마다 "은(는)"으로 얼버무리면
 * "파이리은(는) 쓰러졌다!"처럼 나온다. 그래서 이름 + 조사를 여기서 만들어 붙인다.
 */
public class Josa {

    private Josa() { }

    /** 은 / 는 */
    public static String eun(String word) {
        return word + (hasBatchim(word) ? "은" : "는");
    }

    /** 이 / 가 */
    public static String i(String word) {
        return word + (hasBatchim(word) ? "이" : "가");
    }

    /** 을 / 를 */
    public static String eul(String word) {
        return word + (hasBatchim(word) ? "을" : "를");
    }

    /** 와 / 과 */
    public static String wa(String word) {
        return word + (hasBatchim(word) ? "과" : "와");
    }

    /** 로 / 으로 — 받침이 없거나 ㄹ 받침이면 "로" (예: 코일로, 파이리로) */
    public static String ro(String word) {
        int jong = jongseong(word);
        return word + (jong == 0 || jong == 8 ? "로" : "으로");
    }

    /** 받침이 있으면 true */
    public static boolean hasBatchim(String word) {
        return jongseong(word) != 0;
    }

    /**
     * 마지막 글자의 종성 번호 (0 = 받침 없음).
     * 한글 음절이 아니면(영문·숫자 등) 0으로 본다.
     */
    private static int jongseong(String word) {
        if (word == null || word.isEmpty()) return 0;
        char last = word.charAt(word.length() - 1);
        if (last < 0xAC00 || last > 0xD7A3) return 0;
        return (last - 0xAC00) % 28;
    }
}
