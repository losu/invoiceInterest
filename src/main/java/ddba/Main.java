package ddba;

/**
 * Created by ddba on 29/03/2017.
 */
public class Main {

	private String name = "Test" ;

	public String getName() {
		System.out.println(name +" bez param");
		return name;
	}
	private String getName(String str) {
		System.out.println(name +" "+str);
		return name + str;
	}

	public static void main(String[] args) {

	}
}
