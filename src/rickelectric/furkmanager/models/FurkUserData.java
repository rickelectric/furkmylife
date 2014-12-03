package rickelectric.furkmanager.models;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.models.URI_Enums.Prefs_Flags;
import rickelectric.furkmanager.network.api.API_UserData;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager.ChartType;

public class FurkUserData {

	public static class DownloadsUsage {

		static int numAdding, numDownloading, downLimit, numAdded, isLimited;

		static long downSizeLimit, maxBandwidthSizeDiff, addedSize, downSize,
				bandwidthSizeDiff;

		public static void load(JSONObject j) {
			numAdding = j.getInt("adding_cnt");
			numDownloading = j.getInt("dls_cnt");
			downLimit = j.getInt("dls_cnt_limit");
			numAdded = j.getInt("added_cnt");
			isLimited = j.getInt("is_limited");

			downSizeLimit = j.getLong("dls_size_limit");
			maxBandwidthSizeDiff = j.getLong("dls_max_size_bw_diff");
			addedSize = j.getLong("added_size");
			downSize = j.getLong("dls_size");
			bandwidthSizeDiff = j.getLong("size_bw_diff");

		}

	}

	public static class User {

		public static URI_Enums.URI_Port dlPort;// enum.eval(dl_uri_port);
		public static String signupDate;// ctime
		public static URI_Enums.URI_Host dlHost;// enum.eval(dl_uri_host);
		public static int emailVerified;

		public static Prefs_Flags flags;// enum.eval(prefs_flags);
		public static URI_Enums.URI_Scheme dlScheme;// enum.eval(dl_uri_scheme);

		public static String login;
		public static String email;
		public static int proxyGroupID;// eval(id_dl_proxies_groups);
		public static int uriKey;// dl_uri_key

		public static String jsonPreferences;// prefs_json;

		public static void save() {
			API_UserData.saveUserPrefs();
		}

		public static void save(final Runnable after) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					save();
					after.run();
				}
			}).start();
		}

		public static void load(JSONObject j) {
			dlPort = URI_Enums.URI_Port.eval(j.getString("dl_uri_port"));
			signupDate = j.getString("ctime");
			dlHost = URI_Enums.URI_Host.eval(j.getString("dl_uri_host"));
			emailVerified = j.getInt("email_is_ver");

			flags = URI_Enums.Prefs_Flags.eval(j.getString("prefs_flags"));

			dlScheme = URI_Enums.URI_Scheme.eval(j.getString("dl_uri_scheme"));
			login = j.getString("login");
			email = j.getString("email");
			try {
				proxyGroupID = j.getInt("id_dl_proxies_groups");
			} catch (Exception e) {
				proxyGroupID = 0;
			}
			uriKey = j.getInt("dl_uri_key");

			jsonPreferences = j.getString("prefs_json");
		}

	}

	public static class BandwidthStats {

		public static class BWStat {
			long bytes, identifier;
			String date;

			public BWStat() {
				this.bytes = -1;
				this.identifier = -1;
				this.date = null;
			}

			public void set(long ts, long bytes, String date) {
				this.identifier = ts;
				this.bytes = bytes;
				this.date = date;
			}

			public BWStat(JSONObject o) {
				long ts = o.getLong("ts");
				long bytes = o.getLong("bytes");

				String date = null;
				try {
					date = o.getString("dt");
				} catch (Exception e) {
				}

				set(ts, bytes, date);
			}

		}

		public static class SubnetStat extends BWStat {
			public int numUsers;

			// date and numUsers may be null if bytes=0

			public void set(long ts, long bytes, String date, int numUsers) {
				set(ts, bytes, date);
				this.numUsers = numUsers;
			}

			public SubnetStat(JSONObject o) {
				super(o);

				int numUsers = 0;
				try {
					numUsers = o.getInt("num_users");
				} catch (Exception e) {
				}

				this.numUsers = numUsers;

			}
		}

		public static ArrayList<BWStat> bandwidthMonthly;
		public static ArrayList<SubnetStat> subnetMonthly;

		public static void load(JSONArray bandwidth, JSONArray subnet) {
			bandwidthMonthly = new ArrayList<BWStat>();
			for (int x = 0; x < bandwidth.length(); x++) {
				JSONObject bwStat = bandwidth.getJSONObject(x);
				BWStat bw = new BWStat(bwStat);

				bandwidthMonthly.add(bw);
			}

			subnetMonthly = new ArrayList<SubnetStat>();
			for (int x = 0; x < subnet.length(); x++) {
				JSONObject subStat = subnet.getJSONObject(x);
				SubnetStat s = new SubnetStat(subStat);

				subnetMonthly.add(s);
			}
		}

		private static int numDaysIn(int year,int month){
			if(month==9||month==4||month==6||month==11) return 30;
			if(month==2){
				if(isLeapYear(year)) return 29;
				return 28;
			}
			return 31;
		}

		private static boolean isLeapYear(int year) {
			if (year / 400 == 1) {
				return true;
			} else if (year / 100 == 1) {
				return false;
			} else if (year / 4 == 1) {
				return true;
			} else {
				return false;
			}
		}
		
		private static String printDate(int day,int mth,int year){
		     String[] dName={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		     String suffix="th";
		     
		     if(!(day>10&&day<=20)){
		        if(day%10==1) suffix="st";
		        else if(day%10==2) suffix="nd";
		        else if(day%10==3) suffix="rd";
		     }
		     
		     return dName[mth-1]+" "+day+suffix+", "+year;
		}

		public static Chart getChart() {
			int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
			int year = Calendar.getInstance().get(Calendar.YEAR);
			
			String start = printDate(day-1, month, year);
			
			ArrayList<String> x = new ArrayList<String>();
			ArrayList<Integer> y = new ArrayList<Integer>();
			ArrayList<Integer> y2 = new ArrayList<Integer>();
			
			day+=1;
			for (int i=0;i<bandwidthMonthly.size();i++) {
				BWStat user = bandwidthMonthly.get(i);
				SubnetStat sub = subnetMonthly.get(i);
				y.add((int) (sub.bytes / 1000000));
				y2.add((int) (user.bytes / 1000000));
				
				day--;
				if(day==0){
					month--;
					if(month==0){
						month=12;
						year--;
					}
					day=numDaysIn(year,month);
				}
				x.add(day+"");
			}
			
			String end = printDate(day, month, year);
			
			Collections.reverse(x);
			Collections.reverse(y);
			Collections.reverse(y2);
			
			Chart chart = new ChartBuilder().chartType(ChartType.Bar)
					.width(800).height(600).title("Bandwidth Stats ("+end+" to "+start+")")
					.xAxisTitle("Time (Dates)").yAxisTitle("Data Used (MB)").build();
			chart.addSeries("Subnet", x, y);
			chart.addSeries("User", x, y2);

			chart.getStyleManager().setYAxisMin(0);
			chart.getStyleManager().setAxisTickLabelsFont(new Font(Font.DIALOG,Font.PLAIN,9));
			// chart.getStyleManager().setYAxisMax(70);

			return chart;
		}
	}
}
