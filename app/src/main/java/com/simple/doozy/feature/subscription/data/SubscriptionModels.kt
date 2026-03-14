package com.simple.doozy.feature.subscription.data

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class SubscriptionPlan(
    val id: String,
    val name: String,
    val priceInPaise: Int
)

@Serializable
sealed interface SubscriptionData {
    val lastSyncTimestamp: Long

    @Serializable
    data class NoSubscription(override val lastSyncTimestamp: Long = 0L) : SubscriptionData

    @Serializable
    data class Active(
        val plan: SubscriptionPlan,
        val subscribedOn: Instant,
        val billingDate: Instant,
        val expiresOn: Instant,
        override val lastSyncTimestamp: Long = 0L
    ) : SubscriptionData {
        companion object {
            val nowMs = System.currentTimeMillis()
            val MOCK = Active(
                plan = SubscriptionPlan("pro_monthly", "Pro Monthly", 999),
                subscribedOn = Instant.fromEpochMilliseconds(nowMs),
                billingDate = Instant.fromEpochMilliseconds(nowMs) + kotlin.time.Duration.parse("30d"),
                expiresOn = Instant.fromEpochMilliseconds(nowMs) + kotlin.time.Duration.parse("30d"),
                lastSyncTimestamp = nowMs
            )
        }
    }
}

sealed interface SyncStatus {
    data class Idle(val lastSync: Long) : SyncStatus
    data object Loading : SyncStatus
    data class Error(val message: String) : SyncStatus
}

data class SubscriptionRepositoryState(
    val syncStatus: SyncStatus = SyncStatus.Idle(0L),
    val data: SubscriptionData? = null
)
