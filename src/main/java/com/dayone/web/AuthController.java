package com.dayone.web;

import com.dayone.scraper.YahooFinanceScraper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final YahooFinanceScraper yahooFinanceScraper;

    @GetMapping("/run")
    public void test(){
        yahooFinanceScraper.scrap(null);
    }

}
