package com.sam_chordas.android.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

import java.util.ArrayList;


public class WidgetService extends IntentService {

    private static final int INDEX_SYMBOL= 0;
    private static final int INDEX_BIDPRICE = 1;
    private static final int INDEX_CHANGE = 3;

    public WidgetService() {
        super("WidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                WidgetProvider.class));

        Cursor cursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE,},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);

        if (cursor == null) {
            return;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        ArrayList<String> list = new ArrayList<>();

        while (true) {

            list.add(cursor.getString(INDEX_SYMBOL).toUpperCase());
            list.add(cursor.getString(INDEX_BIDPRICE));
            list.add(cursor.getString(INDEX_CHANGE));

            if (!cursor.moveToNext()) {
                cursor.close();
                break;
            }
        }

        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget);
            remoteViews.removeAllViews(R.id.widget_linear_layout);

            int symbolInt = 0;
            int bidPriceInt = 1;
            int changeInt = 2;

            for (int i=0; i<cursor.getColumnCount(); i++) {

                RemoteViews textView = new RemoteViews(getPackageName(), R.layout.widget_text_view);

                textView.setTextViewText(R.id.stock_symbol_list, list.get(symbolInt));
                textView.setTextViewText(R.id.bid_price_list, list.get(bidPriceInt));
                textView.setTextViewText(R.id.change_list, list.get(changeInt));

                remoteViews.addView(R.id.widget_linear_layout, textView);

                symbolInt += 3;
                bidPriceInt += 3;
                changeInt += 3;
            }

            Intent launchIntent = new Intent(this, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_linear_layout, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
