package com.dayone.service;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@AllArgsConstructor
@Slf4j
public class CompanyService {

    //IDE 에서 자꾸 Raw Type Warning 을 던져서 타입 명시해줌.
    private final Trie<String, String> trie;
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;



    public Company save(String ticker){
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if(exists){
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }



    public Page<CompanyEntity> getAllCompany(Pageable pageable){
        return this.companyRepository.findAll(pageable);
    }



    public void addAutocompleteKeyword(String keyword){
        this.trie.put(keyword, null);
    }



    public List<String> autocomplete(String keyword){
        return new ArrayList<>(this.trie.prefixMap(keyword).keySet());
    }



    public List<String> getCompanyNamesByKeyword(String keyword){
        Pageable limit = PageRequest.of(0,10);
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream().map(
            e -> e.getName()
        ).collect(Collectors.toList());
    }



    public String deleteCompany(String ticker){
        var company = this.companyRepository.findByTicker(ticker).orElseThrow(
            () -> new RuntimeException("존재하지 않는 회사입니다.")
        );

        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);

        //트라이에 저장해뒀던 회사 이름도 삭제해야 한다.
        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }



    public void deleteAutocompleteKeyword(String keyword){
        this.trie.remove(keyword);
    }



    //-------------- PRIVATE HELPER METHOD AREA -----------



    private Company storeCompanyAndDividend(String ticker){
        // ticker를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if(ObjectUtils.isEmpty(company)){
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스프래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));

        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream().map(x -> new DividendEntity(
            companyEntity.getId(), x)).collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }

}
