package swing_version;

import java.io.Serializable;

public class StatusEffect implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NONE = "없음";
    public static final String SLEEP = "수면";
    public static final String PARA = "마비";
    public static final String BURN = "화상";
    public static final String POISON = "독";
    public static final String CONFUSE = "혼란";
    public static final String FREEZE = "동상";

    private String status = NONE;
    private int sleepTurn = 0;
    private int paraTurn = 0;
    private int confuseTurn = 0;

    public void apply(String newStatus) {
        if (!status.equals(NONE)) return;

        this.status = newStatus;

        if (newStatus.equals(SLEEP)) sleepTurn = (int) (Math.random() * 2) + 1;
        if (newStatus.equals(PARA)) paraTurn = (int) (Math.random() * 2) + 1;
        if (newStatus.equals(CONFUSE)) confuseTurn = (int) (Math.random() * 2) + 1;
    }

    public String getStatus() { return status; }
    public boolean isSleeping() { return status.equals(SLEEP); }
    public boolean isParalyzed() { return status.equals(PARA); }
    public boolean isBurned() { return status.equals(BURN); }
    public boolean isPoisoned() { return status.equals(POISON); }
    public boolean isConfused() { return status.equals(CONFUSE); }
    public boolean isFrozen() { return status.equals(FREEZE); }

    public void applyEndOfTurn(Pokemon pokemon) {
        applyEndOfTurn(pokemon, System.out::println);
    }

    public void applyEndOfTurn(Pokemon pokemon, BattleLogger logger) {
        switch (status) {
            case BURN:
                int burnDmg = Math.max(1, pokemon.getMaxHp() / 8);
                pokemon.takeDamage(burnDmg);
                logger.log(pokemon.getName() + "은(는) 화상으로 데미지를 입었다!");
                break;
            case POISON:
                int poisonDmg = Math.max(1, pokemon.getMaxHp() / 8);
                pokemon.takeDamage(poisonDmg);
                logger.log(pokemon.getName() + "은(는) 독에 걸려 독이 퍼지고 있다!");
                break;
            case SLEEP:
                sleepTurn--;
                if (sleepTurn <= 0) {
                    status = NONE;
                    logger.log(pokemon.getName() + "은(는) 잠에서 깨어났다!");
                }
                break;
            case PARA:
                paraTurn--;
                if (paraTurn <= 0) {
                    status = NONE;
                    logger.log(pokemon.getName() + "의 마비가 풀렸다!");
                }
                break;
            case CONFUSE:
                confuseTurn--;
                if (confuseTurn <= 0) {
                    status = NONE;
                    logger.log(pokemon.getName() + "의 혼란이 풀렸다!");
                }
                break;
            case FREEZE:
                int freezeDmg = Math.max(1, pokemon.getMaxHp() / 8);
                pokemon.takeDamage(freezeDmg);
                logger.log(pokemon.getName() + "은(는) 동상으로 데미지를 입었다!");
                break;
        }
    }

    public boolean canAct(Pokemon pokemon) {
        return canAct(pokemon, System.out::println);
    }

    public boolean canAct(Pokemon pokemon, BattleLogger logger) {
        if (isSleeping()) {
            logger.log(pokemon.getName() + "은(는) 잠들어 있다...");
            return false;
        }
        if (isParalyzed()) {
            logger.log(pokemon.getName() + "은(는) 마비로 움직이지 못했다!");
            return false;
        }
        if (isConfused()) {
            logger.log(pokemon.getName() + "은(는) 혼란으로 움직이지 못했다!");
            return false;
        }
        return true;
    }

    public void clear() {
        status = NONE;
        sleepTurn = 0;
        paraTurn = 0;
        confuseTurn = 0;
    }
}
