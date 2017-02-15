package hello;

/**
 * Created by cdavis on 2/13/17.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.support.GemfireCacheManager;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.GemFireCache;

@EnableGemfireRepositories
@Configuration
@EnableAutoConfiguration
@SpringBootApplication
@EnableCaching
@SuppressWarnings("unused")
public class Application implements CommandLineRunner {

    @Bean
    QuoteService quoteService(QuotesRepository quotesRepository) {
        return new QuoteService(quotesRepository);
    }

    @Bean
    Properties gemfireProperties() {
        Properties gemfireProperties = new Properties();
        gemfireProperties.setProperty("name", "DataGemFireCachingApplication");
        gemfireProperties.setProperty("mcast-port", "0");
        gemfireProperties.setProperty("log-level", "config");
        return gemfireProperties;
    }

    @Bean
    CacheFactoryBean gemfireCache() {
        CacheFactoryBean gemfireCache = new CacheFactoryBean();
        gemfireCache.setClose(true);
        gemfireCache.setProperties(gemfireProperties());
        return gemfireCache;
    }

    @Bean
    LocalRegionFactoryBean<Integer, Integer> quotesRegion(GemFireCache cache) {
        LocalRegionFactoryBean<Integer, Integer> quotesRegion = new LocalRegionFactoryBean<>();
        quotesRegion.setCache(cache);
        quotesRegion.setClose(false);
        quotesRegion.setName("Quotes");
        quotesRegion.setPersistent(false);
        return quotesRegion;
    }

    @Autowired
    QuotesRepository quotesRepository;

    @Bean
    GemfireCacheManager cacheManager(Cache gemfireCache) {
        GemfireCacheManager cacheManager = new GemfireCacheManager();
        cacheManager.setCache(gemfireCache);
        return cacheManager;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Quote quote = requestQuote(12l);
        requestQuote(quote.getId());
        requestQuote(10l);
        requestQuote(12l);
    }

    private Quote requestQuote(Long id) {
        QuoteService quoteService = quoteService(quotesRepository);
        long startTime = System.currentTimeMillis();
        Quote quote = (id != null ? quoteService.requestQuote(id) : quoteService.requestRandomQuote());
        long elapsedTime = System.currentTimeMillis();
        System.out.printf("\"%1$s\"%nCache Miss [%2$s] - Elapsed Time [%3$s ms]%n", quote,
                quoteService.isCacheMiss(), (elapsedTime - startTime));

        Iterable<Quote> quotes = quotesRepository.findAll();
        System.out.println("Quotes: ");
        for (Quote q : quotesRepository.findAll()) {
            System.out.println("\t quote: " + q);
        }
        System.out.println("end of Quotes: ");

        return quote;
    }

}