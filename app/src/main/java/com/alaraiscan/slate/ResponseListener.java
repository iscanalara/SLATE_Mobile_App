package com.alaraiscan.slate;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The interface Response listener.
 */
public interface ResponseListener {
    /**
     * On response changed.
     *
     * @param text:the text for response.
     */
    void onResponseChanged(JSONObject text) throws JSONException;
}
