package com.dayone.service;

import com.dayone.exception.impl.NoCompanyException;
import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;


    //RedisTemplate<K,V>를 사용하지 않는 새로운 방법이었다.
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName){
        log.info("search company -> " + companyName);
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company =  this.companyRepository.findByName(companyName)
                               .orElseThrow(NoCompanyException::new);

        // 2. 주회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조회 후 반환
        return new ScrapedResult(
            new Company(company.getTicker(), company.getName())
            ,
            dividendEntities.stream().map(
                dividendEntity -> new Dividend(dividendEntity.getDate(), dividendEntity.getDividend())
            ).collect(Collectors.toList())
        );
    }

}
