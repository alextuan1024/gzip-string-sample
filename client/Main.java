import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

public class Main {

    private static String uncompress(String s) throws IOException {

        //base64 decode
        byte[] byteArray = Base64.getDecoder().decode(s);
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);

        //gzip解压
        GZIPInputStream gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        gis.close();
        bis.close();

        //使用latin1字符集获得bytes
        byte[] latin1 = sb.toString().getBytes("ISO_8859_1");
        //转换回GBK
        return new String(latin1, "GBK");
    }

    public static void main(String[] args) throws IOException {
        //
        URL url = new URL("http://localhost:8080");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        con.setConnectTimeout(5000);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        con.disconnect();
        String s = content.toString();

        String result = uncompress(s);
        System.out.println(result);
    }
}
