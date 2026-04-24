package com.smartcampus.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/")
public class TestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> test() {

        Map<String, String> res = new HashMap<>();
        res.put("message", "API is working");

        return res;
    }
}