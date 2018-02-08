package pl.jakubeeee.integration.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @JsonProperty(value = "product_id")
    String id;

    String code;
    String name;

    @JsonProperty(value = "stock")
    StockDetails stockDetails;

    String previousStock;

    public Product(String code, String name, String stock) {
        this.code = code;
        this.name = name;
        this.stockDetails = new StockDetails(stock);
    }

    public String getStock() {
        return stockDetails.getStock();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private class StockDetails {

        private String stock;

    }
}
