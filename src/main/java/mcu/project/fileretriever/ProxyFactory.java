package mcu.project.fileretriever;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class ProxyFactory {

  public static ProxyFactory instance = new ProxyFactory();

  private HashMap<String, Object> proxies;


  private ProxyFactory() {
    proxies = new HashMap<>();
  }


  public Proxy createProxy(Proxy.Type type) {

    int proxyPort = 0;
    String proxyHost = null;

    try {
      if (type == Proxy.Type.HTTP) {
          String httpProxy = System.getenv("http_proxy");
          if (httpProxy != null && httpProxy.length() > 3) {
            proxyPort = new URI(httpProxy).getPort();
            proxyHost = new URI(httpProxy).getHost();
          }
      } else if (type == Proxy.Type.SOCKS) {
        String httpProxy = System.getenv("socks_proxy");
        if (httpProxy != null && httpProxy.length() > 3) {
          proxyPort = new URI(httpProxy).getPort();
          proxyHost = new URI(httpProxy).getHost();
        }
      }
    } catch (URISyntaxException e) {
      // no proxy
      // TODO logging
    }

    return createProxy(proxyHost, proxyPort, type);
  }

  public Proxy createProxy(final String host, final int port, Proxy.Type type) {

    final String proxyKey = host + ":" + port + "/" + type.name();

    // cached?
    if (proxies.containsKey(proxyKey)) {
      return (Proxy)proxies.get(proxyKey);
    }

    Proxy proxy = null;

    if (host != null && host.length() > 4) {
      InetSocketAddress address = new InetSocketAddress(host, port);
      proxy = new Proxy(type, address);
    }

    proxies.put(proxyKey, proxy);

    return proxy;
  }
}
