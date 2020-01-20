package com.repairsmith.newpricingtestautomation;

import com.repairsmith.newpricingtestautomation.dto.FairPricePackage;
import com.repairsmith.newpricingtestautomation.dto.FairPricePackages;
import com.repairsmith.newpricingtestautomation.dto.Fluid;
import com.repairsmith.newpricingtestautomation.dto.Part;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.IntStream;

//@SpringBootApplication
public class NewPricingTestAutomationApplication {
    private static RestTemplate restTemplate = new RestTemplate();
    private static ConcurrentHashMap<Integer, List<String>> messagesMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//                CompletableFuture completableFuture = performRequest(12);
//        completableFuture.get();
        IntStream.rangeClosed(1, 194)
                .parallel()
                .forEach(NewPricingTestAutomationApplication::performRequest);
        Thread.sleep(500);

        messagesMap.keySet().stream().sorted().forEach(repairId -> {
            System.out.println("\n--------------------------------------------");
            System.out.print(repairId + ".");
            messagesMap.get(repairId).forEach(System.out::println);
        });
        System.out.println("--------------------------------------------\n");
    }

    private static CompletableFuture performRequest(int repairId) {
//        String oldUri = "http://localhost:9010/v2/fair/price?year=2008&makeid=31&model=X5&modelid=213&submodelid=1596&engineid=8051&repairids=" + repairId;
        String oldUri = "http://localhost:9010/v2/fair/price?year=2016&makeid=54&model=F-150&modelid=666&submodelid=656&engineid=13809&repairids=" + repairId;
//        String oldUri = "http://localhost:9010/v2/fair/price?year=2019&makeid=58&model=MDX&modelid=744&submodelid=20&engineid=20422&repairids=" + repairId;
//        String oldUri = "http://localhost:9010/v2/fair/price?year=2017&makeid=63&model=E300&modelid=829&submodelid=850&repairids=" + repairId;
//        String newUri = "http://localhost:9011/v2/fair/price?year=2008&makeid=31&model=X5&modelid=213&submodelid=1596&engineid=8051&repairids=" + repairId;
        String newUri = "http://localhost:9011/v2/fair/price?year=2016&makeid=54&model=F-150&modelid=666&submodelid=656&engineid=13809&repairids=" + repairId;
//        String newUri = "http://localhost:9011/v2/fair/price?year=2019&makeid=58&model=MDX&modelid=744&submodelid=20&engineid=20422&repairids=" + repairId;
//        String newUri = "http://localhost:9011/v2/fair/price?year=2017&makeid=63&model=E300&modelid=829&submodelid=20&repairids=" + repairId;
        return CompletableFuture.supplyAsync(() -> getPricing(oldUri))
                .thenAcceptBoth(CompletableFuture.supplyAsync(() -> getPricing(newUri)), (oldPrice, newPrice) -> processPrices(oldPrice, newPrice, repairId));
    }

    private static Optional<FairPricePackages> getPricing(String uri) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(uri, FairPricePackages.class));
        } catch (Exception e) {
            System.err.println("Error with request =" + uri);
            return Optional.empty();
        }
    }

    private static void processPrices(Optional<FairPricePackages> oldPrice, Optional<FairPricePackages> newPrice, int repairId) {
        Double oldTotalPrice = toTotalCost(oldPrice);
        Double newTotalPrice = toTotalCost(newPrice);
        if (!Objects.equals(oldTotalPrice, newTotalPrice)) {
            log(repairId, String.format("PRICES ARE DIFFERENT: %s vs %s %n for %s (id = %d)", oldTotalPrice, newTotalPrice, repairName(newPrice), repairId));
            printPrices(oldPrice, newPrice, repairId);
        }
    }

    private static void log(int repairId, String message) {
        List<String> messages = messagesMap.getOrDefault(repairId, new ArrayList<>());
        messages.add(message);
        messagesMap.put(repairId, messages);
    }

    private static void printPrices(Optional<FairPricePackages> oldPrice, Optional<FairPricePackages> newPrice, int repairId) {
        compare("Labor Rate", oldPrice, newPrice, FairPricePackage::getLaborRate, repairId);
        compare("Labor Time", oldPrice, newPrice, FairPricePackage::getLaborTime, repairId);
        compare("Part Unit Price", oldPrice, newPrice, fairPricePackage -> fairPricePackage.getParts().stream().findFirst().map(Part::getPrice).orElse(null), repairId);
        compare("Total Part Price", oldPrice, newPrice, fairPricePackage -> fairPricePackage.getParts().stream().findFirst().map(Part::getTotalPrice).orElse(null), repairId);
        compare("Fluid Capacity", oldPrice, newPrice, fairPricePackage -> fairPricePackage.getFluids().stream().findFirst().map(Fluid::getCapacity).orElse(null), repairId);
        compare("Capacity Unit", oldPrice, newPrice, fairPricePackage -> fairPricePackage.getFluids().stream().findFirst().map(Fluid::getCapacityUnit).orElse(null), repairId);
    }

    private static void compare(String fieldName, Optional<FairPricePackages> oldPrice, Optional<FairPricePackages> newPrice, Function<FairPricePackage, ?> valueExtractor, int repairId) {
        Object oldValue = extract(oldPrice, valueExtractor);
        Object newValue = extract(newPrice, valueExtractor);
        if (!Objects.equals(oldPrice, newPrice)) {
            log(repairId, String.format("%s: %s vs %s", fieldName, oldValue, newValue));
        }
    }

    private static <T> T extract(Optional<FairPricePackages> optionalFairPricePackages, Function<FairPricePackage, T> valueExtractor) {
        return getPackage(optionalFairPricePackages).map(valueExtractor).orElse(null);
    }

    private static String repairName(Optional<FairPricePackages> optionalFairPricePackages) {
        return getPackage(optionalFairPricePackages).map(FairPricePackage::getName).orElseThrow();
    }

    private static Optional<FairPricePackage> getPackage(Optional<FairPricePackages> optionalFairPricePackages) {
        return optionalFairPricePackages
                .flatMap(pricePackages -> pricePackages.getPackages().stream().findFirst());
    }

    private static Double toTotalCost(Optional<FairPricePackages> pricePackages) {
        return getPackage(pricePackages)
                .map(FairPricePackage::getTotalCost)
                .orElse(null);
    }

}
