package swing_version;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class SaveManager {
    private static final String SAVE_FILE = "pokemon_save.dat";

    static class LoadResult {
        List<Pokemon> party;
        String message;

        LoadResult(List<Pokemon> party, String message) {
            this.party = party;
            this.message = message;
        }
    }

    public static String saveGameMessage(List<Pokemon> playerParty) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(playerParty);
            return "게임이 저장되었습니다. (" + SAVE_FILE + ")";
        } catch (IOException e) {
            return "저장 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @SuppressWarnings("unchecked")
    public static LoadResult loadGameWithMessage() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return new LoadResult(new ArrayList<>(), "저장 파일이 없습니다.");

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            List<Pokemon> loadedParty = (List<Pokemon>) ois.readObject();
            return new LoadResult(loadedParty, "게임을 불러왔습니다. (" + SAVE_FILE + ")");
        } catch (IOException | ClassNotFoundException e) {
            return new LoadResult(new ArrayList<>(), "불러오기 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ─── console 버전 메서드 (Gamecontroller 호환) ───────────────────────────
    public static void saveGame(List<Pokemon> playerParty) {
        BT_Dialog.show(saveGameMessage(playerParty));
    }

    @SuppressWarnings("unchecked")
    public static List<Pokemon> loadGame() {
        LoadResult result = loadGameWithMessage();
        BT_Dialog.show(result.message);
        return result.party;
    }
}
