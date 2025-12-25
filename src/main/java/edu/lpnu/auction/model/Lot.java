package edu.lpnu.auction.model;

import edu.lpnu.auction.model.enums.LotStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(indexes = {
        @Index(name = "idx_lot_status_start_time", columnList = "status, start_time"),
        @Index(name = "idx_lot_status_end_time", columnList = "status, end_time")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lot extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User seller;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Car car;

    @Column(precision = 19, scale = 2)
    private BigDecimal startPrice;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentPrice;

    @Column(precision = 19, scale = 2)
    private BigDecimal minBidIncrement;

    @Column(precision = 19, scale = 2)
    private BigDecimal reservePrice;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LotStatus status;

    private Integer bidCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    private User currentHighBidder;

    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bid> bids = new ArrayList<>();

    @Version
    private Long version;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Lot lot = (Lot) o;
        return getId() != null && Objects.equals(getId(), lot.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
