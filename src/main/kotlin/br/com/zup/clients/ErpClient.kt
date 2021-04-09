package br.com.zup.clients

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:9091")
interface ErpClient {

    @Get(
        "/api/v1/clientes/{clienteId}/contas",
        consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON]
    )
    fun buscaContaERP(@PathVariable clienteId: String, @QueryValue tipo: String): BuscaPorClienteResponse?
}

data class BuscaPorClienteResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
)

data class InstituicaoResponse(
    val nome: String,
    val ispb : String
)

data class TitularResponse(
    val id: String,
    val nome: String,
    val cpf: String
)