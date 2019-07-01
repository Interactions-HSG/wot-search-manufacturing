/* Initial beliefs and rules */

searchEngineURI("http://localhost:9090/searchEngine").
crawlerURI("http://localhost:9090/crawler").

lightbulbURI("http://localhost:8085/artifacts/lightbulb").
sanFranciscoWsURI("http://localhost:8085/workspaces/wksp1").

autonomous_color(0.409, 0.518).
manual_color(0.4, 0.2).

/* Initial goals */

!start.

/* General Plans */

+!start : true <- 
	// create artifacts for the robot1, crawler and search engines and add the robot to be maintained as a seed
	.print("Hi! This is the maintenance agent!");
	.wait(5000);
	?searchEngineURI(SearchEngineURI);
	makeArtifact("se","www.SearchEngineArtifact",[SearchEngineURI],SE);
	?crawlerURI(CrawlerURI);
	makeArtifact("ce", "www.CrawlerEngineArtifact", [CrawlerURI], CE);
	//?robotToMaintainURI(Robo1);
	//.send(node_manager, achieve, generateNewArtifact(Robo1));
	?sanFranciscoWsURI(SFURI);
	.print("Registering ", SFURI , " as seed at hypermedia crawler engine");
	addSeed(SFURI) [CE];
	?lightbulbURI(Lightbulb)
	.send(node_manager, achieve, generateNewArtifact(Lightbulb));
	!discoverRobots.
	
+!discoverRobots <-
	searchArtifact("eve: <http://w3id.org/eve#>", "", "eve:isRobot", "", SubjectResult, PredicateResult, ObjectResult) [SE];
	.print("Found robot to monitor: ", SubjectResult);
	+robotToMaintainURI(SubjectResult);
	.send(node_manager, achieve, generateNewArtifact(SubjectResult)).
	
-!discoverRobots [error_msg(Msg)] <-
	//.print("Searching for robots");
	.wait(3000);
	!discoverRobots.
	
	
+event("maintenance") <- 
	.print("Received maintenance event from robo1!");
	.print("Searching for maintenance supplier...");
	!doMaintenance.
	
+!doMaintenance <- 
	?robotToMaintainURI(RobotURI);
	.concat("<", RobotURI, RobotString1);
	.concat(RobotString1, ">", RobotString);
	searchArtifact("eve: <https://w3id.org/stn/core#>", "", "stn:locatedAt", "", SubjectResult, PredicateResult, ObjectResult) [SE];
	//.print("Search Result: ", SearchResult);
	.print("Autonomous maintenance available for: ", RobotURI, " by: ", SubjectResult);
	//Notify node manager to generate artifact for found robot if found any
	.print("[Lightbulb GREEN] Autonomous maintenance in progress...");
	!notifyEngineerAutonomous;
	.wait(3000);
	.print("Maintenance finished!").
	
-!doMaintenance [error_msg(Msg)] <- 
	.print("[Lightbulb RED] No autonomous maintenance available!");
	.print("Maintenance engineer travelling on site...");
	!notifyEngineerTravel;
	.wait(3000);
	.print("Maintenance finished!").

+!notifyEngineerTravel
  	:	thing_artifact_available(_, ArtifactName, WorkspaceName) &
      	hasAction(_,"http://iotschema.org/SwitchOn")[artifact_name(_, ArtifactName)] 
      	& hasAction(_,"http://iotschema.org/SwitchOff")[artifact_name(_, ArtifactName)]
    		& hasAction(_,"http://iotschema.org/SetColor")[artifact_name(_, ArtifactName)] 
  	<-
  		joinWorkspace(WorkspaceName, WorkspaceArtId);
  		?manual_color(CIEx, CIEy);
 	 	!bulb_notification(ArtifactName, CIEx, CIEy).

+!notifyEngineerAutonomous
  	: 	thing_artifact_available(_, ArtifactName, WorkspaceName) &
    		hasAction(_,"http://iotschema.org/SwitchOn")[artifact_name(_, ArtifactName)] 
    		& hasAction(_,"http://iotschema.org/SwitchOff")[artifact_name(_, ArtifactName)]
    		& hasAction(_,"http://iotschema.org/SetColor")[artifact_name(_, ArtifactName)] 
  	<-
  		joinWorkspace(WorkspaceName, WorkspaceArtId);
  		?autonomous_color(CIEx, CIEy);
  		!bulb_notification(ArtifactName, CIEx, CIEy).


/* Lightbulb notification */

+!bulb_notification(ArtifactName, CIEx, CIEy) : true <-
  	act("http://iotschema.org/SetColor", [
          ["http://iotschema.org/CIExData", CIEx], 
          ["http://iotschema.org/CIEyData", CIEy]
        ])[artifact_name(ArtifactName)];
  	.wait(2000);
  	act("http://iotschema.org/SwitchOff", [])[artifact_name(ArtifactName)].


/* Focusing Generated Artifacts */
+thing_artifact_available(ArtifactIRI, ArtifactName, WorkspaceName) : true <-
  	.print("A thing artifact is available: " , ArtifactIRI, " in workspace: ", WorkspaceName);
  	joinWorkspace(WorkspaceName, WorkspaceArtId);
	focusWhenAvailable(ArtifactName).
  
+artifact_available("www.Robot1", ArtifactName, WorkspaceName) : true <-
	.print("An artifact is available: : ", ArtifactName);
  	joinWorkspace(WorkspaceName, WorkspaceArtId);
  	focusWhenAvailable(ArtifactName).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$jacamoJar/templates/org-obedient.asl") }
