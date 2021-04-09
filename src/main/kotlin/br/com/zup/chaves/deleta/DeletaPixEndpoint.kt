package br.com.zup.chaves.deleta

import br.com.zup.DeletaPixRequest
import br.com.zup.DeletaPixResponse
import br.com.zup.DeletaPixServiceGrpc
import br.com.zup.chaves.ChavePixRepository
import br.com.zup.clients.BacenClient
import br.com.zup.clients.requests.DeletePixKeyRequest
import br.com.zup.clients.requests.DeletePixKeyResponse
import br.com.zup.contas.ContaRepository
import br.com.zup.exceptions.ChaveNaoEncontradaException
import br.com.zup.exceptions.ErrorHandler
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class DeletaPixEndpoint(
    @Inject private val chavePixRepository: ChavePixRepository,
    @Inject private val bacenClient: BacenClient,

) : DeletaPixServiceGrpc.DeletaPixServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun deleta(request: DeletaPixRequest?, responseObserver: StreamObserver<DeletaPixResponse>?) {

        logger.info("$request")

        logger.info("Verificando se a chave existe no banco de dados")
        val possivelChave = chavePixRepository.findById(request!!.pixId)
        val chavePix = if (possivelChave.isPresent) possivelChave.get()
        else throw ChaveNaoEncontradaException("Chave ${request.pixId} não encontrada")

        logger.info("Chave encontrada")

        logger.info("Deletando chave do banco de dados")
        chavePixRepository.delete(chavePix)

        val solicitaDelete = chavePix.conta.instituicao.ispb.let {
            DeletePixKeyRequest(key = chavePix.chave, participant = chavePix.conta.instituicao.ispb)
        }

        logger.info("Solicitando delete no Bacen")
        val response = bacenClient.deleta(solicitaDelete.key,solicitaDelete)
        if (response.status != HttpStatus.OK) {
            throw IllegalStateException("Erro ao remover chave pix do Banco Central")
        }
    }
}