package www;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;


public class SearchEngineArtifact extends Artifact {
    
    private final int UNDEFINED = -1;
    
    String searchEngineUri;
    
    
	void init(String uri) {
	    this.searchEngineUri = uri;
	}
	
	@OPERATION
	void searchArtifact(String prefix, String subject, String predicate, String object, OpFeedbackParam<String> result) {
	    log("Searching on " + searchEngineUri);
	    HttpPost request = new HttpPost(searchEngineUri);
	    
	    // Buidl sparql query
	    if (subject == null || subject.equals("")) {
			subject = "?x";
		}
	    if (predicate == null || predicate.equals("")) {
			predicate = "?p";
		}
	    if (object == null || object.equals("")) {
			object = "?y";
		}
	    String spo = subject + " " + predicate + " " + object;
        String sparqlQuery = "@prefix " + prefix + " construct {" + spo + " } where {" + spo + "}";
        log(sparqlQuery);
        try {
        		
            request.setEntity(new StringEntity(sparqlQuery));
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(request);
            String resultString = EntityUtils.toString(response.getEntity());
            log("Response[" + response.getStatusLine().getStatusCode() + ": " + resultString);
            // assuming only 1 artifact returned
            if (resultString.trim().length() > 0) {
            		String[] splittedResult = resultString.split(" ");
                String resultArtifact = splittedResult[splittedResult.length-2].trim().replace("<", "").replace(">", "");
                result.set(resultArtifact);
			} else {
				failed("inc failed","inc_failed","max_value_reached",5);
			}
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    	}
}

