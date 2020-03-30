package io.lastwill.eventscan.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import org.springframework.util.ClassUtils;

import javax.persistence.*;

@Entity
@Getter
public abstract class TransitionEntry implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name = "tx_hash")
    private String txHash;

    @Setter
    @Column(name = "transfer_status")
    @Enumerated(EnumType.STRING)
    private TransferStatus transferStatus;


    public TransitionEntry(String txHash) {
        this.txHash = txHash;
    }

    @Transient // DATAJPA-622
    public boolean isNew() {
        return null == getId();
    }

    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(ClassUtils.getUserClass(obj))) {
            return false;
        }

        TransitionEntry that = (TransitionEntry) obj;

        return null != this.getId() && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }
}
