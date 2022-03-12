package org.acme.entity;

import java.io.Serializable;
import java.util.Objects;

public class DownloadId implements Serializable {
    private String idProperty1;

    private String idProperty2;

    public DownloadId(String idProperty1, String idProperty2) {
        this.idProperty1 = idProperty1;
        this.idProperty2 = idProperty2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadId that = (DownloadId) o;
        return Objects.equals(idProperty1, that.idProperty1) && Objects.equals(idProperty2, that.idProperty2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProperty1, idProperty2);
    }
}