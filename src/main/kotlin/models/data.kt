package models


// Data classes for CarListing, CarEnrichment, and JoinedCarData
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.serialization.SerializationSchema
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty


class CarListing @JsonCreator constructor() {
    @JsonProperty("carId")
    var carId: Int = 0

    @JsonProperty("make")
    var make: String = ""

    @JsonProperty("model")
    var model: String = ""

    @JsonProperty("year")
    var year: Int = 0

    @JsonProperty("price")
    var price: Int = 0
}

class CarEnrichment @JsonCreator constructor() {
    @JsonProperty("carId")
    var carId: Int = 0

    @JsonProperty("estimatedValue")
    var estimatedValue: Int = 0

    @JsonProperty("accidentHistory")
    var accidentHistory: String = ""

    @JsonProperty("serviceRecords")
    var serviceRecords: Int = 0
}

class JoinedCarData @JsonCreator constructor() {
    @JsonProperty("carId")
    var carId: Int = 0

    @JsonProperty("make")
    var make: String = ""

    @JsonProperty("model")
    var model: String = ""

    @JsonProperty("year")
    var year: Int = 0

    @JsonProperty("price")
    var price: Int = 0

    @JsonProperty("estimatedValue")
    var estimatedValue: Int = 0

    @JsonProperty("accidentHistory")
    var accidentHistory: String = ""

    @JsonProperty("serviceRecords")
    var serviceRecords: Int = 0
}


class CarListingDeserializationSchema : DeserializationSchema<CarListing> {
    private var objectMapper: ObjectMapper? = null

    override fun deserialize(message: ByteArray): CarListing {
        return objectMapper!!.readValue(message)
    }

    override fun isEndOfStream(nextElement: CarListing): Boolean {
        return false
    }

    override fun getProducedType(): org.apache.flink.api.common.typeinfo.TypeInformation<CarListing> {
        return org.apache.flink.api.common.typeinfo.TypeInformation.of(CarListing::class.java)
    }
}
class EnrichedCarDataSerializationSchema : SerializationSchema<JoinedCarData> {
    override fun serialize(element: JoinedCarData): ByteArray {
        val objectMapper = jacksonObjectMapper()
        return objectMapper.writeValueAsBytes(element)
    }
}


class CarEnrichmentDeserializationSchema : DeserializationSchema<CarEnrichment> {
    private var objectMapper: ObjectMapper? = null

    override fun deserialize(message: ByteArray): CarEnrichment {
        return objectMapper!!.readValue(message)
    }

    override fun isEndOfStream(nextElement: CarEnrichment): Boolean {
        return false
    }

    override fun getProducedType(): org.apache.flink.api.common.typeinfo.TypeInformation<CarEnrichment> {
        return org.apache.flink.api.common.typeinfo.TypeInformation.of(CarEnrichment::class.java)
    }
}