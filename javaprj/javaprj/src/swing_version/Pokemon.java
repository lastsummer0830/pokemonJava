package swing_version;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Pokemon implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private int level;
	private int maxHp;
	private int currentHp;
	private int attack;
	private String type;
	private String rarity;
	private List<Skill> skills;          // 최대 4슬롯
	private StatusEffect statusEffect = new StatusEffect();

	// ─── 추가: 개체값 (IV) ───────────────────────────────────
	// ivHp: 0~31, ivAtk: 0~31 → 레벨업 시 스탯에 소량 반영
	private int ivHp;
	private int ivAtk;

	// ─── EXP 시스템 ──────────────────────────────────────────
	// exp: 누적 배틀 승리 횟수, expNeeded: 레벨업에 필요한 횟수
	// evolutionStage: 0=기본형, 1=1차 진화, 2=2차 진화
	private int exp = 0;
	private int expNeeded;
	private int evolutionStage = 0;

	public Pokemon(String name, int level, int maxHp, int attack,
	               String type, String rarity, List<Skill> skills) {
		this.name   = name;
		this.level  = level;
		this.maxHp  = maxHp;
		this.currentHp = maxHp;
		this.attack = attack;
		this.type   = type;
		this.rarity = rarity;
		this.skills = new ArrayList<>(skills); // 초기 스킬 최대 4개

		// 개체값 랜덤 생성 (0~31)
		this.ivHp  = (int)(Math.random() * 32);
		this.ivAtk = (int)(Math.random() * 32);
		// 기본형: 3~4 배틀마다 레벨업
		this.expNeeded = 3 + (int)(Math.random() * 2);
	}

	// copy() 시 개체값·EXP도 함께 복사
	public Pokemon copy() {
		Pokemon copied = new Pokemon(name, level, maxHp, attack, type, rarity, skills);
		copied.currentHp    = this.currentHp;
		copied.ivHp         = this.ivHp;
		copied.ivAtk        = this.ivAtk;
		copied.exp          = this.exp;
		copied.expNeeded    = this.expNeeded;
		copied.evolutionStage = this.evolutionStage;
		return copied;
	}

	// ─── 추가: 레벨업 ────────────────────────────────────────
	// 레벨 1 증가 + 개체값 반영 스탯 상승 + HP 전회복
	public void levelUp() {
		this.level++;
		// 기본 상승 + 개체값에 따라 소량 추가 (ivHp/16 → 최대 +2, ivAtk/16 → 최대 +2)
		this.maxHp  += 2 + ivHp / 16;
		this.attack += 1 + ivAtk / 16;
		this.currentHp = this.maxHp;
	}

	// ─── 추가: 진화 ──────────────────────────────────────────
	// 이름 변경 + 스탯 10% 상승 + 진화 단계 증가 → 다음 레벨업 난이도 상승
	public void evolve(String newName) {
		this.name   = newName;
		this.maxHp  = (int)(this.maxHp  * 1.1);
		this.attack = (int)(this.attack * 1.1);
		this.currentHp = this.maxHp;
		this.evolutionStage++;
		this.expNeeded = 5 + (int)(Math.random() * 2); // 진화체: 5~6 배틀마다 레벨업
	}

	// ─── EXP 획득 (배틀 승리 시 BattleEngine에서 호출) ──────
	// expNeeded 도달 시 레벨업 → true 반환, 아니면 false
	public boolean addExp() {
		exp++;
		if (exp >= expNeeded) {
			exp = 0;
			expNeeded = (evolutionStage >= 1)
				? 5 + (int)(Math.random() * 2)  // 진화체: 5~6 배틀
				: 3 + (int)(Math.random() * 2); // 기본형: 3~4 배틀
			levelUp();
			return true;
		}
		return false;
	}

	// ─── 추가: 스킬 슬롯 관리 ────────────────────────────────
	/** null 슬롯(빈 자리)이 있으면 true */
	public boolean canLearnSkill() {
		for (Skill s : skills) {
			if (s == null) return true;
		}
		return skills.size() < 4;
	}

	/** null 슬롯에 기술 추가, 없으면 맨 뒤에 추가 (최대 4개) */
	public void addSkill(Skill skill) {
		for (int i = 0; i < skills.size(); i++) {
			if (skills.get(i) == null) {
				skills.set(i, skill);
				return;
			}
		}
		if (skills.size() < 4) {
			skills.add(skill);
		}
	}

	/** index번째 슬롯의 기술을 newSkill로 교체 */
	public void replaceSkill(int index, Skill newSkill) {
		if (index >= 0 && index < skills.size()) {
			skills.set(index, newSkill);
		}
	}

	// ─── 기존 메서드 (변경 없음) ──────────────────────────────
	public void healFull() {
		currentHp = maxHp;
		statusEffect.clear();
	}

	public void takeDamage(int dmg) {
		currentHp = Math.max(0, currentHp - dmg);
	}

	public boolean isFainted() {
		return currentHp <= 0;
	}

	// 타입이 "얼음/악" 형태로 저장되어 있어서 "/" 기준으로 분리
	public String getType1() {
		return type.contains("/") ? type.split("/")[0] : type;
	}

	public String getType2() {
		return type.contains("/") ? type.split("/")[1] : "없음";
	}

	// ─── getter / setter ──────────────────────────────────────
	public String getName()               { return name; }
	public int getLevel()                 { return level; }
	public int getMaxHp()                 { return maxHp; }
	public int getHp()                    { return currentHp; }
	public int getCurrentHp()             { return currentHp; }
	public int getAttack()                { return attack; }
	public String getType()               { return type; }
	public String getRarity()             { return rarity; }
	public List<Skill> getSkills()        { return skills; }
	public ArrayList<Skill> getMoves()    { return new ArrayList<>(skills); }
	public StatusEffect getStatusEffect() { return statusEffect; }
	public int getIvHp()                  { return ivHp; }
	public int getIvAtk()                 { return ivAtk; }
	public int getExp()                   { return exp; }
	public int getExpNeeded()             { return expNeeded; }
	public void setName(String name)      { this.name = name; }
}
