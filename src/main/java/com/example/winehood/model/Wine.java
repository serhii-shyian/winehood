package com.example.winehood.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "wines")
@SQLDelete(sql = "UPDATE wines SET is_deleted = true WHERE id = ?")
@SQLRestriction(value = "is_deleted=false")
@Getter
@Setter
@ToString
public class Wine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal price;
    private String grapeVariety;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Region region;
    @OneToMany(mappedBy = "wine", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Review> reviews;
    @Column(nullable = false)
    private boolean isDeleted;
}
