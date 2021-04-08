package br.com.zup.exceptions

import br.com.zup.chaves.CadastraPixEndpoint
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import javax.inject.Singleton

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionHandlerInterceptor: MethodInterceptor<CadastraPixEndpoint, Any> {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun intercept(context: MethodInvocationContext<CadastraPixEndpoint, Any>?): Any? {

        try {
            return context!!.proceed()
        } catch (e: Exception) {
            logger.error(e.message)

            val statusError = when(e) {
                is IllegalStateException -> Status.ALREADY_EXISTS.withDescription(e.message).asRuntimeException()
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException()
                else -> Status.UNKNOWN.withDescription("Desculpe, houve um erro inesperado").asRuntimeException()
            }

            val responseObserver = context!!.parameterValues[1] as StreamObserver<*>
            responseObserver.onError(statusError.status
                .withDescription(e.message)
                .asRuntimeException())
            return null
        }
    }
}