import java.net.HttpURLConnection;
import java.net.URL;

public class ConcurrentAccessTask implements Runnable{
	int id;
	public ConcurrentAccessTask(int id) {
		this.id = id;
	}
	
	@Override
	public void run() {
		try {
			// event reservation
			//String url = "http://localhost:8080/saveReservationConc?eventId=78&userName=Daniela&threadId="+id;
			String url = "http://localhost:8080/deleteReservationConc?reservId="+id;
			URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int response = con.getResponseCode();
            System.out.println("GET Response Code :: " + response);
			
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		
	}
	
}
