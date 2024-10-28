    package lab1;
    import com.fastcgi.FCGIInterface;

    import java.io.IOException;
    import java.nio.ByteBuffer;
    import java.nio.charset.StandardCharsets;
    import java.util.HashMap;

    public class Main {
        public static void main(String[] args) {
            var fcgiInterface = new FCGIInterface();
            Validator validator = new Validator();
            String requestBody = "";
            while (fcgiInterface.FCGIaccept()>=0){
                try {
                    requestBody = readRequestBody();
                    HashMap<String, String> map = parseJson(requestBody);
                    System.out.println(map);
                    long startTime = System.nanoTime();
                    var content= "";
                    if (!validator.validate(map)) {
                        content = "{\"error\": \"проблема в пустом запросе или неправильно заполненных данных!\"}";
                    }
                    if(validator.validate(map)){
                        long time = System.nanoTime();
                        if (check(map)){
                            content = sendJson(map,true,time-startTime);
                        }else{
                            content = sendJson(map,false,time-startTime);
                        }
                    }
                    byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
                    var httpResponse = """
                            HTTP/1.1 200 OK
                            Content-Type: application/json
                            Content-Length: %d
                            
                            %s
                            """.formatted(contentBytes.length, content);

                    FCGIInterface.request.outStream.write(httpResponse.getBytes(StandardCharsets.UTF_8));
                    FCGIInterface.request.outStream.flush();
                }catch(IOException e){
                    System.err.println(e.getMessage());
                }
            }
        }
        private static String sendJson(HashMap<String, String> map, boolean hit,long time){
            map.put("result", String.valueOf(hit));
            map.put("time", String.valueOf(time));
            StringBuilder ans = new StringBuilder("{");
            boolean firstEntry = true;
            for (String key : map.keySet()) {
                if (!firstEntry) {
                        ans.append(",");
                }
                ans.append("\"").append(key).append("\":\"").append(map.get(key)).append("\"");
                firstEntry = false;
            }
            ans.append("}");
            return ans.toString();
        }
        private static HashMap<String,String> parseJson(String s1){
            HashMap<String, String> map = new HashMap<>();
            System.out.println("Парсинг проходит успешно");
            s1 = s1.replaceAll("[{}\"]", ""); // Убираем фигурные скобки и кавычки
            for (String s : s1.split(",")) {
                String[] keyValue = s.split(":");
                if (keyValue.length == 2) {
                    map.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
            return map;
        }
        private static String readRequestBody() throws IOException {
            FCGIInterface.request.inStream.fill();
            var contentLength = FCGIInterface.request.inStream.available();
            var buffer = ByteBuffer.allocate(contentLength);
            var readBytes = FCGIInterface.request.inStream.read(buffer.array(), 0, contentLength);
            var requestBodyRaw = new byte[readBytes];
            buffer.get(requestBodyRaw);
            buffer.clear();
            String ans = new String(requestBodyRaw, StandardCharsets.UTF_8);
            return new String(requestBodyRaw, StandardCharsets.UTF_8);
        }
        private static boolean check(HashMap<String,String> map){
            int x = Integer.parseInt(map.get("x"));
            float y = Float.parseFloat(map.get("y"));
            int r = Integer.parseInt(map.get("r"));
            if ((y<=0 && x>=0 && y>=0.5*x - r*0.5) || (x<=0 && x>=-r*0.5 && y>=0 && y<=r)  || (x>=0 && y>=0 && x*x+y*y<r*r*0.25) ){
                return true;
            }else{
                return false;
            }
        }
    }