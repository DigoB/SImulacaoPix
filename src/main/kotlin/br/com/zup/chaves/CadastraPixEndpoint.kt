package br.com.zup.chaves

import br.com.zup.*
import br.com.zup.chaves.clients.BacenClient
import br.com.zup.chaves.clients.ErpClient
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
                contaRepository.save(it.toModel())
            }

            logger.info("Conta encontrada")
            logger.info("Verificando se a chave já está cadastrada")
            if (chavePixRepository.existsByChave(request.chave)) {
                throw IllegalStateException("Chave já cadastrada!")
            }

            // TODO: 08/04/2021 Descobrir como chamar a conta, instituicao e titular
//            val novaChave = request.paraChave()

            logger.info("Salvando chave no banco de dados")
//            chavePixRepository.save(novaChave)

            logger.info("Cadastrando chave no Bacen")
//            bacenClient.cadastra(novaChave.paraPixRequest())
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