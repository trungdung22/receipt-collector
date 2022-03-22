package com.demo.restapi.model;

import com.demo.restapi.model.audit.UserDateAudit;
import com.demo.restapi.service.StorageService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(name="medias")
@Data
public class Media extends UserDateAudit{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "file_name")
    private String fileName;

    @NotBlank
    @Column(name = "key_name")
    private String keyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    @ToString.Exclude
    private Receipt receipt;

    @Transient
    private String url;

    public Media(@NotBlank String fileName, @NotBlank String keyName, Receipt receipt) {
        this.fileName = fileName;
        this.keyName = keyName;
        this.receipt = receipt;
    }

    public Media() {

    }

    @JsonIgnore
    public Receipt getReceipt() {
        return receipt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Media media = (Media) o;
        return id != null && Objects.equals(id, media.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
