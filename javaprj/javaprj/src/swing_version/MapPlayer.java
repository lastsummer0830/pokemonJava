package swing_version;

import java.util.ArrayList;
import java.util.List;

public class MapPlayer {

    int x = 1;
    int y = 1;

    private String name;
    private ArrayList<Pokemon> party = new ArrayList<>();
    private ArrayList<Pokemon> box = new ArrayList<>();

    public String getName() {
        return name == null || name.isBlank() ? "이름없음" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void healAll() {
        for (Pokemon p : party) {
            p.healFull();
        }
    }

    public String getFirstPokemonName() {
        if (!party.isEmpty()) return party.get(0).getName();
        return "없음";
    }

    public boolean addPokemon(Pokemon pokemon) {
        if (pokemon == null || party.size() >= 6) return false;
        party.add(pokemon);
        return true;
    }

    public int getPartySize() {
        return party.size();
    }

    // index 위치의 포켓몬을 newPokemon으로 교체하고, 원래 포켓몬을 반환
    public Pokemon swapPokemon(int index, Pokemon newPokemon) {
        Pokemon old = party.get(index);
        party.set(index, newPokemon);
        return old;
    }

    public void addToBox(Pokemon pokemon) {
        if (pokemon != null) box.add(pokemon);
    }

    public ArrayList<Pokemon> getBox() {
        return box;
    }

    public boolean isPartyEmpty() {
        return party.isEmpty();
    }

    public boolean isAllFainted() {
        return !party.isEmpty() && party.stream().allMatch(Pokemon::isFainted);
    }

    public ArrayList<Pokemon> getParty() {
        return party;
    }

    public void setParty(List<Pokemon> newParty) {
        this.party = new ArrayList<>(newParty);
    }

    public void moveToCenter() {
        this.x = 3;
        this.y = 3;
    }

    public String getPartySummaryText() {
        if (party.isEmpty()) return "현재 파티가 비어 있습니다.";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < party.size(); i++) {
            Pokemon p = party.get(i);
            sb.append(i + 1)
                    .append(". ")
                    .append(p.getName())
                    .append(" | 타입: ")
                    .append(p.getType())
                    .append(" | Lv.")
                    .append(p.getLevel())
                    .append(" | HP ")
                    .append(p.getHp())
                    .append("/")
                    .append(p.getMaxHp());

            if (p.isFainted()) sb.append(" [기절]");
            if (i < party.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }
}