package com.choxmi.collegeops;

import org.json.JSONException;

/**
 * Created by Choxmi on 12/21/2017.
 */

public interface AsyncResponse {
    void processFinish(String response) throws JSONException;
}
