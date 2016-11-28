package dtu.dk.budgetracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;


import java.util.ArrayList;
import java.util.Map;


public class ChartActivity extends AppCompatActivity {
    private DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init database
        db = new DatabaseHandler(getApplicationContext());

        //assign barchart view
        BarChart barChart = (BarChart) findViewById(R.id.horChart);

        int[] objects  = {800, 210, 300, 80, 752, 124};
        int count = 0;
        //populating with data
        ArrayList<BarEntry> entries = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList<>();
        //for (int i = 0; i < db.getSpendings().size(); i++) {
        //    entries.add(new BarEntry(i, db.getSpendings().));
        //}

        for (Map.Entry<String,Integer> entry : db.getSpendings().entrySet()){
            String key = entry.getKey();
            int val = entry.getValue();
            entries.add(new BarEntry(count, val));
            labels.add(key);
            count++;
        }
        /*
        for(int data : objects){
            //creating bars float x,y axis
            entries.add(new BarEntry(count,data));
            count++; //inc count to see how many columns
        }*/

        BarDataSet barDataSet = new BarDataSet(entries, "DKK amount");

        //X axis labels

        //for (int i = 0; i < db.getAllRecords().size(); i++) {
        //    labels.add(db.getAllRecords().get(i).getShop());
        //}

       // barDataSet.setDrawValues(true); //not working for horizontal
        barDataSet.setColor(Color.RED); // set color




        BarData barData = new BarData(barDataSet);
        //barData.setDrawValues(true);//not working for horizontal
        //Set shop nemes from the list to X axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int)value);
            }

        });


        //assign data for the chart
        barChart.setData(barData);

        //set description, requires new Object, because stupidity
        Description description = new Description();
        description.setText("");
        //description setup
        barChart.setDescription(description);
        barChart.setDrawValueAboveBar(true);
        barChart.animateY(1250);
        barChart.invalidate();

    }

}
