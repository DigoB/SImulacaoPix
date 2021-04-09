package br.com.zup.exceptions

import java.lang.RuntimeException

class ChaveNaoEncontradaException(mensagem: String) : RuntimeException(mensagem)