package com.dayone.scraper;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

    private static final long START_TIME = 86400; // 60 * 60 * 24 (초 * 분 * 일)

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000;

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            Connection connection
                    = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = (Elements) document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0);

            Element tbody = tableEle.children().get(1);

            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                // txt: Oct 24, 2019 0.25 Dividend
                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                // for문이 돌 때마다 dividends 리스트에, Dividend 모델을 객체로 한 데이터들이 들어감
                dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));
            }
            scrapedResult.setDividends(dividends);
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }

        return scrapedResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text().split(" - ")[1].trim();
            // abc - def - xzy -> def를 가져오는 셈
            // 사이트 특성 상 -를 사용하여 구분

            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
