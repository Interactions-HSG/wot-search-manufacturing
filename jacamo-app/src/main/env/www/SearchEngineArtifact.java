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
	void searchArtifact(String prefix, String subject, String predicate, String object, 
			OpFeedbackParam<String> subjectRes, OpFeedbackParam<String> predicateRes, OpFeedbackParam<String> objectRes) {
	    log("Searching on " + searchEngineUri);
	    HttpPost request = new HttpPost(searchEngineUri);
	    
	    // Build sparql query
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
            		resultString = resultString.replace("\n", "");
            		log(resultString);
            		String[] splittedResult = resultString.split(" ");
            		log(splittedResult[3]);
                String resultObject = splittedResult[5].trim().replace("<", "").replace(">", "");
                String resultPredicate = splittedResult[4].trim().replace("<", "").replace(">", "");
                String resultSubject = splittedResult[3].substring(1).trim().replace("<", "").replace(">", "");
                objectRes.set(resultObject);
                subjectRes.set(resultSubject);
                predicateRes.set(resultPredicate);
			} else {
				failed("no result","no_result","query_returned_empty");
			}
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    	}
}

