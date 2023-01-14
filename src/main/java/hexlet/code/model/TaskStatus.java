package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Getter
@Setter
@Table(name = "statuses")
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatus {
    private static final int MIN = 1;
    private static final int MAX = 1000;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(min = MIN, max = MAX)
    @Column(unique = true)
    private String name;
    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;
    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "taskStatus"
    )
    private List<Task> tasks;

    public TaskStatus(final Long idValue) {
        this.id = idValue;
    }

    public TaskStatus(final Long idValue, String nameValue) {
        this.id = idValue;
        this.name = nameValue;
    }

    /**
     * The method is for prevention of a status deletion when he has tasks associated with.
     */
    @PreRemove
    public void checkTasksBeforeRemoval() {
        if (this.tasks != null) {
            if (!this.tasks.isEmpty()) {
                throw new RuntimeException("Can't remove status");
            }
        }
    }
}
