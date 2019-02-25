package fileretriever;

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

/**
 * Establish a SSL connection to a host and port, writes a byte and prints the
 * response. See
 * http://confluence.atlassian.com/display/JIRA/Connecting+to+SSL+services
 */
public class HttpFileRetriever {

  protected HttpClient httpClient;
  protected ForkJoinPool threadPool;

  /**
   * Creates a HttpFileRetriever that can e
   *
   * @param proxy
   */
  public HttpFileRetriever(final InetSocketAddress proxy) {

    try {
      URI uri = new URI("ftps://speedtest.tele2.net/1MB.zip");
      URLConnection con = uri.toURL().openConnection(new Proxy(Proxy.Type.HTTP, proxy));
      con.connect();
      con.getContentType();
      con.getInputStream().available();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }


    threadPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    Builder clientBuilder = HttpClient.newBuilder().version(Version.HTTP_2).followRedirects(Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20)).executor(threadPool);

    if (proxy != null) {
      clientBuilder.proxy(ProxySelector.of(proxy));
    } else {
      clientBuilder.proxy(ProxySelector.getDefault());
    }

    httpClient = clientBuilder.build();
  }

  /**
   * Get the resource specified with the URI
   *
   * @param uri
   * @return a String containing the response pay-load
   * @throws HttpClientException
   */
  public String getUri(final String uri) throws HttpClientException {
    try {
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).timeout(Duration.ofMinutes(1))
          .header("Content-Type", "application/json").GET().build();
      HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

      return response.body();

    } catch (Exception e) {
      throw new HttpClientException(String.format("Call for URI %s failed.", uri), e);
    }
  }

  public Map<String, String> getUris(final List<String> uris) throws HttpClientException {
    List<CompletableFuture<?>> getUriFutures = uris.stream().map((s) -> getUriFuture(s)).collect(Collectors.toList());

    CompletableFuture<Void> getUriFuturesCombined = CompletableFuture
        .allOf(getUriFutures.toArray(new CompletableFuture<?>[getUriFutures.size()]));

    CompletableFuture<List<?>> getUriFuturesCombinedJoined = getUriFuturesCombined.thenApply(nullValue -> {
      return getUriFutures.stream().map((cf) -> cf.handle((a, b) -> {
        return a;
      }).join()).collect(Collectors.toList());
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

  private static class Response {
    Response(Throwable throwable, HttpResponse<InputStream> response) {
      this.throwable = throwable;
      this.response = response;
    }

    protected Throwable throwable;
    protected HttpResponse<InputStream> response;
  }

  private static CompletableFuture<Response> handleClientFuture(HttpResponse<InputStream> responseStream, Throwable t) {
    return CompletableFuture.completedFuture(new Response(t, responseStream));
  }

  private CompletableFuture<HttpResponse<InputStream>> getUriFuture(final String uri) {
    try {
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).timeout(Duration.ofMinutes(1))
          .header("Content-Type", "application/json").GET().build();
      CompletableFuture<HttpResponse<InputStream>> future = httpClient.sendAsync(request, BodyHandlers.ofInputStream());
      return future;
    } catch (IllegalArgumentException e) {
      throw e;
    }
  }

}
