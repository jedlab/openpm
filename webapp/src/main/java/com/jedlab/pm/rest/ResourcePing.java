package com.jedlab.pm.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class ResourcePing
{

    @GET
    @Path("ping")
    public Response ping()
    {
        return Response.ok("pong").build();
    }

}
