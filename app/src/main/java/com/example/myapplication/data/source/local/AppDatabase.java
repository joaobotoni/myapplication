package com.example.myapplication.data.source.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myapplication.data.source.local.converters.Converters;
import com.example.myapplication.data.source.local.dao.CapacidadeFreteDao;
import com.example.myapplication.data.source.local.dao.CategoriaFreteDao;
import com.example.myapplication.data.source.local.dao.CategoriaNegociacaoDao;
import com.example.myapplication.data.source.local.dao.CorretorDao;
import com.example.myapplication.data.source.local.dao.EmpresaDao;
import com.example.myapplication.data.source.local.dao.FreteDao;
import com.example.myapplication.data.source.local.dao.NegociacaoAnimalDao;
import com.example.myapplication.data.source.local.dao.NegociacaoGadoDao;
import com.example.myapplication.data.source.local.dao.RacaDao;
import com.example.myapplication.data.source.local.dao.TipoReferenciaDao;
import com.example.myapplication.data.source.local.dao.TipoVeiculoFreteDao;
import com.example.myapplication.data.source.local.dao.ValorReferenciaDao;
import com.example.myapplication.data.source.local.entities.CapacidadeFrete;
import com.example.myapplication.data.source.local.entities.CategoriaFrete;
import com.example.myapplication.data.source.local.entities.CategoriaNegociacao;
import com.example.myapplication.data.source.local.entities.Corretor;
import com.example.myapplication.data.source.local.entities.Empresa;
import com.example.myapplication.data.source.local.entities.Frete;
import com.example.myapplication.data.source.local.entities.NegociacaoAnimal;
import com.example.myapplication.data.source.local.entities.NegociacaoGado;
import com.example.myapplication.data.source.local.entities.Raca;
import com.example.myapplication.data.source.local.entities.TipoReferencia;
import com.example.myapplication.data.source.local.entities.TipoVeiculoFrete;
import com.example.myapplication.data.source.local.entities.ValorReferencia;

@Database(entities = {
        Frete.class, CapacidadeFrete.class, CategoriaFrete.class, TipoVeiculoFrete.class,
        ValorReferencia.class, TipoReferencia.class, NegociacaoGado.class, NegociacaoAnimal.class,
        Empresa.class, Corretor.class, CategoriaNegociacao.class, Raca.class
}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract FreteDao freteDao();
    public abstract CategoriaFreteDao categoriaFreteDao();
    public abstract CapacidadeFreteDao capacidadeFreteDao();
    public abstract TipoVeiculoFreteDao tipoVeiculoFreteDao();
    public abstract ValorReferenciaDao valorReferenciaDao();
    public abstract TipoReferenciaDao tipoReferenciaDao();
    public abstract NegociacaoGadoDao negociacaoGadoDao();
    public abstract NegociacaoAnimalDao negociacaoAnimalDao();
    public abstract EmpresaDao empresaDao();
    public abstract CorretorDao corretorDao();
    public abstract CategoriaNegociacaoDao categoriaNegDao();
    public abstract RacaDao racaDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "Sample.db")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);

                                    // CATEGORIAS FRETE
                                    db.execSQL("INSERT INTO xgp_categoria_frete (descricao) VALUES ('Boi')");
                                    db.execSQL("INSERT INTO xgp_categoria_frete (descricao) VALUES ('Vaca')");
                                    db.execSQL("INSERT INTO xgp_categoria_frete (descricao) VALUES ('Bezerro')");

                                    // TIPOS DE TRANSPORTES
                                    db.execSQL("INSERT INTO xgp_tipo_veiculo_frete (descricao) VALUES ('TRUK')");
                                    db.execSQL("INSERT INTO xgp_tipo_veiculo_frete (descricao) VALUES ('CARRETA BAIXA')");
                                    db.execSQL("INSERT INTO xgp_tipo_veiculo_frete (descricao) VALUES ('CARRETA ALTA')");
                                    db.execSQL("INSERT INTO xgp_tipo_veiculo_frete (descricao) VALUES ('CARRETA TRES EIXOS')");

                                    // CAPACIDADE DE BOIS
                                    db.execSQL("INSERT INTO xgp_capacidade_frete(id_categoria_frete, id_tipo_veiculo_frete, qtde_inicial, qtde_final) VALUES (1,1,1,18)");
                                    db.execSQL("INSERT INTO xgp_capacidade_frete(id_categoria_frete, id_tipo_veiculo_frete, qtde_inicial, qtde_final) VALUES (1,2,19,26)");
                                    db.execSQL("INSERT INTO xgp_capacidade_frete(id_categoria_frete, id_tipo_veiculo_frete, qtde_inicial, qtde_final) VALUES (1,3,27,36)");
                                    db.execSQL("INSERT INTO xgp_capacidade_frete(id_categoria_frete, id_tipo_veiculo_frete, qtde_inicial, qtde_final) VALUES (1,4,37,45)");

                                    // CAPACIDADE DE VACAS
                                    db.execSQL("INSERT INTO xgp_capacidade_frete(id_categoria_frete, id_tipo_veiculo_frete, qtde_inicial, qtde_final)VALUES (2,1,1,20)");
                                    db.execSQL("INSERT INTO xgp_capacidade_frete(id_categoria_frete, id_tipo_veiculo_frete, qtde_inicial, qtde_final) VALUES (2,2,21,28)");
                                    db.execSQL("INSERT INTO xgp_capacidade_frete(id_categoria_frete, id_tipo_veiculo_frete, qtde_inicial, qtde_final) VALUES (2,3,29,38)");
                                    db.execSQL("INSERT INTO xgp_capacidade_frete(id_categoria_frete, id_tipo_veiculo_frete, qtde_inicial, qtde_final) VALUES (2,4,39,50)");

                                    // CAPACIDADE DE BEZERROS
                                    db.execSQL("INSERT INTO xgp_capacidade_frete(id_categoria_frete, id_tipo_veiculo_frete, qtde_inicial, qtde_final) VALUES (3,1,1,38)");
                                    db.execSQL("INSERT INTO xgp_capacidade_frete(id_categoria_frete, id_tipo_veiculo_frete, qtde_inicial, qtde_final) VALUES (3,2,39,55)");
                                    db.execSQL("INSERT INTO xgp_capacidade_frete(id_categoria_frete, id_tipo_veiculo_frete, qtde_inicial, qtde_final) VALUES (3,3,56,75)");
                                    db.execSQL("INSERT INTO xgp_capacidade_frete(id_categoria_frete, id_tipo_veiculo_frete, qtde_inicial, qtde_final) VALUES (3,4,76,110)");

                                    // TRUCK
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,1,0,50,836.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,1,51,75,1067.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,1,76,100,1320.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,1,101,150,1640.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,1,151,200,1980.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,1,201,250,2390.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,1,251,300,2830.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (1,1,301,9223372036854775807,9.90)");

                                    // CARRETA BAIXA
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,2,0,50,1040.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,2,51,75,1350.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,2,76,100,1650.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,2,101,150,2200.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,2,151,200,2820.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,2,201,250,3390.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,2,251,300,3950.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (1,2,301,9223372036854775807,13.00)");

                                    // CARRETA ALTA
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,3,0,50,1250.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,3,51,75,1650.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,3,76,100,2050.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,3,101,150,2800.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,3,151,200,3500.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,3,201,250,4100.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,3,251,300,4700.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (1,3,301,9223372036854775807,15.00)");

                                    // CARRETA TRES EIXOS
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,4,0,50,1400.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,4,51,75,2000.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,4,76,100,2350.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,4,101,150,3400.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,4,151,200,4100.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,4,201,250,4700.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (0,4,251,300,5300.00)");
                                    db.execSQL("INSERT INTO xgp_frete (tipo_cobranca, id_tipo_veiculo_frete, km_inicial, km_final, valor) VALUES (1,4,301,9223372036854775807,17.00)");

                                    // TIPOS DE REFERÊNCIA DE PREÇO
                                    db.execSQL("INSERT INTO xgp_tipo_referencia (id_tipo_referencia, descricao) VALUES (1, 'CEPEA/Esalq')");
                                    db.execSQL("INSERT INTO xgp_tipo_referencia (id_tipo_referencia, descricao) VALUES (2, 'Mercado Local')");
                                    db.execSQL("INSERT INTO xgp_tipo_referencia (id_tipo_referencia, descricao) VALUES (3, 'Negociação Particular')");

                                    // EMPRESA PADRÃO
                                    db.execSQL("INSERT INTO empresa (nome) VALUES ('Agropecuaria Poças')");
                                    db.execSQL("INSERT INTO empresa (nome) VALUES ('Fazenda Bom Sucesso')");

                                    // CORRETOR PADRÃO
                                    db.execSQL("INSERT INTO xgp_corretor (name, comissao, tipo_comissao) VALUES ('Jucilei Ferreira da Silva', 25.0, 'c')");

                                    // VALOR DE REFERÊNCIA INICIAL
                                    db.execSQL("INSERT INTO xgp_valor_referencia (id_valor_referencia, id_tipo_referencia, id_empresa, data_referencia, valor_arroba_boi, valor_bezerro, peso_bezerro, valor_arroba_vaca, valor_bezerra, peso_bezerra, agio_bezerro, agio_bezerra) VALUES (1, 1, 1, strftime('%s','now') * 1000, 350.00, 2800.00, 180, 319.00, 2800.00, 180, 29.0, 30.0)");

                                    // RAÇAS BOVINAS
                                    db.execSQL("INSERT INTO xgp_raca (id_raca, descricao, sigla) VALUES (1, 'Hereford', 'HER')");
                                    db.execSQL("INSERT INTO xgp_raca (id_raca, descricao, sigla) VALUES (2, 'Nelore', 'NEL')");
                                    db.execSQL("INSERT INTO xgp_raca (id_raca, descricao, sigla) VALUES (3, 'Aberdeen', 'ANG')");
                                    db.execSQL("INSERT INTO xgp_raca (id_raca, descricao, sigla) VALUES (4, 'Anelorado', 'NEL')");
                                    db.execSQL("INSERT INTO xgp_raca (id_raca, descricao, sigla) VALUES (5, 'Cruzamento', '')");
                                    db.execSQL("INSERT INTO xgp_raca (id_raca, descricao, sigla) VALUES (6, 'Girolanda', '')");
                                    db.execSQL("INSERT INTO xgp_raca (id_raca, descricao, sigla) VALUES (7, 'Tabapuã', '')");
                                    db.execSQL("INSERT INTO xgp_raca (id_raca, descricao, sigla) VALUES (8, 'Brahma', '')");
                                    db.execSQL("INSERT INTO xgp_raca (id_raca, descricao, sigla) VALUES (9, 'Brangus', '')");
                                    db.execSQL("INSERT INTO xgp_raca (id_raca, descricao, sigla) VALUES (10, 'Caracu', '')");
                                    db.execSQL("INSERT INTO xgp_raca (id_raca, descricao, sigla) VALUES (11, 'Sinepol', '')");
                                }
                            }).build();
                }
            }
        }
        return INSTANCE;
    }
}