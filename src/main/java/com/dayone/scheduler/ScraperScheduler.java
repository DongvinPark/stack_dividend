package com.dayone.scheduler;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.YahooFinanceScraper;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableCaching // 스케줄러가 동작할 때마다 @CacheEvict도 같이 동작하면서 캐시 데이터를 전부 비워준다.
@AllArgsConstructor
public class ScraperScheduler {

    /*@Scheduled(cron = "0/5 * * * * *")
    public void test(){
        System.out.println("current time : " + LocalDateTime.now());
    }*/

    private final CompanyRepository companyRepository;
    private final YahooFinanceScraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;


    // 일정 주기마다 실행
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling(){
        log.info("scraping scheduler is started");

        // 저장된 회사 목록 조회 : 추후 데이터 양이 많아질 경우, SpringBatch를 써야될 가능성이 크다.
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for(var company : companies){
            log.info("scrapped company : " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(
                new Company(company.getTicker(), company.getName())
            );

            // 스크래핑한 배당금 정보 중 DB에 없는 것은 저장
            scrapedResult.getDividends().stream().map(
                x -> new DividendEntity(company.getId(), x)
            ).forEach(
                e -> {
                    boolean exist = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                    if(!exist){
                        this.dividendRepository.save(e);
                    }//if
                }
            );//for each

            //연속적으로 스크래핑 대상 서버에 요청을 보내지 않도록 일시정지.
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e){
                e.printStackTrace();
                //현재 스레드에서 문제가 발생했을 시 인터럽트 시켜서 작동을 정지시킴.
                Thread.currentThread().interrupt();
            }
        }//for

    }



}





















