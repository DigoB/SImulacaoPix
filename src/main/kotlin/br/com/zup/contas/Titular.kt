package br.com.zup.contas

import br.com.zup.validations.CpfOuCnpj
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Titular(
    @field:Id
    val id: String,

    val nome: String,

    @CpfOuCnpj
    val cpf: String
)
