package rickelectric.furkmanager.models;

public class FurkDate {

	public int year, month, day, hour, min, sec;

	public FurkDate(String date) {
		year = 2000;
		month = day = 1;
		hour = min = sec = 0;
		parseFDate(date);
	}

	private void parseFDate(String date) {
		if (date == null)
			return;
		String[] d = date.split(" ");
		parseSQLDate(d[0]);
		if (d.length != 2)
			return;

		String[] hms = d[1].split(":");
		hour = Integer.parseInt(hms[0]);
		min = Integer.parseInt(hms[0]);
		sec = Integer.parseInt(hms[0]);
	}

	private void parseSQLDate(String date) {
		String[] ymd = date.split("-");
		year = Integer.parseInt(ymd[0]);
		month = Integer.parseInt(ymd[0]);
		day = Integer.parseInt(ymd[0]);
	}

	public String toMySQLString() {
		return String.format("%4d-%2d-%2d", year, month, day);
	}

	public String toString() {
		return String.format("%4d-%2d-%2d %2d:%2d:%2d", year, month, day, hour,
				min, sec);
	}

}
