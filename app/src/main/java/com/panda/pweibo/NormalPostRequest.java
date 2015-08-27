package com.panda.pweibo;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * 普通的post请求方法,并返回jsonObject请求结果
 *
 * Created by Administrator on 2015/08/26.
 */
public class NormalPostRequest extends Request<JSONObject> {

    private     Map                     mMap;
    private     Listener<JSONObject>    mListener;

    public NormalPostRequest(String uri, Listener<JSONObject> listener,
                             ErrorListener errorListener, Map map) {
        super(Method.POST, uri, errorListener);

        mListener = listener;
        mMap = map;
    }

    @Override
    protected Map getParams() throws AuthFailureError {
        return mMap;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
//            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            StringBuilder builder = new StringBuilder();
            builder.append(response.data);

            return Response.success(new JSONObject(String.valueOf(builder)), HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject jsonObject) {

    }
}
