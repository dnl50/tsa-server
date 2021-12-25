package dev.mieser.tsa.signing.serial;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.util.function.Supplier;

/**
 * {@link SerialNumberGenerator} generating random serial numbers. There is a low chance that the same serial is generated twice.
 */
@RequiredArgsConstructor
public class RandomSerialNumberGenerator implements SerialNumberGenerator {

    private final Supplier<Long> randomValueSupplier;

    public RandomSerialNumberGenerator() {
        this(() -> new SecureRandom().nextLong());
    }

    @Override
    public long generateSerialNumber() {
        return randomValueSupplier.get();
    }

}
