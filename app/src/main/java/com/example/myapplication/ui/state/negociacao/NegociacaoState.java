package com.example.myapplication.ui.state.negociacao;

import static com.example.myapplication.ui.state.negociacao.StatusNegociacao.NEGOCIADA;

public class NegociacaoState {
    private final CotacaoState cotacao;
    private final PropostaState proposta;
    private final FechamentoState fechamento;
    private final StatusNegociacao statusNegociacao;

    public NegociacaoState(CotacaoState cotacao, PropostaState proposta, FechamentoState fechamento) {
        this.fechamento = fechamento;
        this.proposta = proposta;
        this.cotacao = cotacao;
        this.statusNegociacao = StatusNegociacao.PRE_CALCULADA;
    }

    public NegociacaoState(CotacaoState cotacao, PropostaState proposta, FechamentoState fechamento, StatusNegociacao statusNegociacao) {
        this.cotacao = cotacao;
        this.proposta = proposta;
        this.fechamento = fechamento;
        this.statusNegociacao = statusNegociacao;
    }

    public CotacaoState getCotacao() {
        return cotacao;
    }

    public PropostaState getProposta() {
        return proposta;
    }

    public FechamentoState getFechamento() {
        return fechamento;
    }

    public StatusNegociacao getStatusNegociacao() {return statusNegociacao;}

    public boolean isNegociada(){
        return statusNegociacao == NEGOCIADA;
    }
}