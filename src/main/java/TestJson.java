import com.google.gson.*;

import java.io.BufferedReader;
import java.io.FileReader;

public class TestJson {
	
	public static void main(String[] args) {
		System.out.println("Done");
        try {
            FileReader fileReader = new FileReader("conf/jsonString");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder jsonString = new StringBuilder();
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                jsonString.append(currentLine);
            }

            JsonObject jsonObject = new JsonParser().parse(jsonString.toString()).getAsJsonObject();
            System.out.println("done");

        }
        catch(Exception e) {
            e.printStackTrace();
        }
	}
}