package com.example.payment.monolith.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "cloud.aws")
data class SqsConfigProperties(
    var sqs: SqsProperties = SqsProperties(),
    var credentials: CredentialsProperties = CredentialsProperties()
) {
    data class SqsProperties(
        var enabled: Boolean = false,
        var region: String = "ap-northeast-2",
        var endpoint: String = ""
    )

    data class CredentialsProperties(
        var accessKey: String = "",
        var secretKey: String = ""
    )

    val region: String
        get() = sqs.region

    val endpoint: String
        get() = sqs.endpoint

    val accessKey: String
        get() = credentials.accessKey

    val secretKey: String
        get() = credentials.secretKey
}
