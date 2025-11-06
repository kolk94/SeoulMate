package seoulmate.mission.entity;

import jakarta.persistence.*;
import lombok.*;
import seoulmate.user.entity.User;

import java.time.Instant;

@Entity
@Table(name = "mission_attempts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MissionAttempt {

    public enum Status {
        PENDING,
        SUCCESS,
        FAIL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(nullable = false, length = 300)
    private String imageUrl;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(length = 50)
    private String verifiedLabel;

    private Double verifiedScore;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = Status.PENDING;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void verifySuccess(String label, Double score) {
        this.status = Status.SUCCESS;
        this.verifiedLabel = label;
        this.verifiedScore = score;
    }

    public void verifyFail(String label, Double score) {
        this.status = Status.FAIL;
        this.verifiedLabel = label;
        this.verifiedScore = score;
    }
}
