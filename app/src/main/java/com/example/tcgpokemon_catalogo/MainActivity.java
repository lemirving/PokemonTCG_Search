package com.example.tcgpokemon_catalogo;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.tcgdex.sdk.TCGdex;
import net.tcgdex.sdk.models.CardResume;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {


    public TCGdex api;
    List<CardResume> cardResumeList =  new ArrayList<>();
    String pokemonName = "Charizard";
    boolean dataLoader = false;
    private SearchView searchBar;
    private RecyclerView recyclerView;
    private Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchBar = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.recycler_view);



        api = new TCGdex("en");
        getAllCards();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);


        searchBar.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                filterCards(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filterCards(newText);
                return true;
            }
        });


    }
    public void getAllCards(){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {

                CardResume[] allCardsFetch = api.fetchCards();
                cardResumeList  = Arrays.asList(allCardsFetch);


                if(!cardResumeList.isEmpty()){

                    runOnUiThread(() ->{
                        if (adapter == null) {
                            adapter = new Adapter(cardResumeList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            // Se o adapter já existe, apenas atualize a lista
                            adapter.updateList(cardResumeList);
                        }


                        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                CardResume clickedCard = adapter.getCardAt(position);

                                if (clickedCard != null) {
                                    String cardId = clickedCard.getId();
                                    Intent intent = new Intent(MainActivity.this, DetalhesActivity.class);

                                    intent.putExtra("CARD_ID", cardId);
                                    startActivity(intent);
                                }
                            }
                            @Override
                            public void onItemLongClick(int position) {
                                // Deixar vazio por enquanto
                            }
                        });
                    });
                }

            } catch (Exception e) {
                Log.e("TESTE", "Error fetching all cards: ", e);
            }
        });
    }

    private void filterCards(String name){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                if(cardResumeList == null){
                    // Se a lista principal ainda não carregou, não faz nada
                    return;
                }

                List<CardResume> filteredList = new ArrayList<>();
                String lowerCaseName = name.toLowerCase();

                // Lógica de Filtragem (Background Thread)
                if (lowerCaseName.isEmpty()) {
                    filteredList.addAll(cardResumeList);
                } else {
                    for (CardResume cr : cardResumeList){
                        if(cr.getName().toLowerCase().contains(lowerCaseName)){
                            filteredList.add(cr);
                        }
                    }
                }

                runOnUiThread(() -> {
                    if (adapter == null) {
                        return;
                    }

                    recyclerView.post(() -> {

                        if (!filteredList.isEmpty()) {
                            adapter.updateList(filteredList);
                            Log.d( "TESTE" , "Filtered " + filteredList.size() + " cards. Example: " + filteredList.get(0).getName());
                        } else {
                            adapter.updateList(new ArrayList<>());
                            Toast.makeText(
                                    MainActivity.this,
                                    "Nenhuma carta encontrada.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
                });


            } catch (Exception e) {

                Log.e("TESTE", "Error during filtering: ", e);
            }
        });
    }
}

