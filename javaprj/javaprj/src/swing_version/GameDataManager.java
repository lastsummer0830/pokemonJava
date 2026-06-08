package swing_version;

import java.util.Arrays;

// ┌─────────────────────────────────────────────────────────────────────┐
// │ [A+D파트 통합 수정] GameDataManager                                 │
// │                                                                     │
// │ 변경 내용:                                                          │
// │  1) BT_LearnSet에서 사용하는 스킬 전부 등록                        │
// │     (솔라빔, 번개, 눈사태, 오라구, 미래예지, 역린, 악의파동,       │
// │      매지컬샤인 추가)                                               │
// │  2) 포켓몬 초기 스킬 2개 → 해당 포켓몬 속성 우선, 낮은 레벨 기준  │
// │  3) 나머지 2슬롯은 null로 비워둠 (BT_LearnSet 레벨업 시 채움)     │
// └─────────────────────────────────────────────────────────────────────┘
class GameDataManager {
	public static Pokedex createDefaultPokedex() {
		Pokedex pokedex = new Pokedex();

		// ══════════════════════════════════════════════════
		// 스킬 등록
		// ══════════════════════════════════════════════════

		// ── 노말 ──────────────────────────────────────────
		Skill tackle        = new Skill("몸통박치기",   "노말",  40, "없음");
		Skill quickAttack   = new Skill("전광석화",     "노말",  40, "없음");
		Skill scratch       = new Skill("할퀴기",       "노말",  40, "없음");
		Skill tailWhip      = new Skill("꼬리치기",     "노말",   0, "없음"); // 변수용 (이브이 초기)
		Skill vineWhip      = new Skill("덩굴채찍",     "풀",    45, "없음"); // 이상해씨 초기
		Skill bubble        = new Skill("거품",         "물",    40, "없음"); // 꼬부기 초기

		// ── 불 ────────────────────────────────────────────
		Skill ember         = new Skill("불꽃세례",     "불",    60, "화상 30%"); // 브케인 초기
		Skill flamethrower  = new Skill("화염방사",     "불",    90, "화상 10%"); // BT_LearnSet Lv14
		Skill eruption      = new Skill("분화",         "불",   150, "없음");     // BT_LearnSet Lv27

		// ── 물 ────────────────────────────────────────────
		Skill waterGun      = new Skill("물대포",       "물",    40, "없음");     // 별가사리/야돈 초기
		Skill surf          = new Skill("파도타기",     "물",    90, "없음");     // BT_LearnSet Lv13
		Skill hydroPump     = new Skill("하이드로펌프", "물",   110, "없음");     // BT_LearnSet Lv22

		// ── 풀 ────────────────────────────────────────────
		Skill razorLeaf     = new Skill("잎날가르기",   "풀",    55, "없음");     // 치코리타 초기
		Skill sleepPowder   = new Skill("수면가루",     "풀",     0, "수면 100%");// 치코리타 초기
		Skill gigaDrain     = new Skill("기가드레인",   "풀",    90, "흡수");     // BT_LearnSet Lv15
		Skill solarBeam     = new Skill("솔라빔",       "풀",   120, "없음");     // BT_LearnSet Lv26

		// ── 전기 ──────────────────────────────────────────
		Skill thunderShock  = new Skill("전기쇼크",     "전기",  40, "마비 10%"); // 피카츄 초기
		Skill thunderbolt   = new Skill("10만볼트",     "전기",  90, "마비 10%"); // BT_LearnSet Lv18
		Skill thunder       = new Skill("번개",         "전기", 110, "마비 30%"); // BT_LearnSet Lv26
		Skill thunderPunch  = new Skill("번개펀치",     "전기",  75, "마비 10%"); // 기술머신계열, Pokedex 보관용
		Skill discharge     = new Skill("방전",         "전기",  80, "마비 10%"); // 코일 BT_LearnSet Lv17
		Skill thunderWave   = new Skill("전기자석파",   "전기",   0, "마비 100%");// 코일 초기

		// ── 얼음 ──────────────────────────────────────────
		Skill icyWind       = new Skill("얼어붙은 바람","얼음",  55, "동상 30%"); // 포푸니 초기
		Skill icePunch      = new Skill("냉동펀치",     "얼음",  75, "동상 10%"); // BT_LearnSet Lv20
		Skill avalanche     = new Skill("눈사태",       "얼음",  60, "없음");     // BT_LearnSet Lv30

		// ── 격투 ──────────────────────────────────────────
		Skill forcePalm     = new Skill("발경",         "격투",  60, "마비 30%"); // 리오르 초기
		Skill closeCombat   = new Skill("인파이트",     "격투", 120, "없음");     // BT_LearnSet Lv18
		Skill auraSphere    = new Skill("파동탄",       "격투",  80, "없음");     // BT_LearnSet Lv28

		// ── 독 ────────────────────────────────────────────
		Skill sludgeBomb    = new Skill("오물폭탄",     "독",    90, "독 30%");   // 또가스 초기
		Skill toxic         = new Skill("맹독",         "독",     0, "독 100%");  // BT_LearnSet Lv16

		// ── 땅 ────────────────────────────────────────────
		Skill bulldoze      = new Skill("땅고르기",     "땅",    80, "없음");     // 딥상어동 초기
		Skill earthquake    = new Skill("지진",         "땅",   100, "없음");     // BT_LearnSet Lv22 (꼬마돌/딥상어동)

		// ── 비행 ──────────────────────────────────────────
		Skill peck          = new Skill("쪼기",         "비행",  35, "없음");     // 구구 초기
		Skill airSlash      = new Skill("에어슬래시",   "비행",  75, "없음");     // BT_LearnSet Lv15
		Skill hurricane     = new Skill("폭풍",         "비행", 110, "없음");     // BT_LearnSet Lv24

		// ── 에스퍼 ────────────────────────────────────────
		Skill confusion     = new Skill("염동력",       "에스퍼",50, "혼란 30%"); // 야돈 초기
		Skill psychic       = new Skill("사이코키네시스","에스퍼",90, "없음");    // BT_LearnSet Lv22
		Skill futureS       = new Skill("미래예지",     "에스퍼",120,"없음");     // BT_LearnSet Lv33

		// ── 벌레 ──────────────────────────────────────────
		Skill xScissor      = new Skill("시저크로스",   "벌레",  80, "없음");     // BT_LearnSet Lv22
		Skill bugBite       = new Skill("벌레먹기",     "벌레",  60, "없음");     // 스라크 초기

		// ── 바위 ──────────────────────────────────────────
		Skill rockSlide     = new Skill("스톤샤워",     "바위",  75, "없음");     // 꼬마돌 초기 / BT_LearnSet Lv18
		Skill rockThrow     = new Skill("돌던지기",     "바위",  50, "없음");     // 꼬마돌 초기

		// ── 고스트 ────────────────────────────────────────
		Skill lick          = new Skill("혀로핥기",     "고스트",30, "마비 30%"); // 고오스 초기
		Skill shadowBall    = new Skill("섀도볼",       "고스트",80, "없음");     // BT_LearnSet Lv17
		Skill shadowDive    = new Skill("섀도다이브",   "고스트",120,"없음");     // BT_LearnSet Lv27

		// ── 드래곤 ────────────────────────────────────────
		Skill dragonBreath  = new Skill("용의숨결",     "드래곤",60, "마비 30%"); // 미뇽 초기
		Skill twister       = new Skill("회오리",       "드래곤",40, "혼란 30%"); // 미뇽 초기
		Skill dragonClaw    = new Skill("드래곤클로",   "드래곤",80, "없음");     // BT_LearnSet Lv20
		Skill dragonDive    = new Skill("드래곤다이브", "드래곤",100,"없음");     // BT_LearnSet Lv35 (미뇽 역린대신)
		Skill dragonPulse   = new Skill("용의파동",     "드래곤",85, "혼란 20%"); // 기라티나
		Skill outrage       = new Skill("역린",         "드래곤",120,"없음");     // BT_LearnSet Lv35 (미뇽)

		// ── 강철 ──────────────────────────────────────────
		Skill magnetBomb    = new Skill("자석폭탄",     "강철",  60, "없음");     // 코일 초기

		// ── 악 ────────────────────────────────────────────
		Skill bite          = new Skill("물기",         "악",    60, "혼란 20%"); // 딥상어동 초기 / 나옹 초기
		Skill nightSlash    = new Skill("깜짝베기",     "악",    70, "없음");     // BT_LearnSet Lv18
		Skill darkPulse     = new Skill("악의파동",     "악",    80, "혼란 20%"); // BT_LearnSet Lv29

		// ── 페어리 ────────────────────────────────────────
		Skill charm         = new Skill("애교부리기",   "페어리",  0, "혼란 100%");// 삐삐 초기
		Skill moonblast     = new Skill("문포스",       "페어리", 95, "없음");    // BT_LearnSet Lv15
		Skill dazzling      = new Skill("매지컬샤인",   "페어리", 80, "없음");    // BT_LearnSet Lv26

		// ── 모든 스킬 Pokedex 등록 ────────────────────────
		Skill[] allSkills = {
			// 노말
			tackle, quickAttack, scratch, tailWhip,
			// 불
			ember, flamethrower, eruption,
			// 물
			waterGun, surf, hydroPump,
			// 풀
			razorLeaf, sleepPowder, gigaDrain, solarBeam,
			// 전기
			thunderShock, thunderbolt, thunder, thunderPunch, discharge, thunderWave,
			// 얼음
			icyWind, icePunch, avalanche,
			// 격투
			forcePalm, closeCombat, auraSphere,
			// 독
			sludgeBomb, toxic,
			// 땅
			bulldoze, earthquake,
			// 비행
			peck, airSlash, hurricane,
			// 에스퍼
			confusion, psychic, futureS,
			// 벌레
			xScissor, bugBite,
			// 바위
			rockSlide, rockThrow,
			// 고스트
			lick, shadowBall, shadowDive,
			// 드래곤
			dragonBreath, twister, dragonClaw, dragonDive, dragonPulse, outrage,
			// 강철
			magnetBomb,
			// 악
			bite, nightSlash, darkPulse,
			// 페어리
			charm, moonblast, dazzling
		};
		for (Skill s : allSkills) pokedex.addSkill(s);

		// ══════════════════════════════════════════════════
		// 포켓몬 등록
		// 초기 스킬 2개만 설정. 나머지 2슬롯은 null (레벨업 시 BT_LearnSet에서 채움)
		// Pokemon(이름, 레벨, HP, ATK, 타입, 등급, 스킬리스트)
		// ══════════════════════════════════════════════════

		// 이브이 (노말) - 전광석화(노말), 할퀴기(노말)
		pokedex.addPokemon(new Pokemon("이브이",    12,  95, 35, "노말",
				"Wild", Arrays.asList(quickAttack, scratch, null, null)));

		// 브케인 (불) - 불꽃세례(불), 몸통박치기(노말)
		// Lv14 화염방사, Lv27 분화
		pokedex.addPokemon(new Pokemon("브케인",    12, 100, 40, "불",
				"Wild", Arrays.asList(ember, tackle, null, null)));

		// 별가사리 (물) - 물대포(물), 파도타기는 Lv13 레벨업
		// 실제론 파도타기가 초기에 있지만 초반 밸런스상 물대포로 시작
		pokedex.addPokemon(new Pokemon("별가사리",  12, 100, 38, "물",
				"Wild", Arrays.asList(waterGun, tackle, null, null)));

		// 치코리타 (풀) - 잎날가르기(풀), 수면가루(풀)
		// Lv15 기가드레인, Lv26 솔라빔
		pokedex.addPokemon(new Pokemon("치코리타",  12, 105, 34, "풀",
				"Wild", Arrays.asList(razorLeaf, sleepPowder, null, null)));

		// 피카츄 (전기) - 전기쇼크(전기), 전광석화(노말)
		// 공식 기준: 번개펀치는 기술머신이라 레벨업 목록 아님
		// Lv18 10만볼트, Lv26 번개
		pokedex.addPokemon(new Pokemon("피카츄",    12,  90, 42, "전기",
				"Wild", Arrays.asList(thunderShock, quickAttack, null, null)));

		// 포푸니 (얼음/악) - 얼어붙은 바람(얼음), 물기(악)
		// Lv20 냉동펀치, Lv30 눈사태
		pokedex.addPokemon(new Pokemon("포푸니",    13,  95, 45, "얼음/악",
				"Wild", Arrays.asList(icyWind, bite, null, null)));

		// 리오르 (격투) - 발경(격투), 전광석화(노말)
		// Lv18 인파이트, Lv28 오라구
		pokedex.addPokemon(new Pokemon("리오르",    13, 100, 46, "격투",
				"Wild", Arrays.asList(forcePalm, quickAttack, null, null)));

		// 또가스 (독) - 오물폭탄(독), 몸통박치기(노말)
		// Lv16 맹독, Lv27 오물폭탄(이미 초기에 있으니 다른 걸로 대체 필요 → BT_LearnSet 주의)
		pokedex.addPokemon(new Pokemon("또가스",    12, 110, 36, "독",
				"Wild", Arrays.asList(sludgeBomb, tackle, null, null)));

		// 딥상어동 (땅/드래곤) - 물기(악), 용의숨결(드래곤) → 땅속성은 레벨업으로
		// Lv22 지진, Lv32 드래곤다이브
		pokedex.addPokemon(new Pokemon("딥상어동",  14, 120, 50, "땅/드래곤",
				"Wild", Arrays.asList(bite, dragonBreath, null, null)));

		// 구구 (비행/노말) - 쪼기(비행), 전광석화(노말)
		// Lv15 에어슬래시, Lv24 폭풍
		pokedex.addPokemon(new Pokemon("구구",      10,  85, 28, "비행/노말",
				"Wild", Arrays.asList(peck, quickAttack, null, null)));

		// 야돈 (에스퍼/물) - 염동력(에스퍼), 물대포(물)
		// Lv22 사이코키네시스, Lv33 미래예지
		pokedex.addPokemon(new Pokemon("야돈",      13, 130, 32, "에스퍼/물",
				"Wild", Arrays.asList(confusion, waterGun, null, null)));

		// 스라크 (벌레/비행) - 벌레먹기(벌레), 에어슬래시(비행)
		// Lv22 시저크로스, Lv32 에어슬래시(이미 초기) → BT_LearnSet에서 폭풍으로 교체 고려
		pokedex.addPokemon(new Pokemon("스라크",    14, 110, 48, "벌레/비행",
				"Wild", Arrays.asList(bugBite, airSlash, null, null)));

		// 꼬마돌 (바위/땅) - 돌던지기(바위), 땅고르기(땅)
		// Lv18 스톤샤워, Lv28 지진
		pokedex.addPokemon(new Pokemon("꼬마돌",    12, 115, 44, "바위/땅",
				"Wild", Arrays.asList(rockThrow, bulldoze, null, null)));

		// 고오스 (고스트/독) - 혀로핥기(고스트), 오물폭탄(독)
		// Lv17 섀도볼, Lv27 섀도다이브
		pokedex.addPokemon(new Pokemon("고오스",    12,  90, 43, "고스트/독",
				"Wild", Arrays.asList(lick, sludgeBomb, null, null)));

		// 미뇽 (드래곤) - 용의숨결(드래곤), 회오리(드래곤)
		// Lv20 드래곤클로, Lv35 역린
		pokedex.addPokemon(new Pokemon("미뇽",      14, 115, 47, "드래곤",
				"Wild", Arrays.asList(dragonBreath, twister, null, null)));

		// 코일 (강철/전기) - 자석폭탄(강철), 전기자석파(전기)
		// Lv17 방전, Lv28 10만볼트
		pokedex.addPokemon(new Pokemon("코일",      12,  95, 39, "강철/전기",
				"Wild", Arrays.asList(magnetBomb, thunderWave, null, null)));

		// 나옹 (악) - 물기(악), 할퀴기(노말)
		// Lv18 깜짝베기, Lv29 악의파동
		pokedex.addPokemon(new Pokemon("나옹",      11,  88, 37, "악",
				"Wild", Arrays.asList(bite, scratch, null, null)));

		// 삐삐 (페어리) - 애교부리기(페어리), 몸통박치기(노말)
		// Lv15 문포스, Lv26 매지컬샤인
		pokedex.addPokemon(new Pokemon("삐삐",      12, 100, 33, "페어리",
				"Wild", Arrays.asList(charm, tackle, null, null)));

		// 기라티나 (고스트/드래곤) - 전설 포켓몬, 처음부터 4슬롯 풀
		pokedex.addPokemon(new Pokemon("기라티나",  30, 700, 135, "고스트/드래곤",
				"Legendary", Arrays.asList(shadowDive, dragonPulse, shadowBall, dragonClaw)));

		// ─── 스타팅 포켓몬 3마리 (Starter) ───────────────────────────────────
		// ATK를 야생 포켓몬 수준으로 상향, HP도 증가 (초반 전투 생존 가능하도록)
		pokedex.addPokemon(new Pokemon("이상해씨",   5, 120, 48, "풀", "Starter", Arrays.asList(vineWhip, razorLeaf)));
		pokedex.addPokemon(new Pokemon("꼬부기",     5, 125, 46, "물", "Starter", Arrays.asList(waterGun, bubble)));
		pokedex.addPokemon(new Pokemon("파이리",     5, 110, 50, "불", "Starter", Arrays.asList(ember, scratch)));

		return pokedex;
	}
}
