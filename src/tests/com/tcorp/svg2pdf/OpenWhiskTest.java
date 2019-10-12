package com.tcorp.svg2pdf;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class OpenWhiskTest {
    private static final Gson gson = new Gson();
    @Test
    public void test() throws Exception{
        OpenWhisk.main(gson.fromJson(
                "{\"type\":\"EAN13\",\"params\":{\"code\":\"978020137962\"}}",
                JsonObject.class));
    }
}
