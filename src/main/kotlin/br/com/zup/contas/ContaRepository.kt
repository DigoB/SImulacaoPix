package br.com.zup.contas

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ContaRepository : JpaRepository<Conta, Long> {

    @Query("select c from Conta c where c.titular.id = :clienteId")
    fun findByClienteId(clienteId: String?): Optional<Conta>
}