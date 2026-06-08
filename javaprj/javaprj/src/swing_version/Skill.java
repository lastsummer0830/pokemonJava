package swing_version;

import java.io.Serializable;

class Skill implements Serializable {
	private String name;
	private String type;
	private int power;
	private String effect; // ex) "화상 10%", "마비 100%", "없음"

	public Skill(String name, String type, int power, String effect) {
		this.name = name;
		this.type = type;
		this.power = power;
		this.effect = effect;
	}

	// 위력이 0이면 상태이상 전용 기술
	public boolean isStatusMove() {
		return power == 0;
	}

	// "화상 10%" → "화상", "없음" → "없음"
	public String getStatusEffect() {
		if (effect.equals("없음") || effect.equals("흡수")) return StatusEffect.NONE;
		String[] parts = effect.split(" ");
		if (parts.length >= 2) return parts[0];
		return StatusEffect.NONE;
	}

	// "화상 10%" → 10, "없음" → 0
	public int getStatusChance() {
		if (effect.equals("없음") || effect.equals("흡수")) return 0;
		String[] parts = effect.split(" ");
		if (parts.length >= 2) {
			try {
				return Integer.parseInt(parts[1].replace("%", ""));
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}

	public String getName()   { return name; }
	public String getType()   { return type; }
	public int getPower()     { return power; }
	public String getEffect() { return effect; }
}
