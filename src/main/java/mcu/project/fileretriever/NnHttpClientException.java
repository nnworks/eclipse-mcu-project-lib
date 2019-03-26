package mcu.project.fileretriever;

public class NnHttpClientException extends Throwable {

	private static final long serialVersionUID = -2638969859260044103L;

	NnHttpClientException(final String message) {
		super(message);
	}

	NnHttpClientException(final String message, final Throwable t) {
		super(message, t);
	}
	
}
