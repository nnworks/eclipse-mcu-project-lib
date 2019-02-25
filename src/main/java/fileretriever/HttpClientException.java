package fileretriever;

public class HttpClientException extends Throwable {

	private static final long serialVersionUID = -2638969859260044103L;

	HttpClientException(final String message) {
		super(message);
	}

	HttpClientException(final String message, final Throwable t) {
		super(message, t);
	}
	
}
