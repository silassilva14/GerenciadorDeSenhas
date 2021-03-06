package com.project.silas.gerenciadordesenhas.business;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.repository.database.dao.SiteDao;
import com.project.silas.gerenciadordesenhas.repository.network.BackendIntegrator;

import java.io.File;

public class TelaPrincipalBusiness {

    private Context contexto;
    private SQLiteDatabase bancoDados;
    private SiteDao siteDao;

    private BackendIntegrator backendIntegrator;

    private Usuario usuarioLogado;
    private FileBusiness fileBusiness;

    public TelaPrincipalBusiness (Context context){
        this.contexto = context;
        this.bancoDados = InicializacaoBusiness.getDatabase();
        this.siteDao = new SiteDao(this.bancoDados);
        this.usuarioLogado = SessionSingletonBusiness.getUsuario();
        this.backendIntegrator = new BackendIntegrator(this.contexto);
        this.fileBusiness = new FileBusiness(this.contexto);
    }

    public OperationResult<Cursor> buscarLogins(String queryPesquisa) {

        OperationResult<Cursor> retornoSites = new OperationResult<>();
        String pesquisa = "%" + queryPesquisa + "%";
        Cursor cursor = null;

        try {
            cursor = this.siteDao.rawQuery(Query.BUSCA_SITES_USUARIO, new String[]{
                    String.valueOf(this.usuarioLogado.getId())
                    , pesquisa
                    , pesquisa
            });

            Log.i("telaPrincipalBusiness", "Qtde de registros: " + cursor.getCount());

            retornoSites.withResult(cursor);

        } catch (Throwable error){
            error.printStackTrace();
            Log.i("telaPrincipalBusiness", "Erro ao carregar sites. Mensagem: " + error.getMessage());
            retornoSites.withError(error);
        }
        return retornoSites;
    }

    public interface Query {
        String BUSCA_SITES_USUARIO = "SELECT * FROM " + Site.Metadata.TABLE_NAME
                + " WHERE " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID_USUARIO + " = ?"
                + " AND (" + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_URL + " LIKE ?"
                + " OR " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_LOGIN + " LIKE ?"
                + ")"
                + " ORDER BY " + Site.Metadata.FIELD_URL + " " + Site.Metadata.ORDER_BY_DESC;
    }
}
