package br.com.zup

import br.com.zup.chaves.ChavePix
import br.com.zup.chaves.clients.BuscaPorClienteResponse
import br.com.zup.contas.Conta
import br.com.zup.contas.Instituicao
import br.com.zup.contas.Titular

fun BuscaPorClienteResponse.toModel() : Conta {
    return Conta(
        tipo,
        Instituicao(
            instituicao.nome,
            instituicao.ispb
        ),
        agencia,
        numero,
        Titular(
            titular.id,
            titular.nome,
            titular.cpf
        )
    )
}

fun CadastraPixRequest.paraChave(instituicao: Instituicao, conta: Conta, titular: Titular) : ChavePix {
    return ChavePix(
        chave,
        tipoDaChave,
        tipoDaConta,
        Conta(
            tipoDaConta.toString(),
            Instituicao(
                instituicao.nome,
                instituicao.ispb
            ),
            conta.agencia,
            conta.numero,
            titular
        )
    )
}