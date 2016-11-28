package dtu.dk.budgetracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class ProgressBarActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private DatabaseHandler db;
    private float percentFloat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create text views
        TextView leftText = (TextView) findViewById(R.id.textView);
        TextView rightText = (TextView) findViewById(R.id.textView2);
        TextView percents = (TextView) findViewById(R.id.textViewPercent);

        //Assign new typeface (font)
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Roboto_Medium.ttf");

        //assign font for texts
        percents.setTypeface(typeface);
        leftText.setTypeface(typeface);
        rightText.setTypeface(typeface);

        //create progress bar
        TextRoundCornerProgressBar progressBar = (TextRoundCornerProgressBar) findViewById(R.id.progress_2);
        db = new DatabaseHandler(getApplicationContext());

        progressBar.setMax(5000);
        float moneySpent = db.getTotalAmount();
        percentFloat = moneySpent / progressBar.getMax() * 100;
        if(percentFloat > 100) {
            percentFloat = 100;
        }

        Log.i("OHFUCKTAG", "TOTAL AMOUNT EXUCTED FROM bar activity: " + db.getTotalAmount());

        String moneyLeft = Float.toString(progressBar.getMax() - moneySpent);
        progressBar.setProgress(Float.parseFloat(moneyLeft));

        progressBar.setProgressBackgroundColor(Color.parseColor("#757575"));
        progressBar.setProgressColor(Color.parseColor("#56d2c2"));
        progressBar.setProgressText(moneyLeft + " / " +progressBar.getMax());
        leftText.setText(Float.toString(progressBar.getProgress()));
        rightText.setText(Float.toString(progressBar.getMax()));
        percents.setText(String.format("%.1f", percentFloat)+ "%");

        Button chartABtn = (Button) findViewById(R.id.chartAButton);
        chartABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProgressBarActivity.this, ChartActivity.class);
                startActivity(i);
            }
        });

        /*
        IconRoundCornerProgressBar progress2 = (IconRoundCornerProgressBar) findViewById(R.id.progress_2);

        progress2.setProgressColor(Color.parseColor("#56d2c2"));
        progress2.setProgressBackgroundColor(Color.parseColor("#757575"));
        progress2.setIconBackgroundColor(Color.parseColor("#38c0ae"));
        progress2.setMax(550);
        progress2.setProgress(147);
        */
       // progress2.setIconImageResource(imageResource);



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ProgressBar Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
