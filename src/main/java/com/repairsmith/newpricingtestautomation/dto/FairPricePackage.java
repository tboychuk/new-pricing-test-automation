package com.repairsmith.newpricingtestautomation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FairPricePackage {

    private Long repairId;

    @Builder.Default
    private List<Part> parts = new ArrayList<>();

    @Builder.Default
    private List<Fluid> fluids = new ArrayList<>();

    private Double laborDuration;

    private Double laborTime;

    private Double minLaborTime;

    private Double maxLaborTime;

    private String name;

    private String laborRate;

    private String repairType;

    private Double calculatedTotalCost;

    private Double laborCost;

    private Double partsCost;

    private Double fluidsCost;

    private Double inspectionFee;

    @Getter(AccessLevel.NONE)
    private boolean concierge;

    @Builder.Default
    private List<ConciergeReasonType> conciergeReasons = new ArrayList<>();

    private transient boolean hasPcdb;

    private transient boolean expectedFluids;

    private transient boolean expectedParts;

    private Double minLaborCost;

    private Double maxLaborCost;

    private Double minPartsCost;

    private Double maxPartsCost;

    private Double minFluidsCost;

    private Double maxFluidsCost;

    private Double calculatedMinTotalCost;

    private Double calculatedMaxTotalCost;

    private Boolean rangeExceedsThreshold;

    private String pricingStrategy;

    public boolean isConcierge() {
        return concierge;
    }

    public Double getTotalCost() {
        return this.isConcierge()
                ? null
                : this.getCalculatedTotalCost();
    }

    public boolean hasExpectedFluids() { return this.expectedFluids; }

    public boolean hasExpectedParts() { return this.expectedParts; }
}