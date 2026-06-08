package swing_version;

public class Location {
    char type;
    String name;

    public Location(char type, String name) {
        this.type = type;
        this.name = name;
    }

    public char getType() { return type; }
    public String getName() { return name; }
}
