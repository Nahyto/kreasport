package fr.univ_lille1.iut_info.caronic.kreasport.volley;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by Chris on 23-Mar-17.
 */

public class ProxyHurlStack extends HurlStack {

    private static final String LOG = HurlStack.class.getSimpleName();

    private static final String PROXY_ADDRESS = "cache.univ-lille1.fr";
    private static final int PROXY_PORT = 3128;

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        Proxy proxy = new Proxy(Proxy.Type.HTTP,
                InetSocketAddress.createUnresolved(PROXY_ADDRESS, PROXY_PORT));
        return (HttpURLConnection) url.openConnection(proxy);
    }

    /*

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        final HttpURLConnection urlConnection;
        Proxy proxy = null;
        try {
            proxy = ProxySelector.getDefault().select(url.toURI()).get(0);
        } catch(Exception e) {
            Log.d(LOG, e.getMessage());
        }
        if (proxy == null) {
            urlConnection = (HttpURLConnection) url.openConnection();
        } else {
            urlConnection = (HttpURLConnection) url.openConnection(proxy);
        }
        return urlConnection;
    }
     */
}
