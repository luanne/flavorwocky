<%@page import="com.sun.jersey.api.client.*" %>
<%@page import="javax.ws.rs.core.MediaType" %>
<%@page import="java.net.URI" %>

<html>
<body>
<h2>Hello WWWWWWWWWWWW World!</h2>

<%!
    private static final String SERVER_ROOT_URI = "http://5afd95982.hosted.neo4j.org:7024/db/data/";
    private static final String nodeEntryPointUri = SERVER_ROOT_URI + "node";
%>
<%

WebResource resource = Client.create()
        .resource( nodeEntryPointUri );
ClientResponse cresponse = resource.accept( MediaType.APPLICATION_JSON )
        .type( MediaType.APPLICATION_JSON )
        .entity( "{}" )
        .post( ClientResponse.class );

final URI location = cresponse.getLocation();
System.out.println( String.format(
        "POST to [%s], status code [%d], location header [%s]",
        nodeEntryPointUri, cresponse.getStatus(), location.toString() ) );
cresponse.close();

%>
</body>
</html>
