package com.example.divulgatudo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.divulgatudo.DAO.DAO;
import com.example.divulgatudo.Objeto.Anuncios;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private EditText editAnuncio, editCliente, editInicio, editTermino, editInvestimento, editBusca;
    private Button buttonCadastrar;
    private ListView listView;
    private TextView textInvest, textShare, textClicks, textViews, textNomes;
    private long alcanceMaximo, investimentoTotal;
    private List<String> nomes;
    List<Anuncios> anuncio;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editCliente = findViewById(R.id.editTextCliente2);
        editInicio = findViewById(R.id.editTextInicio2);
        editInvestimento = findViewById(R.id.editTextInvestimento2);
        editAnuncio = findViewById(R.id.editTextAnuncio2);
        editTermino = findViewById(R.id.editTextTermino2);
        buttonCadastrar = findViewById(R.id.buttonCadastar2);
        listView = findViewById(R.id.listview);
        textInvest = findViewById(R.id.textInvest2);
        textClicks = findViewById(R.id.textClicks2);
        textShare = findViewById(R.id.textShare2);
        textViews = findViewById(R.id.textViews2);
        textNomes = findViewById(R.id.textNomes2);
        searchView = findViewById(R.id.searchView);

        //Criando a máscara para os campos de data do editText
        SimpleMaskFormatter data = new SimpleMaskFormatter("NN/NN/NNNN");

        MaskTextWatcher dataWatch = new MaskTextWatcher(editInicio, data);
        editInicio.addTextChangedListener(dataWatch);

        MaskTextWatcher mtw = new MaskTextWatcher(editTermino, data);
        editTermino.addTextChangedListener(mtw);

        buscaNoBanco();

        pesquisar();

        //Buscando os valores dos objetos através de evento de clique na listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Anuncios anuncios1 = anuncio.get(position);
                textNomes.setText(anuncios1.getCliente());
                try {
                    String CurrentDate = anuncios1.getDatainicio();
                    String FinalDate = anuncios1.getDatatermino();
                    Date date1;
                    Date date2;
                    SimpleDateFormat dates = new SimpleDateFormat("dd/MM/yyyy");
                    date1 = dates.parse(CurrentDate);
                    date2 = dates.parse(FinalDate);
                    long difference = Math.abs(date1.getTime() - date2.getTime());
                    long differenceDates = difference / (24 * 60 * 60 * 1000);
                    investimentoTotal = differenceDates * anuncios1.getInvestimento();

                    String Alcance = Long.toString(investimentoTotal);

                    calculoAlcance();

                    textInvest.setText("R$ " + Alcance);

                } catch (Exception exception) {
                    Toast.makeText(getApplicationContext(), "Não foi possível calcular a " +
                            "diferença entre as datas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(editAnuncio.getText().toString().equals("") || (editCliente.getText().toString().equals(""))
                        || (editInicio.getText().toString().equals("")) || (editTermino.getText().toString().equals(""))
                        || (editInvestimento.getText().toString().equals("")))) {

                    DAO dao = new DAO(getApplicationContext());
                    Anuncios anuncios = new Anuncios();
                    anuncios.setNome(editAnuncio.getText().toString());
                    anuncios.setCliente(editCliente.getText().toString());
                    anuncios.setDatainicio(editInicio.getText().toString());
                    anuncios.setDatatermino(editTermino.getText().toString());
                    anuncios.setInvestimento(Integer.valueOf(editInvestimento.getText().toString()));
                    dao.cadastroAnuncio(anuncios);
                    dao.close();


                    //Calculando a diferença entre a data de inicio e término dos anúncios
                    try {
                        String CurrentDate = anuncios.getDatainicio();
                        String FinalDate = anuncios.getDatatermino();
                        Date date1;
                        Date date2;
                        SimpleDateFormat dates = new SimpleDateFormat("dd/MM/yyyy");
                        date1 = dates.parse(CurrentDate);
                        date2 = dates.parse(FinalDate);
                        long difference = Math.abs(date1.getTime() - date2.getTime());
                        long differenceDates = difference / (24 * 60 * 60 * 1000);
                        investimentoTotal = differenceDates * anuncios.getInvestimento();

                        String Alcance = Long.toString(investimentoTotal);

                        calculoAlcance();

                        textInvest.setText("R$ " + Alcance);


                    } catch (Exception exception) {
                        Toast.makeText(getApplicationContext(), "Não foi possível calcular a " +
                                "diferença entre as datas", Toast.LENGTH_SHORT).show();
                    }

                    DAO dao1 = new DAO(getApplicationContext());
                    anuncio = dao1.buscaAnuncios();
                    nomes = new ArrayList<String>();


                    for (Anuncios nomeBuscado : anuncio) {

                        nomes.add(nomeBuscado.getNome());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, nomes);
                    listView.setAdapter(adapter);

                    buscaNoBanco();

                    limpaCampos();


                } else {

                    Toast.makeText(getApplicationContext(), "Preencha todos campos para " +
                            "cadastrar seu anúncio", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Método de busca e listagem dos anúncios no listview
    private void buscaNoBanco() {

        DAO dao1 = new DAO(getApplicationContext());
        anuncio = dao1.buscaAnuncios();
        nomes = new ArrayList<String>();

        for (Anuncios nomeBuscado : anuncio){

            nomes.add(nomeBuscado.getNome());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, nomes);
        listView.setAdapter(adapter);

    }


    //Método para limpar os campos de texto depois de cadastrar o anúncio
    private void limpaCampos() {
        editInvestimento.setText("");
        editTermino.setText("");
        editInicio.setText("");
        editCliente.setText("");
        editAnuncio.setText("");
    }

    //Cálculo dos compartilhamentos para descobrir o alcance máximo do anúncio
    private void calculoAlcance(){
        Long views = investimentoTotal * 30;

        double shareRate = 0.018;
        double reshareRate = 0.72;
        double clickRate = 0.12;

        double compartilhamentos = (views)*shareRate;
        double compartilhamentos2 = (compartilhamentos)*reshareRate;
        double compartilhamentos3 = (compartilhamentos2)*reshareRate;
        double compartilhamentos4 = (compartilhamentos3)*reshareRate;

        double compartilhamento = Math.round(compartilhamentos + compartilhamentos2 + compartilhamentos3 + compartilhamentos4);
        double visualizações = Math.round(views + (compartilhamento * 40));

        double cliques = Math.round(visualizações * clickRate);

        String sharemax = String.valueOf(compartilhamento);
        String viewsmax = String.valueOf(visualizações);
        String clicksmax = String.valueOf(cliques);

        textShare.setText(sharemax);
        textViews.setText(viewsmax);
        textClicks.setText(clicksmax);

    }
    //Método para filtrar por nome os anúncios
    private void pesquisar(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                DAO dao = new DAO(getApplicationContext());
                anuncio = dao.buscaAnuncios();
                nomes = new ArrayList<String>();

                for (Anuncios nomeBuscado : anuncio) {

                    if (nomeBuscado.getNome().toLowerCase().contains(newText.toLowerCase())) {
                        nomes.add(nomeBuscado.getNome());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, nomes);
                listView.setAdapter(adapter);

                return false; }
        });
    }
}


