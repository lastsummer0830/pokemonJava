package swing_version;

import java.util.*;

public class Gamecontroller {
	private Scanner scanner;
	private Pokedex pokedex;
	private List<Pokemon> playerParty;
	private boolean isRunning;

	public Gamecontroller() {
		scanner = new Scanner(System.in);
		pokedex = GameDataManager.createDefaultPokedex();
		playerParty = new ArrayList<>();
		isRunning = true;
	}

	public static void main(String[] args) {
		Gamecontroller game = new Gamecontroller();
		game.start();
	}

	public void start() {
		BT_Dialog.show("==================================");
		BT_Dialog.show("      포켓몬 게임 시스템 시작");
		BT_Dialog.show("==================================");

		showMainMenu(); // 처음 시작할 때만 전체 메뉴 출력

		while (isRunning) {
			BT_Dialog.show("\n==============================================");
			BT_Dialog.show("          + 입력 ➜ 메인메뉴 보기");
			BT_Dialog.show("==============================================\n");
			System.out.print("메뉴 선택 >> ");
			String choice = scanner.nextLine().trim();

			if (choice.equals("+")) {
				showMainMenu(); // + 입력 시 메뉴 다시 펼쳐서 보여주기
			} else {
				handleMainMenu(choice);
			}
		}
	}

	private void showMainMenu() {
		BT_Dialog.show("\n[ 메인 메뉴 ]");
		BT_Dialog.show("1. 포켓몬 도감 보기");
		BT_Dialog.show("2. 포켓몬 상세 보기");
		BT_Dialog.show("3. 야생 포켓몬 포획");
		BT_Dialog.show("4. 내 파티 보기");
		BT_Dialog.show("5. 파티 회복");
		BT_Dialog.show("6. 게임 저장");
		BT_Dialog.show("7. 게임 불러오기");
		BT_Dialog.show("0. 게임 종료");
	}

	private void handleMainMenu(String choice) {
		switch (choice) {
		case "1": pokedex.displayAll(); break;
		case "2":
			System.out.print("상세 정보를 볼 포켓몬 이름 입력 >> ");
			String name = scanner.nextLine();
			pokedex.displayOne(name);
			break;
		case "3": captureRandomPokemon(); break;
		case "4": showPlayerParty(); break;
		case "5": healParty(); break;
		case "6": SaveManager.saveGame(playerParty); break;
		case "7": playerParty = SaveManager.loadGame(); break;
		case "0": endGame(); break;
		default:  BT_Dialog.show("올바른 메뉴 번호를 입력하세요.");
		}
	}

	private void captureRandomPokemon() {
		List<Pokemon> wildList = pokedex.getByRarity("Wild");
		if (wildList.isEmpty()) {
			BT_Dialog.show("포획 가능한 야생 포켓몬이 없습니다.");
			return;
		}
		Random random = new Random();
		Pokemon target = wildList.get(random.nextInt(wildList.size()));
		BT_Dialog.show("\n==== 야생 포켓몬 발견! ====");
		BT_Dialog.show(target.getName() + " 이(가) 나타났다!");
		BT_Dialog.show("타입: " + target.getType());
		BT_Dialog.show("Lv." + target.getLevel() + " | HP " + target.getMaxHp() + " | ATK " + target.getAttack());
		System.out.print("이 포켓몬을 포획하시겠습니까? (y/n) >> ");
		String choice = scanner.nextLine();
		if (choice.equalsIgnoreCase("y")) {
			Pokemon copied = target.copy();
			playerParty.add(copied);
			BT_Dialog.show(String.valueOf(copied.getName()));
			BT_Dialog.show("포획 성공! 파티에 추가되었습니다.");
		} else {
			BT_Dialog.show("\n포획을 취소했습니다.");
		}
	}

	private void showPlayerParty() {
		BT_Dialog.show("\n==== 내 파티 ====");
		if (playerParty.isEmpty()) {
			BT_Dialog.show("현재 파티가 비어 있습니다.");
			return;
		}
		for (int i = 0; i < playerParty.size(); i++) {
			Pokemon p = playerParty.get(i);
			BT_Dialog.show((i + 1) + ". " + p.getName() + " | 타입: " + p.getType()
					+ " | Lv." + p.getLevel() + " | HP " + p.getCurrentHp() + "/" + p.getMaxHp());
		}
	}

	private void healParty() {
		if (playerParty.isEmpty()) {
			BT_Dialog.show("회복할 포켓몬이 없습니다.");
			return;
		}
		for (Pokemon p : playerParty) p.healFull();
		BT_Dialog.show("파티의 모든 포켓몬이 회복되었습니다.");
	}

	private void endGame() {
		BT_Dialog.show("게임을 종료합니다.");
		isRunning = false;
	}
}
