package br.com.zup.chaves.cadastra

import br.com.zup.*
import br.com.zup.chaves.ChavePix
import br.com.zup.chaves.ChavePixRepository
import br.com.zup.clients.BacenClient
import br.com.zup.clients.ErpClient
import br.com.zup.contas.Conta
import br.com.zup.contas.ContaRepository
import br.com.zup.exceptions.ErrorHandler
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@ErrorHandler
@Singleton
class CadastraPixEndpoint(
    @Inject private val chavePixRepository: ChavePixRepository,
    @Inject private val contaRepository: ContaRepository,
    @Inject private val erpClient: ErpClient,
    @Inject private val bacenClient: BacenClient) :
    PixServiceGrpc.PixServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun cadastra(request: CadastraPixRequest, responseObserver: StreamObserver<CadastraPixResponse>) {

        logger.info("$request")

        try {
            logger.info("Verificando se a conta existe")

            val possivelConta = contaRepository.findByClienteId(request.clienteId)

            val conta = if (possivelConta.isPresent) possivelConta.get()
            else erpClient.buscaContaERP(request.clienteId, request.tipoDaConta.toString()).let {
                if (it == null) {
                    throw IllegalStateException("Conta não encontrada!")
                }
                logger.info("Salvando conta no banco de dados")
                contaRepository.save(it.toModel())
            }

            logger.info("Conta encontrada")
            logger.info("Verificando se a chave já está cadastrada")
            if (chavePixRepository.existsByChave(request.chave)) {
                throw IllegalStateException("Chave já cadastrada!")
            }

            logger.info("Chave não encontrada, cadastrando chave")

            val novaChave = toModel(request,conta)

            logger.info("Salvando chave no banco de dados")
            chavePixRepository.save(novaChave)

            logger.info("Cadastrando chave no Bacen")
            bacenClient.cadastra(novaChave.paraPixRequest(conta))
        } catch (e: ConstraintViolationException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Request com parametros inválidos")
                    .asRuntimeException()
            )
        }

        logger.info("Gerando response do cadastro da chave")
        val response = CadastraPixResponse.newBuilder().apply {
            clienteId = request.clienteId
            pixId = request.chave
        }.build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()

    }

}

private fun toModel(request: CadastraPixRequest, conta: Conta) : ChavePix {
    return ChavePix(
        chave = request.chave,
        tipoDaChave = request.tipoDaChave,
        tipoDaConta = request.tipoDaConta,
        conta = conta
    )
}