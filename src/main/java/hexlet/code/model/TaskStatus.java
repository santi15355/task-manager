package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Getter
@Setter
@Table(name = "statuses")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatus {
    private static final int MIN_V = 1;
    private static final int MAX_V = 1_000;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(min = MIN_V, max = MAX_V)
    @Column(unique = true)
    private String name;
    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;

    public TaskStatus(final Long idValue) {
        this.id = idValue;
    }

    //@JsonIgnore
    /*@OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "taskStatus"
    )*/
    //private List<Task> tasks;

    public TaskStatus(final Long idValue, String nameValue) {
        this.id = idValue;
        this.name = nameValue;
    }

    /**
     * The method is for prevention of a status deletion when he has tasks associated with.
     */
    /*@PreRemove
    public void checkTasksAssociationBeforeRemoval() {
        if (this.tasks != null) {
            if (!this.tasks.isEmpty()) {
                throw new RuntimeException("Can't remove a status that has Tasks.");
            }
        }
    }*/
}
