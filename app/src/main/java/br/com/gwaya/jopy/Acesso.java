package br.com.gwaya.jopy;

import android.content.Context;
import android.content.Intent;

import br.com.gwaya.jopy.activity.LoginActivity;


@SuppressWarnings("ResourceType")
public class Acesso {
    public long id;
    public String Access_Token;
    public String Refresh_Token;
    public String Usuario;
    public String Senha;
    public String Token_Type;
    public String dtMod;


    public static final void logoff(Context context) {

        try {
            AcessoDataSource acessoDataSource = new AcessoDataSource();
            PedidoCompraDataSource pedidoCompraDatasource = new PedidoCompraDataSource();

            acessoDataSource.open();
            acessoDataSource.deleteAcesso(null);
            acessoDataSource.close();

            pedidoCompraDatasource.open();
            pedidoCompraDatasource.deleteAll();
            pedidoCompraDatasource.close();
/*
            AlertDialog.Builder builder = new AlertDialog.Builder(context.getApplicationContext());
            builder.setMessage("Por favor faça login novamente.")
                    .setTitle("Autenticação")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            Dialog dialog = builder.create();
            dialog.show();
*/

            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
    }
}
/*

1 Lista de códigos de status HTTP
2 1xx Informativa
2.1 100 Continuar
2.2 101 Mudando protocolos
2.3 122 Pedido-URI muito longo
3 2xx Sucesso
3.1 200 OK
3.2 201 Criado
3.3 202 Aceito
3.4 203 não-autorizado (desde HTTP/1.1)
3.5 204 Nenhum conteúdo
3.6 205 Reset
3.7 206 Conteúdo parcial
3.8 207-Status Multi (WebDAV) (RFC 4918)
4 3xx Redirecionamento
4.1 300 Múltipla escolha
4.2 301 Movido
4.3 302 Encontrado
4.4 304 Não modificado
4.5 305 Use Proxy (desde HTTP/1.1)
4.6 306 Proxy Switch
4.7 307 Redirecionamento temporário (desde HTTP/1.1)
5 4xx Erro de cliente
5.1 400 Requisição inválida
5.2 401 Não autorizado
5.3 402 Pagamento necessário
5.4 403 Proibido
5.5 404 Não encontrado
5.6 405 Método não permitido
5.7 406 Não Aceitável
5.8 407 Autenticação de proxy necessária
5.9 408 Timeout Pedido
5.10 409 Conflito
5.11 410 Gone
5.12 411 comprimento necessário
5.13 412 Pré-condição falhou
5.14 413 Entidade de solicitação muito grande
5.15 414 Pedido-URI Too Long
5.16 415 Tipo de mídia não suportado
5.17 416 Solicitada de Faixa Não Satisfatória
5.18 417 Falha na expectativa
5.19 418 Eu sou um bule de chá
5.20 422 Entidade improcessável (WebDAV) (RFC 4918)
5.21 423 Fechado (WebDAV) (RFC 4918)
5.22 424 Falha de Dependência (WebDAV) (RFC 4918)
5.23 425 coleção não ordenada (RFC 3648)
5.24 426 Upgrade Obrigatório (RFC 2817)
5.25 450 bloqueados pelo Windows Controles dos Pais
5.26 499 cliente fechou Pedido (utilizado em ERPs/VPSA)
6 5xx outros erros
6.1 500 Erro interno do servidor (Internal Server Error)
6.2 501 Não implementado (Not implemented)
6.3 502 Bad Gateway
6.4 503 Serviço indisponível (Service Unavailable)
6.5 504 Gateway Time-Out
6.6 505 HTTP Version not supported


 */