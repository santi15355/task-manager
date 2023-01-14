package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Getter
@Setter
@Table(name = "labels")
@NoArgsConstructor
@AllArgsConstructor
public class Label {
    private static final int MIN = 3;
    private static final int MAX = 30;

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
    @ManyToMany
    @JoinTable(name = "task_label",
            joinColumns = @JoinColumn(name = "label_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    private Set<Task> tasks;

    public Label(final Long idValue) {
        this.id = idValue;
    }

    /**
     * The method is for prevention of a status deletion when he has tasks associated with.
     */
    @PreRemove
    public void checkTasksBeforeRemoval() {
        if (this.tasks != null) {
            if (this.tasks.size() > 0) {
                throw new RuntimeException("Can't remove");
            }
        }
    }
}
