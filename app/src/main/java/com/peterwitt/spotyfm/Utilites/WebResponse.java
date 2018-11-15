package com.peterwitt.spotyfm.Utilites;

public interface WebResponse {
    void onWebResponse(String response);
    void onWebResponseFailure(String reason);
}
