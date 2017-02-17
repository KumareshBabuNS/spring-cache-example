package hello;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;
import org.springframework.util.ObjectUtils;

/**
 * Created by cdavis on 2/13/17.
 */
@Region("Quotes")
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class Quote {

    @Id
    private Long id;

    private String quote;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Quote)) {
            return false;
        }

        Quote that = (Quote) obj;

        return ObjectUtils.nullSafeEquals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        int hashValue = 17;
        hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getId());
        return hashValue;
    }

    @Override
    public String toString() {
        //return getQuote();
        return String.format("%d - Quote is \"%s\"", getId(), getQuote());
    }
}
