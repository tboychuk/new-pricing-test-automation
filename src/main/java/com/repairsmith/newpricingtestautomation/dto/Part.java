package com.repairsmith.newpricingtestautomation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Part {
    private String name;

    private Double price;

    private Double minPrice;

    private Double maxPrice;

    private String partNumber;

    private Integer quantity;

    private boolean matchSubModel;

    // Original equipment manufacturer price
    private Double oemPrice;

    // Price with aftermarket discount 1
    private Double amPrice1;

    // Price with aftermarket discount 2
    private Double amPrice2;

    public Double getTotalPrice() {
        return calculateTotal(price);
    }

    public Double getMinTotalPrice() {
        return calculateTotal(minPrice);
    }

    public Double getMaxTotalPrice() {
        return calculateTotal(maxPrice);
    }

    private Double calculateTotal(Double price) {
        return price != null && this.quantity != null
                ? BigDecimal.valueOf(price)
                .multiply(BigDecimal.valueOf(this.quantity))
                .setScale(2, RoundingMode.HALF_EVEN).doubleValue()
                : null;
    }

}
