package com.example.payment.monolith.common.config

import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI

@Configuration
@ConditionalOnProperty(name = ["cloud.aws.sqs.enabled"], havingValue = "true", matchIfMissing = false)
class SqsConfiguration {

    @Bean
    fun sqsAsyncClient(sqsConfigProperties: SqsConfigProperties): SqsAsyncClient {
        val builder = SqsAsyncClient.builder()
            .region(Region.of(sqsConfigProperties.region))

        // Add credentials if provided
        if (sqsConfigProperties.accessKey.isNotBlank() && sqsConfigProperties.secretKey.isNotBlank()) {
            val credentials = AwsBasicCredentials.create(
                sqsConfigProperties.accessKey,
                sqsConfigProperties.secretKey
            )
            builder.credentialsProvider(StaticCredentialsProvider.create(credentials))
        }

        // Add custom endpoint if provided (for LocalStack)
        if (sqsConfigProperties.endpoint.isNotBlank()) {
            builder.endpointOverride(URI.create(sqsConfigProperties.endpoint))
        }

        return builder.build()
    }

    @Bean
    fun sqsTemplate(sqsAsyncClient: SqsAsyncClient): SqsTemplate {
        return SqsTemplate.builder()
            .sqsAsyncClient(sqsAsyncClient)
            .build()
    }
}
