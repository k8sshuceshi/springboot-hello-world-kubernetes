package com.example.springboot;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
@RestController
public class CalculatorController {
        @RequestMapping("/calculate")
        public String calculate() {
                double val = 0.001;
                for (int i = 0; i < 1000000; i++) {
                        val += Math.sqrt(val);
                }
                return "暴力计算中！";
        }
}
