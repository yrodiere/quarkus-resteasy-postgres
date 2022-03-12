package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.List;

@Entity
@IdClass(DownloadId.class)
public class Download extends PanacheEntityBase {

    @Id
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

}
