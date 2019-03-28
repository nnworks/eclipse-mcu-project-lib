package projectpreparer;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import mcu.project.fileretriever.NnHttpClientException;
import mcu.project.fileretriever.NnHttpClient;
import mcu.project.fileretriever.ProxyFactory;


public class ProjectPreparer {

  public static void main(String[] args) throws NnHttpClientException {
//		HttpFileRetriever retriever = new HttpFileRetriever(new InetSocketAddress("localhost", 31228));
//
//		retriever.getUris(List.of("uri1", "uri2", "uri3", "uri4", "uri5", "uri6"));

        //new TestStreams().start();

    // System.out.println(new NnHttpClient(ProxyFactory.instance.createProxy(Proxy.Type.HTTP)).getUri("https://github.com/nnworks/web-component/archive/master.zip"));
    //InputStream is = new NnHttpClient(ProxyFactory.instance.createProxy(Proxy.Type.HTTP)).getUri("https://github.com/nnworks/web-component/archive/master.zip");
    System.out.println(new NnHttpClient(ProxyFactory.instance.createProxy(Proxy.Type.HTTP)).getUris(List.of(
        "http://speedtest.tele2.net/1MB.zip",
        "https://github.com/nnworks/web-component/archive/master.zip")));
  }
}
