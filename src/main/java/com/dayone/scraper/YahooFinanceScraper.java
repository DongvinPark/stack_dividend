package com.dayone.scraper;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.Month;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class YahooFinanceScraper implements Scraper {

    //이것은 실제 결과물을 받아올 수 있는 url이다.
    //"https://finance.yahoo.com/quote/COKE/history?period1=99100800&period2=1673568000&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true"

    //물론 이것만으로는 코카콜라 라는 하나의 화사의 페이지만을 받을 수 있으므로, RequestParam을 스프링 앱 내에서 수정을 해 가면서 호출할 수 있도록 아래의 값을 url 멤버변수로 선언해야 한다. %s, %d 등의 포맷관련 단어들이 나온 것을 확인할 수 있다.

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";

    private static final String SUMMARY = "https://finance.yahoo.com/quote/%s?p=%s";

    private static final long START_TIME = 60*60*24;// 초 * 분 * 시

    @Override
    public ScrapedResult scrap(Company company){
        var scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);

        try{
            long now = System.currentTimeMillis() / 1000;// 밀리세컨드 단위를 그냥 초 단위로 바꾼다.
            String formattedUrl = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);

            Connection connection = Jsoup.connect(formattedUrl);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0);

            Element tbody = tableEle.children().get(1);

            List<Dividend> dividends = new ArrayList<>();

            for(Element e : tbody.children()){
                String txt = e.text();
                if(!txt.endsWith("Dividend")){
                    continue;
                }
                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",",""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if(month < 0){
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(
                    Dividend.builder()
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(dividend)
                        .build()
                );
                //System.out.println(year + "/" + month + "/" + day + " -> " + dividend);
            }//for

            scrapedResult.setDividends(dividends);

        } catch (IOException e){
            e.printStackTrace();
        }

        return scrapedResult;
    }//end of scrap()




    @Override
    public Company scrapCompanyByTicker(String ticker){
        String url = String.format(SUMMARY, ticker, ticker);

        try{
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text().split(" - ")[1].trim();

            return Company.builder()
                .ticker(ticker)
                .name(title)
                .build();
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

}























