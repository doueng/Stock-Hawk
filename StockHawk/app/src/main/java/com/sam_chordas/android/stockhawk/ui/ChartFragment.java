package com.sam_chordas.android.stockhawk.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.AsyncResponse;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.ChartParcelable;
import com.sam_chordas.android.stockhawk.rest.FetchChartData;

/**
 * Created by douglas on 14/05/2016.
 */
public class ChartFragment extends android.support.v4.app.Fragment implements AsyncResponse {

    ChartParcelable[] parcel;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_line_graph, container, false);


        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

        Bundle bundle = getArguments();
        String symbol = bundle.getString("Symbol");

        FetchChartData fetchChartData = new FetchChartData();
        fetchChartData.delegate = this;
        fetchChartData.execute(symbol);

        return rootView;
    }


    @Override
    public void processFinish(ChartParcelable[] result) {
        this.parcel = result;


        LineChartView lineChart = (LineChartView) rootView.findViewById(R.id.linechart);

        LineSet dataset = new LineSet();
        Float lowestPrice = parcel[0].getClosePrice();
        Float highestPrice = 0f;

        if (parcel.length != 0) {
            for (int i=parcel.length-1; i>0; i--) {
                dataset.addPoint(parcel[i].getDate(), parcel[i].getClosePrice());

                if (parcel[i].getClosePrice()>highestPrice) {
                    highestPrice = parcel[i].getClosePrice();
                }

                if (parcel[i].getClosePrice()<lowestPrice) {
                    lowestPrice = parcel[i].getClosePrice();
                }


            }

            int yAxisStart = Math.round(lowestPrice)  - 20;
            int yAxisEnd = Math.round(highestPrice) + 5;
            int i = 0;
            while (i<100) {
                i++;
                if (((yAxisEnd + i) - yAxisStart)%10 == 0) {
                    yAxisEnd += i;
                    break;
                }
            }

            lineChart.addData(dataset);

            lineChart.setAxisBorderValues(yAxisStart, yAxisEnd);
            lineChart.setStep(10);

            lineChart.show();
        }
    }
}
