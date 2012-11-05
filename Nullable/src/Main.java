
public class Main {

	String testField = null;
	
	public void a() {
		String str = "str";
		c(str);
	}
	
	public String b() {
		String s = null;
		return s;
	}
	
	public void c(@NonNull String data) {
		// Some random code that assumes data is not null
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		String temp = m.testField;
		Object o = temp;
//		m.a();
//		m.c("test");
//		m.c(m.b());
		
//		String temp = m.b();
//		temp = m.testField;
//		m.c(temp);
		
		m.c((String) o);
	}
}
