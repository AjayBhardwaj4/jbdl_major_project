package com.bhardwaj.services.epay.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.Properties;

@Configuration
public class KafkaConfig {

    Properties getProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return properties;
    }

    @Bean
    ConsumerFactory getConsumerFactory() {
        return new DefaultKafkaConsumerFactory(getProperties());
    }


    ConcurrentKafkaListenerContainerFactory<String, String> getListener() {
        ConcurrentKafkaListenerContainerFactory listener = new ConcurrentKafkaListenerContainerFactory();
        listener.setConsumerFactory(getConsumerFactory());

        return listener;
    }

    ProducerFactory getProducerFactory() {
        return new DefaultKafkaProducerFactory(getProperties());
    }

    @Bean
    KafkaTemplate<String, String> getKafkaTemplate() {
        return new KafkaTemplate<>(getProducerFactory());
    }
}
