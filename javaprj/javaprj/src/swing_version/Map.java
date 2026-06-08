package swing_version;

public class Map {
    Location[][] grid;
    int size = 7;

    public Map() {
        grid = new Location[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Location('F', "풀숲");
            }
        }
        grid[1][1] = new Location('T', "마을");
        grid[3][3] = new Location('C', "포켓몬센터");
        grid[5][5] = new Location('G', "체육관");
    }

    public void move(MapPlayer p, char input) {
        if (input == 'W' && p.y > 0) p.y--;
        else if (input == 'S' && p.y < size - 1) p.y++;
        else if (input == 'A' && p.x > 0) p.x--;
        else if (input == 'D' && p.x < size - 1) p.x++;
    }

    public char getTile(int x, int y) {
        return grid[y][x].getType();
    }
}
