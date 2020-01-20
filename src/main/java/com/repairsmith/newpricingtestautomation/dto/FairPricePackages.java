package com.repairsmith.newpricingtestautomation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FairPricePackages {

    @Builder.Default
    private Set<FairPricePackage> packages = new HashSet<>();
}
