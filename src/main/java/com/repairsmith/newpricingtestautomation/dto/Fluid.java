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
public class Fluid {

    private String name;

    private Double price;

    private String capacityUnit;

    private String capacityUnitAbbreviation;

    private Double capacity;

    private Double minMotorCapacity;

    private Double maxMotorCapacity;

    private String type;

    public Double getTotalPrice() {
        return calculateTotal(capacity);
    }

    public Double getMinTotalPrice() {
        return calculateTotal(minMotorCapacity);
    }

    public Double getMaxTotalPrice() {
        return calculateTotal(maxMotorCapacity);
    }

    private Double calculateTotal(Double capacity) {
        return this.price != null && capacity != null
                ? BigDecimal.valueOf(this.price)
                .multiply(BigDecimal.valueOf(capacity))
                .setScale(2, RoundingMode.HALF_EVEN).doubleValue()
                : null;
    }
}
