package br.com.zup.contas

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Instituicao(
    @Column(nullable = false)
    @field:NotBlank
    val nome: String,

    @Column(nullable = false)
    @field:NotBlank
    val ispb: String
) {
    @Id
    @GeneratedValue
    val id: Long? = null

}
