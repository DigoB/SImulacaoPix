package br.com.zup.chaves

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface ChavePixRepository: JpaRepository<ChavePix,Long> {

    @Query("SELECT c FROM ChavePix c WHERE c.chave = :chave")
    fun existsByChave(chave: String?): Boolean
}