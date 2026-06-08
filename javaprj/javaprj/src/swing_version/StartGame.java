package swing_version;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class StartGame {

    public static MapPlayer start() {
        IntroDialog dialog = new IntroDialog();
        dialog.startScenario();
        dialog.setVisible(true);   // 모달이라 여기서 대기
        return dialog.getResultPlayer();
    }

    private static class IntroDialog extends JDialog {

        private static final int AUTO_DELAY = 800;

        private final JPanel logPanel = new JPanel();
        private final JScrollPane scrollPane;

        private final JTextField inputField = new JTextField();
        private final JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        private Timer timer;

        private final Pokedex pokedex = GameDataManager.createDefaultPokedex();

        private final StartingPokemon[] starters = {
                new StartingPokemon("이상해씨", "풀", 0.7, 6.9,
                        "태어났을 때부터 등에 이상한 씨앗이 심어져 있으며 몸과 함께 자란다고 한다."),
                new StartingPokemon("꼬부기", "물", 0.5, 9.0,
                        "등껍질에 숨어 몸을 보호한다. 상대의 빈틈을 놓치지 않고 물을 뿜어내어 반격한다."),
                new StartingPokemon("파이리", "불", 0.6, 8.5,
                        "꼬리의 불꽃은 파이리의 생명력의 상징이다. 건강할 때 왕성하게 불타오른다.")
        };

        private String playerName;
        private int genderChoice;
        private StartingPokemon selectedStarter;
        private Pokemon starterPokemon;
        private MapPlayer resultPlayer;

        IntroDialog() {
            super((Frame) null, "포켓몬스터 시작", true); // 모달

            setSize(760, 650);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLayout(new BorderLayout(10, 10));

            logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
            logPanel.setBackground(new Color(245, 248, 252));
            logPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

            scrollPane = new JScrollPane(
                    logPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            );
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.setBorder(BorderFactory.createTitledBorder("로그"));

            JPanel southPanel = new JPanel(new BorderLayout(8, 8));
            southPanel.setBorder(new EmptyBorder(0, 12, 12, 12));

            inputField.setFont(new Font("Dialog", Font.PLAIN, 16));
            inputField.setVisible(false);

            controlPanel.setVisible(false);

            southPanel.add(inputField, BorderLayout.NORTH);
            southPanel.add(controlPanel, BorderLayout.CENTER);

            add(scrollPane, BorderLayout.CENTER);
            add(southPanel, BorderLayout.SOUTH);
        }

        public MapPlayer getResultPlayer() {
            return resultPlayer;
        }

        public void startScenario() {
            SwingUtilities.invokeLater(() -> playMessages(new String[]{
                    ".",
                    ".",
                    ".",
                    "여기는 태초마을",
                    "태초는 새하얀 근원의 색",
                    "오박사: 만나서 반갑다!",
                    "포켓몬스터의 세계에 잘왔단다!",
                    "내 이름은 오박사, 포켓몬 연구를 하고 있단다!"
            }, this::askGender));
        }

        private void appendMessage(String message) {
            appendMessage(message, null);
        }

        private void appendMessage(String message, Runnable onComplete) {
            logPanel.add(BT_Dialog.createMessageBox(message, onComplete));
            logPanel.revalidate();
            logPanel.repaint();

            SwingUtilities.invokeLater(() -> {
                JScrollBar bar = scrollPane.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            });
        }

        private void clearControls() {
            if (timer != null) {
                timer.stop();
                timer = null;
            }

            inputField.setText("");
            inputField.setVisible(false);

            controlPanel.removeAll();
            controlPanel.setVisible(false);

            getRootPane().setDefaultButton(null);

            controlPanel.revalidate();
            controlPanel.repaint();
        }

        private void playMessages(String[] messages, Runnable after) {
            clearControls();
            playMessageAt(messages, 0, after);
        }

        private void playMessageAt(String[] messages, int index, Runnable after) {
            if (index >= messages.length) {
                if (after != null) after.run();
                return;
            }
            appendMessage(messages[index], () -> {
                // 타이핑 완료 후 짧은 딜레이를 두고 다음 메시지 출력
                timer = new Timer(300, e -> {
                    timer.stop();
                    timer = null;
                    playMessageAt(messages, index + 1, after);
                });
                timer.setRepeats(false);
                timer.start();
            });
        }

        private void showChoiceButtons(String message, String[] choices, IntConsumer consumer) {
            clearControls();
            appendMessage(message, () -> {
                controlPanel.setVisible(true);
                for (int i = 0; i < choices.length; i++) {
                    final int idx = i;
                    JButton button = new JButton(choices[i]);
                    button.setFont(new Font("Dialog", Font.BOLD, 15));
                    button.addActionListener(e -> {
                        clearControls();
                        consumer.accept(idx);
                    });
                    controlPanel.add(button);
                }
                controlPanel.revalidate();
                controlPanel.repaint();
            });
        }

        private void showTextInput(String message, String buttonText, Consumer<String> consumer) {
            clearControls();
            appendMessage(message, () -> {
                inputField.setVisible(true);
                controlPanel.setVisible(true);

                JButton okButton = new JButton(buttonText);
                okButton.setFont(new Font("Dialog", Font.BOLD, 15));
                okButton.addActionListener(e -> consumer.accept(inputField.getText().trim()));

                controlPanel.add(okButton);
                getRootPane().setDefaultButton(okButton);

                controlPanel.revalidate();
                controlPanel.repaint();

                SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());
            });
        }

        private void askGender() {
            showChoiceButtons(
                    "자네는 남자인가? 아니면 여자인가?",
                    new String[]{"남자", "여자"},
                    idx -> {
                        genderChoice = idx + 1;
                        String gender = (genderChoice == 1) ? "남자" : "여자";

                        playMessages(new String[]{
                                "자네는 " + gender + "로구나!",
                                "슬슬 너의 이름을 가르쳐다오!"
                        }, this::askName);
                    }
            );
        }

        private void askName() {
            showTextInput("이름을 입력해주세요.", "확인", value -> {
                if (value.isEmpty()) {
                    appendMessage("이름을 다시 입력해주세요.");
                    return;
                }

                playerName = value;

                playMessages(new String[]{
                        "흠...",
                        "너는 " + playerName + "이라고 하는구나!"
                }, this::showStarterIntro);
            });
        }

        private void showStarterIntro() {
            playMessages(new String[]{
                    "바깥은 혼자 돌아다니기엔 위험하단다!",
                    "이 아이들 중 하나를 데려가렴!",
                    "좋아하는 걸 1마리를 주겠다!",
                    "......자 골라보렴!"
            }, this::showStarterList);
        }

        private void showStarterList() {
            showStarterAt(0);
        }

        private void showStarterAt(int idx) {
            if (idx >= starters.length) {
                askStarterChoice();
                return;
            }
            String text =
                    (idx + 1) + ". " + starters[idx].name + "\n" +
                    "타입: " + starters[idx].type + "\n" +
                    "키: " + starters[idx].height + "m\n" +
                    "몸무게: " + starters[idx].weight + "kg\n" +
                    "설명: " + starters[idx].description;
            appendMessage(text, () -> {
                timer = new Timer(300, e -> {
                    timer.stop();
                    timer = null;
                    showStarterAt(idx + 1);
                });
                timer.setRepeats(false);
                timer.start();
            });
        }

        private void appendStarterInfo(StartingPokemon p, int number) {
            String text =
                    number + ". " + p.getName() + "\n" +
                            "타입: " + p.type + "\n" +
                            "키: " + p.height + "m\n" +
                            "몸무게: " + p.weight + "kg\n" +
                            "설명: " + p.description;
            appendMessage(text);
        }

        private void askStarterChoice() {
            showChoiceButtons(
                    "스타팅 포켓몬을 선택해라!",
                    new String[]{"이상해씨", "꼬부기", "파이리"},
                    idx -> {
                        selectedStarter = starters[idx];

                        if (idx == 0) {
                            playMessages(new String[]{
                                    "그렇구나! 이상해씨가 맘에 드는구나!",
                                    "이 애는 엄청 키우기 쉽단다!"
                            }, this::confirmStarter);
                        } else if (idx == 1) {
                            playMessages(new String[]{
                                    "흠, 꼬부기가 맘에 드는구나!",
                                    "키우는 보람이 있는 포켓몬이지!"
                            }, this::confirmStarter);
                        } else {
                            playMessages(new String[]{
                                    "그래! 파이리가 맘에 드는구나!",
                                    "천천히 키우면 좋단다!"
                            }, this::confirmStarter);
                        }
                    }
            );
        }

        private void confirmStarter() {
            showChoiceButtons(
                    playerName + "은(는) " + selectedStarter.type + " 포켓몬 " + selectedStarter.getName() + "가 맘에 드는 거니?",
                    new String[]{"예", "아니요"},
                    idx -> {
                        if (idx == 0) {
                            receiveStarter();
                        } else {
                            playMessages(new String[]{
                                    "다시 선택하게 해주마!"
                            }, this::showStarterIntro);
                        }
                    }
            );
        }

        private void receiveStarter() {
            starterPokemon = pokedex.getPokemon(selectedStarter.getName());
            if (starterPokemon != null) {
                starterPokemon = starterPokemon.copy();
            }

            playMessages(new String[]{
                    playerName + "은(는) 오박사에게 " + selectedStarter.getName() + "를(을) 받았다!"
            }, this::askNickname);
        }

        private void askNickname() {
            showChoiceButtons(
                    selectedStarter.getName() + "에게 별명을 붙이겠습니까?",
                    new String[]{"예", "아니요"},
                    idx -> {
                        if (idx == 0) {
                            inputNickname();
                        } else {
                            finalizePlayerAndFinish();
                        }
                    }
            );
        }

        private void inputNickname() {
            showTextInput("닉네임을 입력해주세요.", "확인", value -> {
                if (value.isEmpty()) {
                    appendMessage("닉네임을 다시 입력해주세요.");
                    return;
                }

                if (starterPokemon != null) {
                    starterPokemon.setName(value);
                }

                finalizePlayerAndFinish();
            });
        }

        private void finalizePlayerAndFinish() {
            MapPlayer player = new MapPlayer();
            player.setName(playerName);

            if (starterPokemon != null) {
                player.addPokemon(starterPokemon);
            }

            resultPlayer = player;

            String pokemonName = (starterPokemon != null)
                    ? starterPokemon.getName()
                    : selectedStarter.getName();

            playMessages(new String[]{
                    pokemonName + "은(는) " + playerName + "의 포켓몬이 되었다!",
                    playerName + "! 준비는 되었는가?",
                    "이제부터 너만의 이야기가 시작된다!",
                    "꿈과 모험과!",
                    "포켓몬스터의 세계로!",
                    "레츠 고-!"
            }, this::dispose);
        }
    }
}