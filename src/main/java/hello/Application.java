package hello;

/**
 * Created by cdavis on 2/13/17.
 */

import java.util.Properties;

import com.gemstone.gemfire.cache.GemFireCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.support.GemfireCacheManager;
import org.springframework.util.ObjectUtils;

@EnableCaching
@EnableGemfireRepositories
@SpringBootApplication
@SuppressWarnings("unused")
public class Application implements CommandLineRunner {

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

    @Bean(name = "Quotes")
    LocalRegionFactoryBean<Integer, Integer> quotesRegion(GemFireCache cache) {
        LocalRegionFactoryBean<Integer, Integer> quotesRegion = new LocalRegionFactoryBean<>();
        quotesRegion.setCache(cache);
        quotesRegion.setClose(false);
        quotesRegion.setName("Quotes");
        quotesRegion.setPersistent(false);
        return quotesRegion;
    }

    @Bean
    GemfireCacheManager cacheManager(GemFireCache gemfireCache) {
        GemfireCacheManager cacheManager = new GemfireCacheManager();
        cacheManager.setCache(gemfireCache);
        return cacheManager;
    }

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private QuoteRepository quoteRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        requestQuote(requestQuote(12L).getId());
        requestQuote(10L);
        requestQuote(12L);
    }

    private Quote requestQuote(Long id) {
        long startTime = System.currentTimeMillis();
        Quote quote = doRequestQuote(id);
        long elapsedTime = (System.currentTimeMillis() - startTime);

        boolean cacheMiss = quoteService.isCacheMiss();

        println("\"%1$s\"%nCache Miss [%2$s] - Elapsed Time [%3$s ms]%n", quote, cacheMiss, elapsedTime);
        println("Cached Quotes: ");

        for (Quote quoteElement : quoteRepository.findAll()) {
            println(String.format("\t quote is \"%s\"", quoteElement));
        }

        println("End of Quotes");

        return quote;
    }

    private Quote doRequestQuote(Long id) {
        return (id != null ? quoteService.requestQuote(id) : quoteService.requestRandomQuote());
    }

    private void println(String message, Object... args) {
        if (ObjectUtils.isEmpty(args)) {
            System.out.println(message);
            System.out.flush();
        }
        else {
            System.out.printf(message, args);
            System.out.flush();
        }
    }
}
