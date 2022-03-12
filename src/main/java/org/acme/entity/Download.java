package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.List;

/*
    idProperty1 and idProperty2 form a composite-key, supporting update of existing rows by saveOrUpdate
    adding id field to composite-key would lead to a new row even when a Download should be updated in-place
 */
@Entity
@IdClass(DownloadId.class)
public class Download extends PanacheEntityBase {

    //primary serial key managed by DB only
    @Column(insertable = false, updatable = false)
    public Long id;

    @Id
    public String idProperty1;

    @Id
    public String idProperty2;

    @Column
    public Boolean finished;

    @OneToMany(mappedBy = "download")
    public List<FilePart> filePartList;

    public Download(String idProperty1, String idProperty2, Boolean finished, List<FilePart> filePartList) {
        this.idProperty1 = idProperty1;
        this.idProperty2 = idProperty2;
        this.finished = finished;
        this.filePartList = filePartList;
    }

    public Download() {
    }
}
