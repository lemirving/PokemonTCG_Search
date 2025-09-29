package com.example.tcgpokemon_catalogo;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import net.tcgdex.sdk.TCGdex;
import net.tcgdex.sdk.models.Card;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.tcgdex.sdk.models.subs.CardAttack;
import net.tcgdex.sdk.models.subs.CardVariants;
import net.tcgdex.sdk.models.subs.CardWeakRes;



public class DetalhesActivity extends AppCompatActivity {

    private static final String TAG = "DetalhesTAG"; // Adicione o TAG para logs
    TCGdex api;

    private TextView detailNameTextView;
    private ImageView detailImageView;
    private TextView detailRarityIllustrator;
    private LinearLayout attacksContainer;
    private TextView evolvesFrom;
    private TextView detailStageEvolve;
    private TextView detailDescriptionTextView;
    private TextView detailVariantsContent;
    private LinearLayout weaknessContainer;
    private LinearLayout resistanceContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhes_activity);


        detailNameTextView = findViewById(R.id.detail_name_textview);
        detailImageView = findViewById(R.id.detail_image_view);

        api = new TCGdex("en");

        detailNameTextView = findViewById(R.id.detail_name_textview);
        detailImageView = findViewById(R.id.detail_image_view);
        detailRarityIllustrator = findViewById(R.id.detail_rarity_illustrator);
        detailStageEvolve = findViewById(R.id.detail_stage_evolve);
        detailDescriptionTextView = findViewById(R.id.detail_description_textview);
        detailVariantsContent = findViewById(R.id.detail_variants_content);
        attacksContainer = findViewById(R.id.attacks_container);
        weaknessContainer = findViewById(R.id.weakness_container);
        resistanceContainer = findViewById(R.id.resistance_container);



        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("CARD_ID")) {
            String cardId = intent.getStringExtra("CARD_ID");
            fetchCardDetails(cardId);
        }

    }

    public void fetchCardDetails(String id) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Card cardDetails = api.fetchCard(id);

                runOnUiThread(() -> {
                    if (cardDetails != null) {
                        Log.d(TAG, "Detalhes da Carta Carregados: " + cardDetails.getName());
                        displayDetails(cardDetails);
                    }
                });
            } catch (Exception e) {

                Log.e(TAG, "Error on searching: ", e);
            }
        });
    }

    private void displayDetails(Card card) {
        TextView nameView = findViewById(R.id.detail_name_textview);
        String headerText = String.format("%s | HP: %s | %s",
                card.getName(),
                card.getHp(),
                card.getRarity());
        nameView.setText(headerText);



        TextView rarityIllustratorView = findViewById(R.id.detail_rarity_illustrator);
        String metaText = String.format("Set: %s (%s)\nIllustrator: %s",
                card.getSet().getName(),
                card.getSet().getId().toUpperCase(),
                card.getIllustrator());
        rarityIllustratorView.setText(metaText);


        TextView stageEvolveView = findViewById(R.id.detail_stage_evolve);
        String evolveText = String.format("Category: %s\nStage: %s\nEvaluates from: %s",
                card.getCategory(),
                card.getStage() != null ? card.getStage() : "Basic",
                card.getEvolveFrom() != null ? card.getEvolveFrom() : "N/A");
        stageEvolveView.setText(evolveText);


        TextView descriptionView = findViewById(R.id.detail_description_textview);
        if (card.getDescription() != null) {
            descriptionView.setText(card.getDescription());
        } else {
            descriptionView.setText("Description not available.");
        }
        displayVariants(card);


        displayAttacks(
                card.getAttacks(),
                R.id.attacks_container,
                "Attacks"
        );


        displayWeaknessResistance(
                card.getWeaknesses(),
                R.id.weakness_container,
                "Weakness"
        );
        displayWeaknessResistance(
                card.getResistances(),
                R.id.resistance_container,
                "Resistence"
        );

        ImageView detailImageView = findViewById(R.id.detail_image_view);
        String imageUrl = card.getImage() + "/high.webp";
        Glide.with(this).load(imageUrl).error(R.drawable.ic_error_image).into(detailImageView);

    }
    private void displayWeaknessResistance(List<CardWeakRes> dataList, int containerId, String sectionName) {
        LinearLayout container = findViewById(containerId);
        container.removeAllViews();

        if (dataList != null && !dataList.isEmpty()) {
            for (CardWeakRes item : dataList) {
                TextView textView = new TextView(this);
                String text = String.format("Type: %s\nValue: %s",
                        item.getType(),
                        item.getValue());

                textView.setText(text);
                textView.setTextSize(16);
                textView.setPadding(0, 4, 0, 4);

                container.addView(textView);
            }
        } else {

            TextView noDataView = new TextView(this);
            noDataView.setText(sectionName + " not listed.");
            noDataView.setTextSize(16);
            container.addView(noDataView);
        }
    }
    private void displayAttacks(List<CardAttack> dataList, int containerId, String sectionName) {
        LinearLayout container = findViewById(containerId);
        container.removeAllViews();

        if (dataList != null && !dataList.isEmpty()) {
            for (CardAttack item : dataList) {
                TextView textView = new TextView(this);
                String text = String.format("Name: %s\nDamage: %s\nCost: %s\nEffect: %s",
                        item.getName(),
                        item.getDamage(),
                        item.getCost(),
                        item.getEffect());

                textView.setText(text);
                textView.setTextSize(16);
                textView.setPadding(0, 4, 0, 4);

                container.addView(textView);
            }
        } else {

            TextView noDataView = new TextView(this);
            noDataView.setText(sectionName + " not listed.");
            noDataView.setTextSize(16);
            container.addView(noDataView);
        }
    }
    private void displayVariants(Card card) {

        TextView variantsTextView = findViewById(R.id.detail_variants_content);

        CardVariants variants = card.getVariants();

        if (variants != null) {

            String text = String.format(
                    "Normal: %s\nFirst Edition: %s\nHolo: %s\nReverse: %s\nWPromo: %s",
                    variants.getNormal() ? "Yes" : "No",
                    variants.getFirstEdition() ? "Yes" : "No",
                    variants.getHolo() ? "Yes" : "No",
                    variants.getReverse() ? "Yes" : "No",
                    variants.getWPromo() ? "Yes" : "No"
            );

            variantsTextView.setText(text);
            variantsTextView.setTextSize(16);
        } else {
            variantsTextView.setText("Information not available.");
        }
    }

}

