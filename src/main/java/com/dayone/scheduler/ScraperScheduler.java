package com.dayone.scheduler;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository; // CompanyRepository에 접근하기 위한 멤버 변수
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinaceScraper;

    @Scheduled(fixedDelay = 1000)

    // 일정 시간마다 스크래핑 수행
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}") // application.yml 내 설정한 스케줄링
    public void yahooFinanceSceduling() {
        log.info("scraping scheduler is started"); // 스케줄링 로그
        // 저장된 회사 목록 조회
        List< CompanyEntity> companies = this.companyRepository.findAll();

        // 회사마다 배당금 정보 새로 스크래핑
        for (var company : companies) {
            log.info(company.getName() + " scraping start");
            ScrapedResult scrapedResult = this.yahooFinaceScraper.scrap(Company.builder()
                                                    .name(company.getName())
                                                    .ticker(company.getTicker())
                                                    .build());

            // 스크래핑한 배당금 정보 중 DB에 없는 값은 저장
            scrapedResult.getDividends().stream()
                    // 디비든 모델을 디비든 엔티티로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 디비든 레파지토리에 삽입(존재하지 않는 경우)
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e); // 저장
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지(부하 예방)
            try {
                Thread.sleep(3000); // 3 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
