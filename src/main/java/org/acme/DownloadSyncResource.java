package org.acme;

import io.quarkus.runtime.StartupEvent;
import org.acme.entity.Download;
import org.acme.entity.FilePart;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.event.Observes;
import javax.transaction.Transactional;
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

        download.idProperty1 = "bla";               //test implicit setter
        String idProperty1 = download.idProperty1;  //test implicit getter

        persist(download);

        Download download2 = new Download("a", "b", true, List.of(
                new FilePart("/home/myfile.txt.part1"),
                new FilePart("/home/myfile.txt.part2")
        ));

//        persist(download2);
//        org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint "pk_download"
//        Detail: Key (idproperty1, idproperty2)=(a, b) already exists.

        //TODO find download by  idProperty1 and idProperty1
        // if download.isPersistent: download2 data |-> queried download entity

        return "";
    }

    @Transactional
    public void persist(Download download){
        download.persist();
    }

}