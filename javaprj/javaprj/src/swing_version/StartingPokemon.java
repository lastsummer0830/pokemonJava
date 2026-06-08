package swing_version;

public class StartingPokemon {
	String name;
    String type;
    double height;
    double weight;
    String description;

    public StartingPokemon(String name, String type, double height, double weight, String description){
        this.name = name;
        this.type = type;
        this.height = height;
        this.weight = weight;
        this.description = description;
    }

	public void showInfo() {
		// TODO Auto-generated method stub
		BT_Dialog.show("이름: " + name);
        BT_Dialog.show("타입: " + type);
        BT_Dialog.show("키: " + height + "m");
        BT_Dialog.show("몸무게: " + weight + "kg");
        BT_Dialog.show("설명: " + description);
        BT_Dialog.show("---------------------");
	}

	public String getName(){
        return name;
    }

}
