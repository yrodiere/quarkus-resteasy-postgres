package org.acme;

import io.quarkus.runtime.StartupEvent;
import org.acme.entity.Download;
import org.acme.entity.FilePart;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.event.Observes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Logger;

@Path("/sync")
public class DownloadSyncResource {

    private static final Logger LOG = Logger.getLogger("DownloadSyncResource");

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String jdbc;

    void onStart(@Observes StartupEvent ev) {
        LOG.info("jdbc: " + jdbc);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sync() {
        Download download = new Download("a", "b", false, List.of());
        if(!download.isPersistent()) download.persist();

        Download download2 = new Download("a", "b", true, List.of(
                new FilePart("a", "b", "/home/myfile.txt.part1"),
                new FilePart("a", "b", "/home/myfile.txt.part2")
        ));
        download2.persist();

        //TODO move to test like https://quarkus.io/guides/hibernate-orm-panache#mocking

        return "Hello RESTEasy";
    }
}