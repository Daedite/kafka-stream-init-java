package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(
                StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());
        kafkaProps.put(
                StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());

        try {
            URL resource = App.class.getClassLoader().getResource("stream.properties");
            if (resource == null) {
                throw new IllegalArgumentException("file not found!");
            }
            kafkaProps.load(new FileReader(resource.getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // We first need to use a Kafka Streams class called StreamsBuilder to build our processor topology:
        StreamsBuilder builder = new StreamsBuilder();
        // In this case, the key is empty (Void) and the value is a String type.
        KStream<Void, String> stream = builder.stream("test");
        stream.foreach(
                (key, value) -> {
                    System.out.println("(DSL) Hello, " + value);
                });
//        Properties properties = new Properties();
//        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,"_confluent-controlcenter-5-5-1-1");
//        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"pkc-q283m.af-south-1.aws.confluent.cloud:9092");
//        StreamsConfig config = new StreamsConfig(kafkaProps);

        KafkaStreams streams = new KafkaStreams( builder.build(),kafkaProps);
        streams.start();
// close Kafka Streams when the JVM shuts down (e.g., SIGTERM)
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
