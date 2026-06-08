package swing_version;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Player {
    private String playerName;
    private int x, y;
    private ArrayList<Pokemon> party;
    private final int MAX_PARTY = 6;

    Scanner sc = new Scanner(System.in);

    public Player() {}

    public Player(String playerName, int x, int y) {
        this.playerName = playerName;
        this.x = x;
        this.y = y;
        this.party = new ArrayList<>();
    }

    //몬스터 포획 메서드
    public void catchPokemon(Pokemon wildPokemon) {
        Random random = new Random();
        int catchRand = 50;

        BT_Dialog.show("가랏, 몬스터볼!");

        if (random.nextInt(100) >= catchRand) {
            if (party.size() < MAX_PARTY) {
                BT_Dialog.show("신난다~! " + wildPokemon.getName() + "를 붙잡았다!");
                party.add(wildPokemon);
            } else {
                switchPokemon(wildPokemon);
            }
        } else {
            BT_Dialog.show("아깝다! 조금만 더 하면 잡을 수 있었는데!");
        }
    }
    public void switchPokemon(Pokemon wildPokemon) {
        BT_Dialog.show("파티가 가득 찼습니다. 기존 포켓몬과 교체하시겠습니까? (Y/N)");
        String ch = sc.nextLine();

        if (ch.equals("Y") || ch.equals("y")) {
            for (int i = 0; i < party.size(); i++) {
                BT_Dialog.show((i + 1) + "번: " + party.get(i));
            }

            BT_Dialog.show("교체할 포켓몬 번호를 선택해주세요.");
            int index = sc.nextInt() - 1;
            sc.nextLine();

            if (index >= 0 && index < party.size()) {
                party.set(index, wildPokemon);
                BT_Dialog.show("교체 완료 되었습니다.");
            } else {
                BT_Dialog.show("잘못된 번호입니다.");
            }
        } else if (ch.equals("N") || ch.equals("n")) {
            BT_Dialog.show("파티가 가득 차서 " + wildPokemon.getName() + "은(는) 숲으로 돌아갔습니다.");
        }
    }
    //모든 포켓몬이 기절했는지 확인하는 메서드
    public boolean defeatCheck() {
        for (int i = 0; i < party.size(); i++) {
            if (party.get(i).getHp() > 0) {
                return false;
            }
        }

        BT_Dialog.show(playerName + "은(는) 눈 앞이 캄캄해졌다!");
        return true;
    }

    //모든 포켓몬 기절 후 포켓몬 센터로 이동 선택 시 실행될 메서드
    private void respawnAtCenter() {
        BT_Dialog.show("가까운 포켓몬 센터로 급히 이동합니다..");

        this.x = 2;
        this.y = 0;

        for (int i = 0; i < party.size(); i++) {
            party.get(i).healFull();
        }

        BT_Dialog.show("모든 포켓몬의 체력이 회복되었다!");
    }

    // 파티 내 포켓몬 조회 및 관리
    public void manageParty() {
        if (party.isEmpty()) {
            BT_Dialog.show("현재 파티에 포켓몬이 없습니다.");
            return;
        }

        while (true) {
            BT_Dialog.show("\n[내 파티 목록]");
            for (int i = 0; i < party.size(); i++) {
                BT_Dialog.show((i + 1) + ". " + party.get(i).getName() + " (HP: " + party.get(i).getHp() + ")");
            }
            BT_Dialog.show("0. 나가기");
            System.out.print("조회하거나 방생할 포켓몬 번호를 선택하세요: ");

            int sel = sc.nextInt();
            sc.nextLine(); // 버퍼 비우기

            if (sel == 0) break;
            if (sel < 1 || sel > party.size()) {
                BT_Dialog.show("잘못된 번호입니다.");
                continue;
            }

            Pokemon selected = party.get(sel - 1);
            BT_Dialog.show("\n선택된 포켓몬: " + selected.toString());
            BT_Dialog.show("1. 스펙 상세 조회 | 2. 방생하기 | 3. 취소");
            int action = sc.nextInt();
            sc.nextLine();

            if (action == 1) {
                // 상세 스펙 출력 (이름, 타입, HP 등)
                BT_Dialog.show("------- 상세 정보 -------");
                BT_Dialog.show("이름: " + selected.getName());
                BT_Dialog.show("타입: " + selected.getType());
                BT_Dialog.show("현재 HP: " + selected.getHp());
                BT_Dialog.show("-----------------------");
            } else if (action == 2) {
                // 방생 로직
                System.out.print("정말로 " + selected.getName() + "을(를) 방생하시겠습니까? (Y/N): ");
                String confirm = sc.nextLine();
                if (confirm.equalsIgnoreCase("Y")) {
                    party.remove(sel - 1);
                    BT_Dialog.show(selected.getName() + "을(를) 숲으로 돌려보냈습니다.");
                    if (party.isEmpty()) break;
                }
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public ArrayList<Pokemon> getParty() {
        return party;
    }
}
