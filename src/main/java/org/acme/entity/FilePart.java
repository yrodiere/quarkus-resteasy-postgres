package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class FilePart extends PanacheEntityBase implements Serializable {
    @Id
    @SequenceGenerator(name = "filepart_id_seq", sequenceName = "filepart_id_seq", allocationSize = 1, initialValue = 4)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "filepart_id_seq")
    public Long id;

    public String filePartFilePath;

    //adds fk fields idProperty1, idProperty2 to FilePart table
    // ==> FIXME maybe conflicting with existing fields idProperty1, idProperty2
    //BEWARE: all @Id fields of Download must be included
    //examples: https://www.baeldung.com/jpa-join-column
    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumns({
                    @JoinColumn(referencedColumnName = "idProperty1", name = "idProperty1"),
                    @JoinColumn(referencedColumnName = "idProperty2", name = "idProperty2")
//                    @JoinColumn(referencedColumnName = "id", name = "downloadId")
            })
    Download download;



    public FilePart() {
    }

    public FilePart(String filePartFilePath) {
        this.filePartFilePath = filePartFilePath;
    }
}
