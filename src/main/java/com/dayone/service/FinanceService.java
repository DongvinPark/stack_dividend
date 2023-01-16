package com.dayone.service;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.CompanyRepository;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.persist.entity.DividendRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;



    public ScrapedResult getDividendByCompanyName(String companyName){
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company =  this.companyRepository.findByName(companyName)
                               .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));

        // 2. 주회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조회 후 반환
        return new ScrapedResult(
            Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build() ,
            dividendEntities.stream().map(
                x -> Dividend.builder()
                    .date(x.getDate())
                    .dividend(x.getDividend())
                    .build()
            ).collect(Collectors.toList())
        );
    }

}
