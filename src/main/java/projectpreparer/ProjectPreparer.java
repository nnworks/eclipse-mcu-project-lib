package projectpreparer;

import java.net.InetSocketAddress;
import java.util.List;

import fileretriever.HttpClientException;
import fileretriever.HttpFileRetriever;


public class ProjectPreparer {

    public static void main(String[] args) throws HttpClientException {
        
//		HttpFileRetriever retriever = new HttpFileRetriever(new InetSocketAddress("localhost", 31228));
//		
//		retriever.getUris(List.of("uri1", "uri2", "uri3", "uri4", "uri5", "uri6"));

        //new TestStreams().start();
        
        System.out.println(new HttpFileRetriever(new InetSocketAddress("localhost", 3128)).getUri("https://github.com/nnworks/web-component/archive/master.zip"));
    }
}
