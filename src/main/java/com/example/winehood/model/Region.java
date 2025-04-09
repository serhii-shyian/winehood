package com.example.winehood.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "regions")
@SQLDelete(sql = "UPDATE regions SET is_deleted = true WHERE id = ?")
@SQLRestriction(value = "is_deleted=false")
@Getter
@Setter
@ToString
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String country;
    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
    private List<Wine> wines;
    @Column(nullable = false)
    private boolean isDeleted;
}
