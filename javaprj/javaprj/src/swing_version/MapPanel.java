package swing_version;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MapPanel extends JPanel {

    private final Map map;
    private final MapPlayer player;
    private BufferedImage playerIcon;

    public MapPanel(Map map, MapPlayer player) {
        this.map = map;
        this.player = player;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(900, 560));
        setFocusable(true);

        try {
            playerIcon = ImageIO.read(getClass().getResourceAsStream("/swing_version/player_icon.png"));
        } catch (Exception e) {
            playerIcon = null;
            System.out.println("플레이어 아이콘 로드 실패: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int rows = map.size;
        int cols = map.size;
        int cellWidth = getWidth() / cols;
        int cellHeight = getHeight() / rows;

        g.setFont(new Font("Dialog", Font.BOLD, 20));

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int drawX = x * cellWidth;
                int drawY = y * cellHeight;

                char tile = map.grid[y][x].getType();
                if (tile == 'F') g.setColor(new Color(200, 255, 200));
                else if (tile == 'T') g.setColor(new Color(255, 245, 200));
                else if (tile == 'C') g.setColor(new Color(255, 220, 220));
                else if (tile == 'G') g.setColor(new Color(220, 235, 255));
                else g.setColor(Color.LIGHT_GRAY);

                g.fillRect(drawX, drawY, cellWidth, cellHeight);
                g.setColor(Color.GRAY);
                g.drawRect(drawX, drawY, cellWidth, cellHeight);

                String text = "";
                if (tile == 'F') text = "🌳";
                else if (tile == 'T') text = "🏠";
                else if (tile == 'C') text = "🏥";
                else if (tile == 'G') text = "🏟";

                g.drawString(text, drawX + cellWidth / 2 - 10, drawY + cellHeight / 2);

                if (player.x == x && player.y == y) {
                    if (playerIcon != null) {
                        int iconSize = Math.min(cellWidth, cellHeight) - 4;
                        g.drawImage(playerIcon,
                                drawX + cellWidth / 2 - iconSize / 2,
                                drawY + cellHeight / 2 - iconSize / 2,
                                iconSize, iconSize, this);
                    } else {
                        g.setColor(Color.RED);
                        g.fillOval(drawX + cellWidth / 2 - 15, drawY + cellHeight / 2 - 15, 30, 30);
                    }
                }
            }
        }
    }
}
