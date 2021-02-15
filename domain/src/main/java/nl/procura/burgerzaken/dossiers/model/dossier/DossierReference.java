/*
 * Copyright 2021 - 2022 Procura B.V.
 *
 * In licentie gegeven krachtens de EUPL, versie 1.2
 * U mag dit werk niet gebruiken, behalve onder de voorwaarden van de licentie.
 * U kunt een kopie van de licentie vinden op:
 *
 *   https://github.com/vrijBRP/vrijBRP/blob/master/LICENSE.md
 *
 * Deze bevat zowel de Nederlandse als de Engelse tekst
 *
 * Tenzij dit op grond van toepasselijk recht vereist is of schriftelijk
 * is overeengekomen, wordt software krachtens deze licentie verspreid
 * "zoals deze is", ZONDER ENIGE GARANTIES OF VOORWAARDEN, noch expliciet
 * noch impliciet.
 * Zie de licentie voor de specifieke bepalingen voor toestemmingen en
 * beperkingen op grond van de licentie.
 */

package nl.procura.burgerzaken.dossiers.model.dossier;

import java.io.Serializable;

import javax.persistence.*;

import nl.procura.burgerzaken.dossiers.util.DatabaseFieldNotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "doss_ref")
public class DossierReference implements Serializable {

  @EmbeddedId
  private DossierReferenceId id;

  @Column(name = "description")
  @DatabaseFieldNotNull
  private String description;

  @ManyToOne
  @MapsId("dossierId")
  @JoinColumn(name = "doss_id", nullable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Dossier dossier;

  public DossierReference(Dossier dossier, String referenceNumber, String description) {
    id = new DossierReferenceId(referenceNumber);
    this.dossier = dossier;
    this.description = description;
  }
}
