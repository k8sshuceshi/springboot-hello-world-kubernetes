package com.example.springboot;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
public class BreakerController {
        @RequestMapping(value = "/app_breaker", method = RequestMethod.GET)
        public ResponseEntity<Map<String,Object>> appBreaker() throws IOException{
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("message", "这是一个模拟了 500 内部服务器出错的地址");
                return new ResponseEntity<Map<String,Object>>(map,
                                HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
