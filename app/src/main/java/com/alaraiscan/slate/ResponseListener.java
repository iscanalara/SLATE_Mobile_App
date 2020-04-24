package com.alaraiscan.slate;

/**
 * The interface Response listener.
 */
public interface ResponseListener {
    /**
     * On response changed.
     *
     * @param text:the text for response.
     */
    void onResponseChanged(String text);
}
