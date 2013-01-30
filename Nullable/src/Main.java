
public class Main {

	@Nullable String testField = null;
	@NonNull String testField2 = null;
	
	public void a() {
		@Nullable String str = "str";
		c(str);
	}
	
	public String b() {
		String s = null;
		return s;
	}
	
	public void c(@NonNull String data) {
		// Some random code that assumes data is not null

		testField2 = testField;
	}
	
	public static void main(@NonNull String[] args) {
		Main m = new Main();
		String temp = m.testField;
		Object o = temp;
//		m.a();
//		m.c("test");
//		m.c(m.b());
		
		String fromB = m.b();
//		temp = m.testField;
		m.c(fromB);
		
		m.c((String) o);
	}
}
