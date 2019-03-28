package mcu.project.fileretriever;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import com.sun.jdi.VoidValue;

/**
 * Http client for retrieving resources from 'the web'.
 */
public class NnHttpClient {

  protected HttpClient httpClient;
  protected ForkJoinPool threadPool;

  /**
   * Creates an http client for retrieving resources.
   *
   * @param proxy
   */
  public NnHttpClient(final Proxy proxy) {

    threadPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    Builder clientBuilder = HttpClient.newBuilder()
        .version(Version.HTTP_2)
        .followRedirects(Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .executor(threadPool);

    if (proxy != null) {
      InetSocketAddress address = (InetSocketAddress)proxy.address();
      clientBuilder.proxy(ProxySelector.of(address));
    } else {
      clientBuilder.proxy(ProxySelector.getDefault());
    }

    httpClient = clientBuilder.build();
  }

  /**
   * Gets the resource specified in the URI
   *
   * @param uri
   * @return an InputStream for getting the response pay-load
   * @throws NnHttpClientException when an error occurs
   */
  public InputStream getUri(final String uri) throws NnHttpClientException {

    try {
      return getUriFuture(uri).get().body();
    } catch (ExecutionException | InterruptedException e) {
      throw new NnHttpClientException(String.format("Call for URI %s failed.", uri), e);
    }
  }

  /**
   * Gets the resources specified in the list of URI
   * @param uris
   * @return
   * @throws NnHttpClientException
   */
  public Map<String, String> getUris(final List<String> uris) {
    // create a list of futures that perform a request
    List<CompletableFuture<?>> getUriFutures = uris.stream().map((s) -> getUriFuture(s)).collect(Collectors.toList());

    // create a combined future
    CompletableFuture<Void> getUriFuturesCombined = CompletableFuture.allOf(getUriFutures.toArray(new CompletableFuture<?>[getUriFutures.size()]));

    CompletableFuture<List<?>> getUriFuturesCombinedJoined = getUriFuturesCombined.thenApply(VoidValue -> {
      System.out.println("thenApply");
      return getUriFutures.stream().map((cf) -> cf.handle((a, b) -> {
        System.out.println("a:" + a);
        System.out.println("b:" + b);
        return a;
      })
      .join()).collect(Collectors.toList());
    });

    System.out.println("is done?: " + getUriFuturesCombinedJoined.isDone());

    try {
    	getUriFuturesCombinedJoined.get();
    } catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println("is done?: " + getUriFuturesCombinedJoined.isDone());
    return null;
  }

  private CompletableFuture<HttpResponse<InputStream>> getUriFuture(final String uri) {
    try {
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).timeout(Duration.ofSeconds(20)).header("Content-Type", "application/json").GET().build();
      CompletableFuture<HttpResponse<InputStream>> future = httpClient.sendAsync(request, BodyHandlers.ofInputStream());
      return future;
    } catch (IllegalArgumentException e) {
      throw e;
    }
  }

}
