package br.com.zup.clients.responses

import br.com.zup.clients.requests.BankAccount
import br.com.zup.clients.requests.KeyType
import br.com.zup.clients.requests.Owner
import java.time.LocalDateTime

data class CreatePixKeyResponse(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
)