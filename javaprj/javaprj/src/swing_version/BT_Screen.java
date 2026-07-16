package swing_version;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 배틀 화면.
 *
 * 기존 전투는 JOptionPane 팝업으로만 진행돼서 HP·타입·상태이상이 로그 텍스트로만 흘렀다.
 * 이 창은 전투 중 상태를 항상 눈에 보이게 띄워두고, 행동/기술 선택도 여기서 받는다.
 *
 * BattleEngine의 전투 루프는 백그라운드 스레드에서 돈다.
 * 그래서 choose()는 호출한 스레드를 큐에서 블로킹하고, 화면 갱신은 EDT로 넘긴다.
 */
public class BT_Screen extends JDialog {

    /** 타입별 색 (뱃지·HP바 계열색) */
    private static final Map<String, Color> TYPE_COLORS = Map.ofEntries(
            Map.entry("노말",   new Color(168, 167, 122)),
            Map.entry("불",     new Color(238, 129,  48)),
            Map.entry("물",     new Color(99,  144, 240)),
            Map.entry("풀",     new Color(122, 199,  76)),
            Map.entry("전기",   new Color(247, 208,  44)),
            Map.entry("얼음",   new Color(150, 217, 214)),
            Map.entry("격투",   new Color(194,  46,  40)),
            Map.entry("독",     new Color(163,  62, 161)),
            Map.entry("땅",     new Color(226, 191, 101)),
            Map.entry("비행",   new Color(169, 143, 243)),
            Map.entry("에스퍼", new Color(249,  85, 135)),
            Map.entry("벌레",   new Color(166, 185,  26)),
            Map.entry("바위",   new Color(182, 161,  54)),
            Map.entry("고스트", new Color(115,  87, 151)),
            Map.entry("드래곤", new Color(111,  53, 252)),
            Map.entry("악",     new Color(112,  87,  70)),
            Map.entry("강철",   new Color(183, 183, 206)),
            Map.entry("페어리", new Color(214, 133, 173))
    );

    private static final Color BG_TOP    = new Color(222, 238, 214);
    private static final Color BG_BOTTOM = new Color(248, 248, 240);
    private static final Color INK       = new Color(48, 48, 48);

    private final PokemonCard enemyCard = new PokemonCard(true);
    private final PokemonCard myCard    = new PokemonCard(false);
    private final JLabel promptLabel    = new JLabel(" ");
    private final JPanel buttonPanel    = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

    /** choose() 결과를 EDT → 전투 스레드로 넘기는 통로 */
    private final BlockingQueue<Integer> answer = new ArrayBlockingQueue<>(1);

    public BT_Screen(Window owner) {
        super(owner, "배틀", ModalityType.MODELESS);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel field = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOTTOM));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // 적/아군 발판
                g2.setColor(new Color(198, 224, 186));
                g2.fillOval(getWidth() - 250, 122, 210, 44);
                g2.fillOval(30, 300, 230, 48);
            }
        };
        field.setPreferredSize(new Dimension(720, 380));

        enemyCard.setBounds(24, 24, 330, 92);
        myCard.setBounds(366, 214, 330, 110);
        field.add(enemyCard);
        field.add(myCard);

        promptLabel.setFont(new Font("Dialog", Font.PLAIN, 19));
        promptLabel.setForeground(INK);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(80, 80, 80)),
                new EmptyBorder(12, 16, 12, 16)));
        buttonPanel.setOpaque(false);
        bottom.add(promptLabel, BorderLayout.NORTH);
        bottom.add(buttonPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(field, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    /** 전투 스레드에서 호출 — 화면의 HP/상태 갱신 */
    public void refresh(Pokemon mine, Pokemon enemy) {
        SwingUtilities.invokeLater(() -> {
            enemyCard.bind(enemy);
            myCard.bind(mine);
        });
    }

    /** 전투 스레드에서 호출 — 버튼을 띄우고 사용자가 고를 때까지 블로킹. 고른 인덱스 반환 */
    public int choose(String prompt, String[] options) {
        answer.clear();
        SwingUtilities.invokeLater(() -> {
            promptLabel.setText(prompt);
            buttonPanel.removeAll();
            for (int i = 0; i < options.length; i++) {
                final int index = i;
                JButton b = new JButton(options[i]);
                b.setFont(new Font("Dialog", Font.PLAIN, 16));
                b.setFocusPainted(false);
                b.addActionListener(e -> {
                    buttonPanel.removeAll();
                    buttonPanel.revalidate();
                    buttonPanel.repaint();
                    answer.offer(index);
                });
                buttonPanel.add(b);
            }
            buttonPanel.revalidate();
            buttonPanel.repaint();
        });
        try {
            return answer.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    public void closeScreen() {
        SwingUtilities.invokeLater(this::dispose);
    }

    /** 포켓몬 한 마리의 상태 카드 — 이름/레벨/타입뱃지/HP바 */
    private static class PokemonCard extends JPanel {
        private final boolean isEnemy;
        private String name = "";
        private int level, hp, maxHp;
        private String type1 = "", type2 = "", status = "";

        PokemonCard(boolean isEnemy) {
            this.isEnemy = isEnemy;
            setOpaque(false);
        }

        void bind(Pokemon p) {
            if (p == null) return;
            this.name   = p.getName();
            this.level  = p.getLevel();
            this.hp     = p.getHp();
            this.maxHp  = p.getMaxHp();
            this.type1  = p.getType1() == null ? "" : p.getType1();
            this.type2  = p.getType2() == null ? "" : p.getType2();
            this.status = p.getStatusEffect() == null ? "" : p.getStatusEffect().getStatus();
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(255, 255, 255, 235));
            g2.fillRoundRect(0, 0, w, h, 14, 14);
            g2.setColor(new Color(80, 80, 80));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

            // 이름 + 레벨
            g2.setColor(INK);
            g2.setFont(new Font("Dialog", Font.BOLD, 18));
            g2.drawString(name, 14, 26);
            g2.setFont(new Font("Dialog", Font.PLAIN, 14));
            g2.drawString("Lv." + level, w - 58, 26);

            // 타입 뱃지 — 단일 타입이면 하나만.
            // Pokemon.getType2()는 타입이 없을 때 null이 아니라 "없음"을 준다 (Pokemon.java:147)
            int bx = 14;
            bx = drawBadge(g2, type1, bx, 36);
            if (hasValue(type2) && !type2.equals(type1)) drawBadge(g2, type2, bx + 6, 36);

            // 상태이상 뱃지 — 정상일 때는 아예 안 그린다 (StatusEffect.NONE == "없음")
            if (hasValue(status)) {
                drawStatusBadge(g2, status, w - 78, 36);
            }

            // HP 바
            int barX = 14, barY = 64, barW = w - 28, barH = 10;
            g2.setColor(new Color(224, 224, 224));
            g2.fillRoundRect(barX, barY, barW, barH, 8, 8);
            double ratio = maxHp <= 0 ? 0 : Math.max(0, Math.min(1.0, hp / (double) maxHp));
            g2.setColor(hpColor(ratio));
            g2.fillRoundRect(barX, barY, (int) (barW * ratio), barH, 8, 8);
            g2.setColor(new Color(120, 120, 120));
            g2.drawRoundRect(barX, barY, barW, barH, 8, 8);

            // 내 포켓몬만 HP 숫자 (원작도 아군만 숫자를 보여줌)
            if (!isEnemy) {
                g2.setColor(INK);
                g2.setFont(new Font("Dialog", Font.PLAIN, 14));
                String t = hp + " / " + maxHp;
                g2.drawString(t, barX + barW - g2.getFontMetrics().stringWidth(t), barY + 26);
            }
            g2.dispose();
        }

        /** "없음"·빈값·null 을 모두 "표시할 것 없음"으로 취급 */
        private boolean hasValue(String s) {
            return s != null && !s.isEmpty() && !s.equals(StatusEffect.NONE) && !s.equals("정상");
        }

        private int drawBadge(Graphics2D g2, String type, int x, int y) {
            if (!hasValue(type)) return x;
            g2.setFont(new Font("Dialog", Font.BOLD, 12));
            int tw = g2.getFontMetrics().stringWidth(type) + 16;
            g2.setColor(TYPE_COLORS.getOrDefault(type, new Color(150, 150, 150)));
            g2.fillRoundRect(x, y, tw, 18, 9, 9);
            g2.setColor(Color.WHITE);
            g2.drawString(type, x + 8, y + 13);
            return x + tw;
        }

        private void drawStatusBadge(Graphics2D g2, String status, int x, int y) {
            g2.setFont(new Font("Dialog", Font.BOLD, 12));
            int tw = g2.getFontMetrics().stringWidth(status) + 14;
            g2.setColor(new Color(70, 70, 70));
            g2.fillRoundRect(x - tw + 64, y, tw, 18, 9, 9);
            g2.setColor(Color.WHITE);
            g2.drawString(status, x - tw + 71, y + 13);
        }

        private Color hpColor(double ratio) {
            if (ratio > 0.5)  return new Color(88, 190, 96);
            if (ratio > 0.2)  return new Color(240, 190, 60);
            return new Color(226, 78, 66);
        }
    }
}
