package swing_version;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class BattleEngine {

    private BattleLogger logger = System.out::println;
    private Component parentComponent;
    private Runnable logDrainWaiter;

    public void setLogger(BattleLogger logger) {
        if (logger != null) this.logger = logger;
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    public void setLogDrainWaiter(Runnable waiter) {
        this.logDrainWaiter = waiter;
    }

    private void waitForLog() {
        if (logDrainWaiter != null) logDrainWaiter.run();
    }

    private int invokeDialog(IntSupplier dialogSupplier) {
        waitForLog();
        if (SwingUtilities.isEventDispatchThread()) {
            return dialogSupplier.getAsInt();
        }
        int[] result = {-1};
        try {
            SwingUtilities.invokeAndWait(() -> result[0] = dialogSupplier.getAsInt());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    public void setLogArea(JTextArea logArea) {
        if (logArea == null) return;
        setLogger(message -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void printLog(String message) {
        logger.log(BT_Dialog.format(message));
    }

    public boolean startBattle(ArrayList<Pokemon> playerParty, Pokemon enemyPokemon) {
        printLog("");
       // printLog("╔═══════════════════════════════╗");
        printLog(" 야생의 " + enemyPokemon.getName() + "이(가) 나타났다!");
        //printLog("╚═══════════════════════════════╝");

        Pokemon mine = getFirstAlive(playerParty);
        if (mine == null) {
            printLog("싸울 수 있는 포켓몬이 없다!");
            return false;
        }

        printLog("가라! " + mine.getName() + "!");

        while (true) {
            printStatus(mine, enemyPokemon);

            int action = chooseAction(mine);
            if (action == -1) continue;

            if (action == 0) {
                Skill move = chooseSkill(mine);
                if (move == null) continue;

                if (!mine.getStatusEffect().canAct(mine, logger)) {
                    endOfTurn(mine, enemyPokemon);
                    mine = checkMyFainted(mine, playerParty);
                    if (mine == null) return false;
                    if (enemyPokemon.isFainted()) {
                        printWin(enemyPokemon);
                        handlePostBattle(mine, enemyPokemon);
                        return true;
                    }
                    continue;
                }

                doAttack(mine, move, enemyPokemon);
                if (enemyPokemon.isFainted()) {
                    printWin(enemyPokemon);
                    handlePostBattle(mine, enemyPokemon);
                    return true;
                }

                if (!enemyPokemon.getStatusEffect().canAct(enemyPokemon, logger)) {
                    endOfTurn(mine, enemyPokemon);
                    mine = checkMyFainted(mine, playerParty);
                    if (mine == null) return false;
                    if (enemyPokemon.isFainted()) {
                        printWin(enemyPokemon);
                        handlePostBattle(mine, enemyPokemon);
                        return true;
                    }
                    continue;
                }

                enemyTurn(mine, enemyPokemon);
                mine = checkMyFainted(mine, playerParty);
                if (mine == null) return false;
            } else if (action == 1) {
                Pokemon next = switchPokemon(playerParty, mine);
                if (next != null && next != mine) {
                    printLog("수고했어, " + mine.getName() + "! 들어와!");
                    mine = next;
                    printLog("가라! " + mine.getName() + "!");
                    enemyTurn(mine, enemyPokemon);
                    mine = checkMyFainted(mine, playerParty);
                    if (mine == null) return false;
                }
            } else if (action == 2) {
                if (Math.random() < 0.5) {
                    printLog("성공적으로 도망쳤다!");
                    return false;
                } else {
                    printLog("도망치지 못했다!");
                    enemyTurn(mine, enemyPokemon);
                    mine = checkMyFainted(mine, playerParty);
                    if (mine == null) return false;
                }
            }

            endOfTurn(mine, enemyPokemon);
            if (enemyPokemon.isFainted()) {
                printWin(enemyPokemon);
                handlePostBattle(mine, enemyPokemon);
                return true;
            }

            mine = checkMyFainted(mine, playerParty);
            if (mine == null) return false;
        }
    }

    private void doAttack(Pokemon attacker, Skill skill, Pokemon target) {
        printLog(attacker.getName() + "의 " + skill.getName() + "!");

        if (skill.isStatusMove()) {
            applySkillEffect(skill, target);
            return;
        }

        double multiplier = TypeEffect.getMultiplier(skill.getType(), target.getType1(), target.getType2());
        int baseDamage = attacker.getAttack() + skill.getPower() / 5;
        int damage = Math.max(1, (int) (baseDamage * multiplier));
        if (attacker.getStatusEffect().isBurned()) damage /= 2;

        target.takeDamage(damage);
        printLog(target.getName() + "에게 " + damage + "의 데미지!");

        String effectMessage = TypeEffect.getEffectMessage(multiplier);
        if (!effectMessage.isEmpty()) printLog(effectMessage);

        applySkillEffect(skill, target);
    }

    private void applySkillEffect(Skill skill, Pokemon target) {
        String effect = skill.getStatusEffect();
        int chance = skill.getStatusChance();
        if (!StatusEffect.NONE.equals(effect) && Math.random() * 100 < chance) {
            if (StatusEffect.NONE.equals(target.getStatusEffect().getStatus())) {
                target.getStatusEffect().apply(effect);
                printLog(target.getName() + "은(는) " + effect + " 상태가 되었다!");
            }
        }
    }

    private void enemyTurn(Pokemon mine, Pokemon enemy) {
        Skill enemySkill = getRandomSkill(enemy);
        if (enemySkill == null) return;

        printLog("야생의 " + enemy.getName() + "의 " + enemySkill.getName() + "!");

        if (enemySkill.isStatusMove()) {
            applySkillEffect(enemySkill, mine);
            return;
        }

        double multiplier = TypeEffect.getMultiplier(enemySkill.getType(), mine.getType1(), mine.getType2());
        int baseDamage = enemy.getAttack() + enemySkill.getPower() / 5;
        int damage = Math.max(1, (int) (baseDamage * multiplier));
        if (enemy.getStatusEffect().isBurned()) damage /= 2;

        mine.takeDamage(damage);
        printLog(mine.getName() + "은(는) " + damage + "의 데미지를 입었다!");

        String effectMessage = TypeEffect.getEffectMessage(multiplier);
        if (!effectMessage.isEmpty()) printLog(effectMessage);

        applySkillEffect(enemySkill, mine);
    }

    private void endOfTurn(Pokemon mine, Pokemon enemy) {
        if (mine != null) mine.getStatusEffect().applyEndOfTurn(mine, logger);
        if (enemy != null) enemy.getStatusEffect().applyEndOfTurn(enemy, logger);
    }

    private void printWin(Pokemon enemy) {
        printLog(enemy.getName() + "은(는) 쓰러졌다! 전투에서 승리했다!");
    }

    // ─── 전투 승리 후 처리: 레벨업 → 기술 습득 → 진화 체크 ────────────────
    private void handlePostBattle(Pokemon mine, Pokemon defeated) {
        // 1) 레벨업 (배틀당 2레벨)
        mine.levelUp();
        mine.levelUp();
        printLog(mine.getName() + "의 레벨이 " + mine.getLevel() + "로 올랐다!");
        printLog(String.format("  HP: %d  ATK: %d", mine.getMaxHp(), mine.getAttack()));

        // 2) 레벨업 기술 습득 체크
        String learnableName = BT_LearnSet.getLearnableSkill(mine.getName(), mine.getLevel());
        if (learnableName != null) {
            printLog(mine.getName() + "은(는) " + learnableName + "을(를) 떠올렸다!");
            String[] learnOptions = {"배운다", "배우지 않는다"};
            int learnChoice = invokeDialog(() -> JOptionPane.showOptionDialog(
                    parentComponent,
                    mine.getName() + "은(는) " + learnableName + "을(를) 배울 수 있다!",
                    "기술 습득",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    learnOptions, learnOptions[0]));
            if (learnChoice == 0) {
                tryLearnSkill(mine, learnableName);
            } else {
                printLog(learnableName + "을(를) 배우지 않았다.");
            }
        }

        // 3) 진화 체크
        if (BT_Evolution.canEvolve(mine.getName(), mine.getLevel())) {
            String evolvedName = BT_Evolution.getEvolvedName(mine.getName());
            printLog("어라? " + mine.getName() + "의 모습이...!");
            String[] evoOptions = {"진화한다", "진화하지 않는다"};
            int evoChoice = invokeDialog(() -> JOptionPane.showOptionDialog(
                    parentComponent,
                    mine.getName() + "이(가) " + evolvedName + "로 진화할 수 있다!",
                    "진화",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    evoOptions, evoOptions[0]));
            if (evoChoice == 0) {
                printLog("축하합니다! " + mine.getName() + "은(는) " + evolvedName + "로 진화했다!");
                mine.evolve(evolvedName);
            } else {
                printLog(mine.getName() + "은(는) 진화를 거부했다.");
            }
        }
    }

    // ─── 기술 습득 처리: 빈 슬롯이면 바로 추가, 꽉 찼으면 교체 선택 ────────
    private void tryLearnSkill(Pokemon mine, String skillName) {
        Skill newSkill = createSkillByName(skillName);
        if (newSkill == null) {
            printLog("(기술 데이터를 찾을 수 없어 배우지 못했다.)");
            return;
        }
        if (mine.canLearnSkill()) {
            mine.addSkill(newSkill);
            printLog(mine.getName() + "은(는) " + skillName + "을(를) 배웠다!");
        } else {
            List<Skill> skills = mine.getSkills();
            String[] options = new String[skills.size() + 1];
            for (int i = 0; i < skills.size(); i++) {
                Skill s = skills.get(i);
                options[i] = s.getName() + " (" + s.getType() + ", 위력 " + s.getPower() + ")";
            }
            options[skills.size()] = skillName + "을(를) 배우지 않는다";

            int choice = invokeDialog(() -> JOptionPane.showOptionDialog(
                    parentComponent,
                    "이미 기술이 4개입니다. 어떤 기술을 잊게 할까요?",
                    "기술 교체",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    options, options[0]));

            if (choice < 0 || choice == skills.size()) {
                printLog(mine.getName() + "은(는) " + skillName + "을(를) 배우지 않았다.");
            } else {
                String forgotName = skills.get(choice).getName();
                mine.replaceSkill(choice, newSkill);
                printLog(mine.getName() + "은(는) " + forgotName + "을(를) 잊고, " + skillName + "을(를) 배웠다!");
            }
        }
    }

    // ─── 기술 이름으로 Skill 객체 생성 ────────────────────────────────────────
    private Skill createSkillByName(String name) {
        switch (name) {
            case "전광석화":        return new Skill("전광석화",       "노말",  40, "없음");
            case "몸통박치기":      return new Skill("몸통박치기",     "노말",  40, "없음");
            case "화염방사":        return new Skill("화염방사",       "불",    90, "화상 10%");
            case "분화":            return new Skill("분화",           "불",   150, "없음");
            case "불꽃세례":        return new Skill("불꽃세례",       "불",    60, "화상 30%");
            case "파도타기":        return new Skill("파도타기",       "물",    90, "없음");
            case "하이드로펌프":    return new Skill("하이드로펌프",   "물",   110, "없음");
            case "물대포":          return new Skill("물대포",         "물",    40, "없음");
            case "기가드레인":      return new Skill("기가드레인",     "풀",    90, "흡수");
            case "솔라빔":          return new Skill("솔라빔",         "풀",   120, "없음");
            case "잎날가르기":      return new Skill("잎날가르기",     "풀",    55, "없음");
            case "10만볼트":        return new Skill("10만볼트",       "전기",  90, "마비 10%");
            case "번개":            return new Skill("번개",           "전기", 110, "마비 30%");
            case "전기쇼크":        return new Skill("전기쇼크",       "전기",  40, "마비 10%");
            case "방전":            return new Skill("방전",           "전기",  80, "마비 10%");
            case "냉동펀치":        return new Skill("냉동펀치",       "얼음",  75, "동상 10%");
            case "눈사태":          return new Skill("눈사태",         "얼음",  60, "동상 10%");
            case "인파이트":        return new Skill("인파이트",       "격투", 120, "없음");
            case "파동탄":          return new Skill("파동탄",         "격투",  80, "없음");
            case "맹독":            return new Skill("맹독",           "독",     0, "독 100%");
            case "오물폭탄":        return new Skill("오물폭탄",       "독",    90, "독 30%");
            case "악의파동":        return new Skill("악의파동",       "악",    80, "없음");
            case "지진":            return new Skill("지진",           "땅",   100, "없음");
            case "에어슬래시":      return new Skill("에어슬래시",     "비행",  75, "없음");
            case "폭풍":            return new Skill("폭풍",           "비행", 110, "없음");
            case "사이코키네시스":  return new Skill("사이코키네시스", "에스퍼", 90, "없음");
            case "미래예지":        return new Skill("미래예지",       "에스퍼",120, "없음");
            case "시저크로스":      return new Skill("시저크로스",     "벌레",  80, "없음");
            case "스톤샤워":        return new Skill("스톤샤워",       "바위",  75, "없음");
            case "섀도볼":          return new Skill("섀도볼",         "고스트", 80, "없음");
            case "섀도다이브":      return new Skill("섀도다이브",     "고스트",120, "없음");
            case "드래곤클로":      return new Skill("드래곤클로",     "드래곤", 80, "없음");
            case "드래곤다이브":    return new Skill("드래곤다이브",   "드래곤",100, "없음");
            case "역린":            return new Skill("역린",           "드래곤",120, "혼란 100%");
            case "깜짝베기":        return new Skill("깜짝베기",       "악",    70, "없음");
            case "문포스":          return new Skill("문포스",         "페어리", 95, "없음");
            case "매지컬샤인":      return new Skill("매지컬샤인",     "페어리", 80, "없음");
            case "수면가루":        return new Skill("수면가루",       "풀",     0, "수면 100%");
            default:               return null;
        }
    }

    private Pokemon checkMyFainted(Pokemon mine, ArrayList<Pokemon> party) {
        if (mine != null && mine.isFainted()) {
            printLog(mine.getName() + "은(는) 쓰러졌다!");
            Pokemon next = getFirstAlive(party);
            if (next != null) printLog("가라! " + next.getName() + "!");
            return next;
        }
        return mine;
    }

    private Pokemon getFirstAlive(ArrayList<Pokemon> party) {
        for (Pokemon p : party) {
            if (!p.isFainted()) return p;
        }
        return null;
    }

    private void printStatus(Pokemon mine, Pokemon enemy) {
        //printLog("──────────────────────────────");
        printLog(String.format("[적] %-8s HP: %3d / %3d  [%s]", enemy.getName(), enemy.getHp(), enemy.getMaxHp(), enemy.getStatusEffect().getStatus()));
        printLog(String.format("[나] %-8s HP: %3d / %3d  [%s]", mine.getName(), mine.getHp(), mine.getMaxHp(), mine.getStatusEffect().getStatus()));
        //printLog("──────────────────────────────");
    }

    private int chooseAction(Pokemon mine) {
        String[] options = {"싸운다", "포켓몬 교체", "도망간다"};
        return invokeDialog(() -> JOptionPane.showOptionDialog(
                parentComponent,
                "행동을 선택하세요.\n현재 포켓몬: " + mine.getName(),
                "배틀",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        ));
    }

    private Skill chooseSkill(Pokemon pokemon) {
        List<Skill> moves = new ArrayList<>();
        for (Skill s : pokemon.getSkills()) {
            if (s != null) moves.add(s);
        }
        if (moves.isEmpty()) return null;

        String[] options = new String[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            Skill s = moves.get(i);
            options[i] = s.getName() + " | 타입: " + s.getType() + " | 위력: " + s.getPower();
        }

        int choice = invokeDialog(() -> JOptionPane.showOptionDialog(
                parentComponent,
                "사용할 기술을 선택하세요.",
                pokemon.getName() + "의 기술",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        ));

        if (choice < 0 || choice >= moves.size()) return null;
        return moves.get(choice);
    }

    private Pokemon switchPokemon(ArrayList<Pokemon> party, Pokemon current) {
        List<Pokemon> available = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (Pokemon p : party) {
            if (p != current && !p.isFainted()) {
                available.add(p);
                labels.add(p.getName() + " | HP " + p.getHp() + "/" + p.getMaxHp());
            }
        }

        if (available.isEmpty()) {
            printLog("교체할 수 있는 포켓몬이 없다!");
            return current;
        }

        int choice = invokeDialog(() -> JOptionPane.showOptionDialog(
                parentComponent,
                "교체할 포켓몬을 선택하세요.",
                "포켓몬 교체",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                labels.toArray(),
                labels.get(0)
        ));

        if (choice < 0 || choice >= available.size()) return current;
        return available.get(choice);
    }

    private Skill getRandomSkill(Pokemon pokemon) {
        List<Skill> moves = new ArrayList<>();
        for (Skill s : pokemon.getSkills()) {
            if (s != null) moves.add(s);
        }
        if (moves.isEmpty()) return null;
        return moves.get((int) (Math.random() * moves.size()));
    }
}
