package main.kotlin.core.domain.exception

abstract class DomainException(
    message: String
) : RuntimeException(message)