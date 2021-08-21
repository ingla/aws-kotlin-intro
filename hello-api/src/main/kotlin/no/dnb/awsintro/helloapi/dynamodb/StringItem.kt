package no.dnb.awsintro.helloapi.dynamodb

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import kotlinx.serialization.Serializable

@Serializable
@DynamoDBTable(tableName="sand-IL18-StringCatalog")
data class StringItem(
    @DynamoDBHashKey
    var stringKey: String = "",

    @DynamoDBAttribute
    var stringValue: String = ""
)