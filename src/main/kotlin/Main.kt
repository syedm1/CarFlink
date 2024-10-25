import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.serialization.SerializationSchema
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import models.*
import java.util.Properties

fun main() {
    val logger = LoggerFactory.getLogger("FlinkKafkaConsumer")
    val env = StreamExecutionEnvironment.getExecutionEnvironment()

    val kafkaProperties = createKafkaProperties()

    val carListingsDeserializationSchema = CarListingDeserializationSchema()
    val carEnrichmentDeserializationSchema = CarEnrichmentDeserializationSchema()

    val carListingsStream = createCarListingsStream(env, kafkaProperties, carListingsDeserializationSchema)
    val carEnrichmentStream = createCarEnrichmentStream(env, kafkaProperties, carEnrichmentDeserializationSchema)

    val transformedCarListingsStream = transformCarListings(carListingsStream)
    val joinedStream = joinCarListingsWithEnrichment(transformedCarListingsStream, carEnrichmentStream)

    // Use FlinkKafkaProducer to send joined stream data to the Kafka topic
    joinedStream.addSink(
        FlinkKafkaProducer(
            "flink-car-enriched-data",
            EnrichedCarDataSerializationSchema(),
            kafkaProperties
        )
    )

    env.execute("Flink Kotlin Kafka Consumer")
}

fun createKafkaProperties(): Properties {
    return Properties().apply {
        put("bootstrap.servers", "localhost:29092")
        put("group.id", "flink-kotlin-group")
    }
}

fun createCarListingsStream(env: StreamExecutionEnvironment, kafkaProperties: Properties, deserializationSchema: DeserializationSchema<CarListing>): DataStream<CarListing> {
    val carListingsConsumer = FlinkKafkaConsumer("car-listings", deserializationSchema, kafkaProperties)
    return env.addSource(carListingsConsumer)
}

fun createCarEnrichmentStream(env: StreamExecutionEnvironment, kafkaProperties: Properties, deserializationSchema: DeserializationSchema<CarEnrichment>): DataStream<CarEnrichment> {
    val carEnrichmentConsumer = FlinkKafkaConsumer("car-enrichment", deserializationSchema, kafkaProperties)
    return env.addSource(carEnrichmentConsumer)
}

fun transformCarListings(carListingsStream: DataStream<CarListing>): DataStream<CarListing> {
    return carListingsStream.map(object : RichMapFunction<CarListing, CarListing>() {
        override fun open(parameters: Configuration) {
            // Initialization logic
        }

        override fun map(value: CarListing): CarListing {
            val discount = if (value.make == "Toyota" || value.make == "Ford") 500 else 0
            return CarListing().apply {
                carId = value.carId
                make = value.make
                model = value.model
                year = value.year
                price = value.price - discount
            }
        }
    })
}

fun joinCarListingsWithEnrichment(
    carListingsStream: DataStream<CarListing>,
    carEnrichmentStream: DataStream<CarEnrichment>
): DataStream<JoinedCarData> {
    return carListingsStream.keyBy { it.carId }
        .connect(carEnrichmentStream.keyBy { it.carId })
        .process(object : KeyedCoProcessFunction<Int, CarListing, CarEnrichment, JoinedCarData>() {
            override fun processElement1(
                carListing: CarListing,
                ctx: Context,
                out: Collector<JoinedCarData>
            ) {
                // Process car listing data and enrich
            }

            override fun processElement2(
                carEnrichment: CarEnrichment,
                ctx: Context,
                out: Collector<JoinedCarData>
            ) {
                // Process car enrichment data and emit joined data
            }
        })
}
