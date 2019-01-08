package www;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import cartago.Artifact;
import cartago.OPERATION;


public class CrawlerEngineArtifact extends Artifact {
    
    private final int UNDEFINED = -1;
    
    String crawlerEngineUri;
    
    
	void init(String uri) {
	    this.crawlerEngineUri = uri;
	}
	
	@OPERATION
	int addSeed(String seedUri) {
	    log("Adding seed...");
	    HttpPost request = new HttpPost(crawlerEngineUri + "/registrations");
        
        try {
            
            request.setEntity(new StringEntity(seedUri));
            
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(request);
            log(response.getStatusLine().getStatusCode() + "");
            return response.getStatusLine().getStatusCode();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return UNDEFINED;
	}
	
	@OPERATION
	int registerLink(String link) {
	    log("Link to be crawled");
	    HttpPost request = new HttpPost(crawlerEngineUri + "/links");
        
        try {
            
            request.setEntity(new StringEntity(link));
            
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(request);
            log(response.getStatusLine().getStatusCode() + "");
            return response.getStatusLine().getStatusCode();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return UNDEFINED;
	}
}

