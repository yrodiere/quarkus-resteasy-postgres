package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
public class FilePart extends PanacheEntityBase implements Serializable {

    @Id
    @Column(insertable = false, updatable = false)
    public Long id;

    public String idProperty1;

    public String idProperty2;

    public String filePartFilePath;

    @ManyToOne
    Download download;
}
