package br.com.zup.contas

import javax.persistence.*

@Entity
class Conta(
    val tipo: String,

    @field:ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    val instituicao: Instituicao,

    @Column(nullable = false)
    val agencia: String,

    @Column(nullable = false)
    val numero: String,

    @field:ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    val titular: Titular
) {
    @Column(nullable = false, unique = true)
    @Id
    @GeneratedValue
    val id: Long? = null
}