package lab1;
import com.fastcgi.FCGIInterface;  // Пример, замените на реальные имена пакетов и классов

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        var fcgiInterface = new FCGIInterface();
        String requestBody = "";
        while (fcgiInterface.FCGIaccept()>=0){
            try {
                requestBody = readRequestBody();
            }catch(IOException e){
                System.out.println(e);//нужно тут отправлять к клиенту, развернутую инфу об ошибке
            }
            var content= """
                    <html>
                    <head><title>Hello World!</title></head>
                    <body><h1>Hello World!</h1></body>
                    </html>
                    """;
            if (requestBody.isEmpty()){
                content = content.formatted("false");
            }else{
                content = content.formatted("true");
            }
            var httpResponse = """
                    HTTP/1.1 200 OK
                    Content-Type: text/html
                    Content-Length: %d
                    
                    
                    %s
                    """.formatted(content.getBytes(StandardCharsets.UTF_8).length, content);
            try{
                FCGIInterface.request.outStream.write(httpResponse.getBytes(StandardCharsets.UTF_8));
                FCGIInterface.request.outStream.flush();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    private static String readRequestBody() throws IOException {
        FCGIInterface.request.inStream.fill();
        var contentLength = FCGIInterface.request.inStream.available();
        var buffer = ByteBuffer.allocate(contentLength);
        var readBytes =
                FCGIInterface.request.inStream.read(buffer.array(), 0,
                        contentLength);
        var requestBodyRaw = new byte[readBytes];
        buffer.get(requestBodyRaw);
        buffer.clear();
        return new String(requestBodyRaw, StandardCharsets.UTF_8);
    }

}