package com.appswella.wisepaise.config;

import org.bson.types.Decimal128;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.core.convert.converter.Converter;
import java.math.BigDecimal;
import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                new Converter<BigDecimal, Decimal128>() {
                    @Override
                    public Decimal128 convert(BigDecimal source) {
                        return new Decimal128(source);
                    }
                },
                new Converter<Decimal128, BigDecimal>() {
                    @Override
                    public BigDecimal convert(Decimal128 source) {
                        return source.bigDecimalValue();
                    }
                }
        ));
    }
}