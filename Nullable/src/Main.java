import annotations.English;
import annotations.Metric;
import annotations.NonNull;
import annotations.Nullable;

public class Main {

	@Nullable
	String testField = null;
	@NonNull
	String testField2 = null;
	@Metric
	static double speedMetersPerSecond = 11.2;
	@Metric
	static double distanceToMarsInMeters = 1241251.92;
	@English
	static double speedMilesPerHour = 18536.023;
	@English
	@SuppressWarnings(value = { "TypeChecking" })
	static double distanceToMarsInMiles = 342343;

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

	public double time(@Metric double distance, @Metric double speed) {
		return distance / speed;
	}

	public static void main(@NonNull String[] args) {
		Main m = new Main();
		String temp = m.testField;
		Object o = temp;
		// m.a();
		// m.c("test");
		// m.c(m.b());

		String fromB = m.b();
		// temp = m.testField;
		m.c(fromB);

		m.c((String) o);
		double time = m.time(distanceToMarsInMeters, speedMilesPerHour);
		time = m.time(distanceToMarsInMiles, speedMetersPerSecond);
		time = m.time(distanceToMarsInMiles, speedMilesPerHour);
		time = m.time(distanceToMarsInMeters, speedMetersPerSecond);
		System.out.println(time);
	}
}
