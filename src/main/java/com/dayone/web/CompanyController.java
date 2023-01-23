package com.dayone.web;

import com.dayone.model.Company;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.service.CompanyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    private final CacheManager redisCacheManager;



    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(
        @RequestParam String keyword
    ){
        var result = this.companyService.autocomplete(keyword);

        //DB like 연산을 이용한 방법. DB I/O 횟수를 줄일 수 있는 Trie 연산을 쓰기로 한다.
        //var resultFromDBSearch = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(result);
    }



    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable){
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }



    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(
        @RequestBody Company request
    ){
        String ticker = request.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)){
            throw new RuntimeException("ticker is empty");
        }
        Company company = this.companyService.save(ticker);

        //자동완성용 키워드도 이때 같이 저장
        this.companyService.addAutocompleteKeyword(company.getName());

        return ResponseEntity.ok(company);
    }



    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(
        @PathVariable String ticker
    ){
        String companyName = this.companyService.deleteCompany(ticker);

        //캐시에서도 지워야 한다.
        this.clearFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }


    //--------- PRIVATE HELPER METHOD AREA ----------


    private void clearFinanceCache(String companyName){
        this.redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }

}
