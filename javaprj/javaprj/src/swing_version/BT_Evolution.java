package swing_version;

import java.util.HashMap;
import java.util.Map;

/**
 * [D파트 신규] 레벨업 진화 데이터 관리
 * - 진화 조건: 레벨업만
 * - BattleEngine에서 레벨업 후 canEvolve() 호출
 */
public class BT_Evolution {

	// key: 진화 전 포켓몬 이름
	private static final Map<String, Integer> EVO_LEVEL  = new HashMap<>();
	private static final Map<String, String>  EVO_TARGET = new HashMap<>();

	static {
		// register(진화 전 이름, 진화 레벨, 진화 후 이름)
		// 스타터(Lv5 시작): 1차 Lv10(~17배틀), 2차 Lv20(~55배틀 진화체 기준)
		register("파이리",    10, "리자드");
		register("리자드",    20, "리자몽");
		register("이상해씨",  10, "이상해풀");
		register("이상해풀",  20, "이상해꽃");
		register("꼬부기",    10, "어니부기");
		register("어니부기",  20, "거북왕");

		// 야생 포켓몬 단일 진화 (초기 레벨+6, 배틀당 2레벨 기준 3배틀)
		register("이브이",    18, "부스터");
		register("피카츄",    18, "라이츄");
		register("리오르",    19, "루카리오");
		register("나옹",      17, "페르시온");
		register("코일",      18, "레어코일");
		register("별가사리",  18, "아쿠스타");
		register("포푸니",    19, "눈설왕");
		register("또가스",    18, "또도가스");
		register("야돈",      19, "야도란");
		register("딥상어동",  20, "상어왕");

		// 야생 포켓몬 2단계 진화 (1단계 +6, 최종 +10 → 각각 3배틀, 5배틀)
		register("브케인",    18, "마그케인");
		register("마그케인",  28, "블레이범");
		register("치코리타",  18, "베이리프");
		register("베이리프",  28, "메가니움");
		register("구구",      16, "피죤");
		register("피죤",      26, "피죤투");
		register("꼬마돌",    18, "데구리");
		register("데구리",    28, "딱구리");
		register("고오스",    18, "고우스트");
		register("고우스트",  28, "팬텀");
		register("미뇽",      20, "신뇽");
		register("신뇽",      30, "망나뇽");
		// 스라크, 삐삐, 기라티나 → 진화 없음 (등록하지 않음)
	}

	private static void register(String name, int level, String target) {
		EVO_LEVEL.put(name, level);
		EVO_TARGET.put(name, target);
	}

	/**
	 * 진화 가능 여부 확인
	 * @param pokemonName 포켓몬 이름
	 * @param currentLevel 현재 레벨
	 * @return true면 진화 가능
	 */
	public static boolean canEvolve(String pokemonName, int currentLevel) {
		if (!EVO_LEVEL.containsKey(pokemonName)) return false;
		return currentLevel >= EVO_LEVEL.get(pokemonName);
	}

	/**
	 * 진화 후 포켓몬 이름 반환
	 * @param pokemonName 진화 전 이름
	 * @return 진화 후 이름 (없으면 빈 문자열)
	 */
	public static String getEvolvedName(String pokemonName) {
		return EVO_TARGET.getOrDefault(pokemonName, "");
	}

	/**
	 * 진화 레벨 반환 (도감 표시용)
	 * @param pokemonName 포켓몬 이름
	 * @return 진화 레벨, 진화 없으면 -1
	 */
	public static int getEvoLevel(String pokemonName) {
		return EVO_LEVEL.getOrDefault(pokemonName, -1);
	}
}
