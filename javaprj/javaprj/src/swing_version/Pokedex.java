package swing_version;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Pokedex {
    private final Map<String, Skill> skillDb;
    private final Map<String, Pokemon> pokemonDb;

    public Pokedex() {
        skillDb = new LinkedHashMap<>();
        pokemonDb = new LinkedHashMap<>();
    }

    public void addSkill(Skill skill) { skillDb.put(skill.getName(), skill); }
    public void addPokemon(Pokemon pokemon) { pokemonDb.put(pokemon.getName(), pokemon); }
    public Skill getSkill(String skillName) { return skillDb.get(skillName); }
    public Pokemon getPokemon(String pokemonName) { return pokemonDb.get(pokemonName); }

    public List<Pokemon> getAllPokemon() {
        return new ArrayList<>(pokemonDb.values());
    }

    public List<Pokemon> getByRarity(String rarity) {
        List<Pokemon> result = new ArrayList<>();
        for (Pokemon p : pokemonDb.values()) {
            if (p.getRarity().equalsIgnoreCase(rarity)) result.add(p);
        }
        return result;
    }

    public String getAllPokemonText() {
        StringBuilder sb = new StringBuilder("==== 포켓몬 도감 ====\n");
        int idx = 1;
        for (Pokemon p : pokemonDb.values()) {
            sb.append(idx)
              .append(". ")
              .append(p.getName())
              .append(" | 타입: ")
              .append(p.getType())
              .append(" | Lv.")
              .append(p.getLevel())
              .append(" | HP ")
              .append(p.getMaxHp())
              .append(" | ATK ")
              .append(p.getAttack())
              .append(" | 분류: ")
              .append(p.getRarity())
              .append("\n");
            idx++;
        }
        return sb.toString().trim();
    }

    // 포획한 포켓몬만 정보 표시, 미포획은 ??? 처리
    public String getAllPokemonText(Set<String> caughtNames) {
        StringBuilder sb = new StringBuilder("==== 포켓몬 도감 ====\n");
        int idx = 1;
        for (Pokemon p : pokemonDb.values()) {
            if (caughtNames.contains(p.getName())) {
                sb.append(idx)
                  .append(". ")
                  .append(p.getName())
                  .append(" | 타입: ")
                  .append(p.getType())
                  .append(" | Lv.")
                  .append(p.getLevel())
                  .append(" | HP ")
                  .append(p.getMaxHp())
                  .append(" | ATK ")
                  .append(p.getAttack())
                  .append(" | 분류: ")
                  .append(p.getRarity());
            } else {
                sb.append(idx)
                  .append(". ???")
                  .append(" | 타입: ???")
                  .append(" | Lv.???")
                  .append(" | HP ???")
                  .append(" | ATK ???")
                  .append(" | 분류: ???");
            }
            sb.append("\n");
            idx++;
        }
        return sb.toString().trim();
    }

    public String getPokemonDetailText(String pokemonName) {
        Pokemon p = getPokemon(pokemonName);
        if (p == null) return "해당 포켓몬이 존재하지 않습니다.";

        StringBuilder sb = new StringBuilder();
        sb.append("==== ").append(p.getName()).append(" 상세 정보 ====\n");
        sb.append("타입: ").append(p.getType()).append("\n");
        sb.append("레벨: ").append(p.getLevel()).append("\n");
        sb.append("HP: ").append(p.getCurrentHp()).append("/").append(p.getMaxHp()).append("\n");
        sb.append("ATK: ").append(p.getAttack()).append("\n");
        sb.append("분류: ").append(p.getRarity()).append("\n");
        sb.append("스킬 목록:\n");
        for (Skill s : p.getSkills()) {
            sb.append(" - ")
              .append(s.getName())
              .append(" (")
              .append(s.getType())
              .append(") | 위력: ")
              .append(s.getPower())
              .append(" | 효과: ")
              .append(s.getEffect())
              .append("\n");
        }
        return sb.toString().trim();
    }

    // ─── console 출력용 메서드 (Gamecontroller 호환) ──────────────────────────
    public void displayAll() {
        BT_Dialog.show("\n==== 포켓몬 도감 ====");
        int idx = 1;
        for (Pokemon p : pokemonDb.values()) {
            BT_Dialog.show(idx + ". " + p.getName() + " | 타입: " + p.getType()
                    + " | Lv." + p.getLevel() + " | HP " + p.getMaxHp()
                    + " | ATK " + p.getAttack() + " | 분류: " + p.getRarity());
            idx++;
        }
    }

    public void displayOne(String pokemonName) {
        Pokemon p = getPokemon(pokemonName);
        if (p == null) { BT_Dialog.show("해당 포켓몬이 존재하지 않습니다."); return; }
        BT_Dialog.show("\n==== " + p.getName() + " 상세 정보 ====");
        BT_Dialog.show("타입: " + p.getType());
        BT_Dialog.show("레벨: " + p.getLevel());
        BT_Dialog.show("HP: " + p.getCurrentHp() + "/" + p.getMaxHp());
        BT_Dialog.show("ATK: " + p.getAttack());
        BT_Dialog.show("분류: " + p.getRarity());
        BT_Dialog.show("스킬 목록:");
        for (Skill s : p.getSkills()) {
            BT_Dialog.show("  - " + s.getName() + " (" + s.getType() + ")"
                    + " | 위력: " + s.getPower() + " | 효과: " + s.getEffect());
        }
    }
}
