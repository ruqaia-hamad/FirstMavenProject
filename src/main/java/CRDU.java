import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class CRDU {
	static String url = "jdbc:sqlserver://localhost:1433;databaseName=MavenApi;encrypt=true;trustServerCertificate=true";
	static String user = "sa";
	static String pass = "root";

	public static void insert() throws IOException, InterruptedException {

		String jsonUrl = "http://universities.hipolabs.com/search?country=United+States";
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(jsonUrl)).build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//	System.out.println(response.body());
		String responsee = response.body();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(responsee);
		String prettyJsonString = gson.toJson(je);
//	System.out.println(prettyJsonString);
		ApiDbs[] attributs = gson.fromJson(prettyJsonString, ApiDbs[].class);

		for (ApiDbs x : attributs) {
			String webpage = x.getWeb_pages()[0];
			String name = x.getName();
			String domian = x.getDomains()[0];
			String state_province = x.getState_province();
			String alpha_two_code = x.getAlpha_two_code();
			String country = x.getCountry();

			String sql = "insert into users(web_pages,state_province, alpha_two_code,name, country,domains)"
					+ " values('" + webpage + "' ,'" + state_province + "', '" + alpha_two_code + "','" + name + "' ,' "
					+ domian + "','" + country + "')";

			Connection con = null;
			try {
				Driver driver = (Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
				DriverManager.registerDriver(driver);
				con = DriverManager.getConnection(url, user, pass);
				Statement st = con.createStatement();

				int m = st.executeUpdate(sql);
				if (m >= 0)
					System.out.println("inserted successfully ");
				else
					System.out.println("insertion failed");

				con.close();
			} catch (Exception ex) {

				System.out.println(ex);
			}
		}
	}

	public static void deleteById()
			throws IOException, InterruptedException, InstantiationException, Throwable, ClassNotFoundException {

		Scanner sc = new Scanner(System.in);

		Connection con = null;
		Statement stmt = null;

		try {
			Driver driver = (Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
			// Registering drivers
			DriverManager.registerDriver(driver);

			// Reference to connection interface
			con = DriverManager.getConnection(url, user, pass);
			System.out.print("Enter the id to be deleted : ");
			int id = sc.nextInt();
			String sql = "DELETE FROM users WHERE id = " + id;
			stmt = con.createStatement();
			stmt.executeUpdate(sql);

			System.out.println("Record deleted successfully");
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public static void update() throws Throwable {

		String sql = "UPDATE users SET web_pages = ?, state_province = ?,alpha_two_code = ?,name = ?, country = ? , domains = ? WHERE id = ?";

		Scanner sc = new Scanner(System.in);
		System.out.print("Enter the id of the row to update: ");
		int id = sc.nextInt();
		Connection con = null;

		try {
			Driver driver = (Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
			// Registering drivers
			DriverManager.registerDriver(driver);

			// Reference to connection interface
			con = DriverManager.getConnection(url, user, pass);
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, "https://oman.edu/");
			pstmt.setString(2, "Muscat");
			pstmt.setString(3, "OM");
			pstmt.setString(4, "UTAS");
			pstmt.setString(5, "OMAN");
			pstmt.setString(6, "OMAN");
			pstmt.setInt(7, id);
			pstmt.executeUpdate();

			String sql2 = "SELECT * FROM users WHERE id = ?";
			PreparedStatement pstmt2 = con.prepareStatement(sql2);
			pstmt2.setInt(1, id);
			ResultSet rs = pstmt2.executeQuery();

			if (rs.next()) {
				String web_pages = rs.getString("web_pages");
				String state_province = rs.getString("state_province");
				String alpha_two_code = rs.getString("alpha_two_code");
				String name = rs.getString("name");
				String country = rs.getString("country");
				String domains = rs.getString("domains");
				System.out.println(id + "\n" + web_pages + "\n " + state_province + "\n " + alpha_two_code + "\n "
						+ name + "\n " + country + "\n" + domains);

			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
}
