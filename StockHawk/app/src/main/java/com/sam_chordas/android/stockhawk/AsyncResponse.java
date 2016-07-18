package com.sam_chordas.android.stockhawk;

import com.sam_chordas.android.stockhawk.rest.ChartParcelable;

/**
 * Created by douglas on 16/05/2016.
 */
public interface AsyncResponse {
    void processFinish(ChartParcelable[] result);
}
