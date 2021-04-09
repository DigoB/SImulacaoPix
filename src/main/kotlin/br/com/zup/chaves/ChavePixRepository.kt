package br.com.zup.chaves

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix,Long> {

    fun existsByChave(chave: String?): Boolean

//    @Query("select c from ChavePix c where c.id = :pixId")
//    fun findByChave(pixId: Long?): Optional<ChavePix>
}