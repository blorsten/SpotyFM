package com.peterwitt.spotyfm.Utilites;

public interface WebResponse {
    void onWebResponse(String response);
    void onWebResponseFailue(String reason);
}
