package br.com.zup.chaves

import br.com.zup.TipoDaChave
import br.com.zup.TipoDaConta
import br.com.zup.clients.requests.*
import br.com.zup.contas.Conta
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class ChavePix(
    @Column(nullable = false, unique = true)
    @field:NotBlank
    var chave: String,

    @field:NotNull
    val tipoDaChave: TipoDaChave,

    @field:NotNull
    val tipoDaConta: TipoDaConta,

    @field:ManyToOne
    val conta: Conta
) {
    fun paraPixRequest(conta: Conta): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            key = this.chave,
            keyType = KeyType.getKeyType(this.tipoDaChave),
            bankAccount = BankAccount(
                participant = conta.instituicao.ispb,
                conta.agencia,
                conta.numero,
                AccountType.getAccountType(this.tipoDaConta)
            ),
            owner = Owner(
                type = Type.LEGAL_PERSON,
                name = conta.titular.nome,
                taxIdNumber = conta.titular.cpf
            )
        )

    }

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    val id: Long? = null

    var criadoEm: LocalDateTime = LocalDateTime.now()
}