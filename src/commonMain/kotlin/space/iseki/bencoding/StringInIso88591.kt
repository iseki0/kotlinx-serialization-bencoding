package space.iseki.bencoding

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MetaSerializable

@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MetaSerializable
annotation class StringInIso88591
