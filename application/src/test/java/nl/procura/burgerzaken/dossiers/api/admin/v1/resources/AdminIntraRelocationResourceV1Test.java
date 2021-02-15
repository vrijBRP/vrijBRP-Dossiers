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

package nl.procura.burgerzaken.dossiers.api.admin.v1.resources;

import static java.util.Collections.singleton;
import static nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.intra.AdminIntraMunicipalRelocationTest.newRelocation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.burgerzaken.dossiers.GbaSource;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminApiReferenceId;
import nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.intra.AdminApiIntraMunicipalRelocation;
import nl.procura.burgerzaken.dossiers.components.ApiAccess;

@SpringBootTest
@ContextConfiguration(initializers = GbaSource.class)
@AutoConfigureMockMvc
@Disabled("Admin API not important anymore since relocation are add directly to the Front-desk")
class AdminIntraRelocationResourceV1Test {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ApiAccess apiAccess;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private EntityManager em;

  @BeforeEach
  public void setUp() {
    apiAccess.adminScope(mockMvc);
  }

  @AfterEach
  public void tearDown() {
    apiAccess.delete();
  }

  @Test
  void postMustCreateDossierInDb() throws Exception {
    String referenceId = "id";
    AdminApiIntraMunicipalRelocation relocation = newRelocation(
        singleton(new AdminApiReferenceId(referenceId, "description")));
    // when
    AdminApiIntraMunicipalRelocation response = createRelocation(relocation);
    String dossierId = response.getDossier().getDossierId();
    // then database must be updated correctly
    Tuple db = (Tuple) em.createNativeQuery("SELECT * FROM doss" +
        " WHERE casenr = ?", Tuple.class)
        .setParameter(1, dossierId)
        .getSingleResult();
    Object dossId = db.get("doss_id");
    List<Tuple> list = (List<Tuple>) em.createNativeQuery("SELECT * FROM doss_ref" +
        " WHERE doss_id = ?", Tuple.class)
        .setParameter(1, dossId)
        .getResultList();
    assertEquals(1, list.size());
    assertEquals(referenceId, list.get(0).get("ref_casenr"));
  }

  @Test
  void postWithMultipleIdsMustCreateDossierWithMultipleIdsInDb() throws Exception {
    AdminApiReferenceId id1 = new AdminApiReferenceId("id1", "description");
    AdminApiReferenceId id2 = new AdminApiReferenceId("id2", "description");
    AdminApiIntraMunicipalRelocation relocation = newRelocation(Set.of(id1, id2));
    // when
    AdminApiIntraMunicipalRelocation response = createRelocation(relocation);
    String dossierId = response.getDossier().getDossierId();
    // then database must be updated correctly
    Tuple db = (Tuple) em.createNativeQuery("SELECT * FROM doss" +
        " WHERE casenr = ?", Tuple.class)
        .setParameter(1, dossierId)
        .getSingleResult();
    Object dossId = db.get("doss_id");
    List<Tuple> list = (List<Tuple>) em.createNativeQuery("SELECT * FROM doss_ref" +
        " WHERE doss_id = ?" +
        " ORDER BY ref_casenr", Tuple.class)
        .setParameter(1, dossId)
        .getResultList();
    assertEquals(2, list.size());
    assertEquals(id1.getId(), list.get(0).get("ref_casenr"));
    assertEquals(id2.getId(), list.get(1).get("ref_casenr"));
  }

  @Test
  void postWithDossierIdMustReturnBadRequest() throws Exception {
    // when
    AdminApiIntraMunicipalRelocation relocation = newRelocation(
        singleton(new AdminApiReferenceId("id", "description")));
    relocation.getDossier().setDossierId("a-dossier-id");
    // when
    postRelocation(relocation)
        .andExpect(status().isBadRequest());
  }

  @Test
  void createdRelocationMustBeReturnedWithGet() throws Exception {
    // given
    AdminApiIntraMunicipalRelocation relocation = newRelocation(
        singleton(new AdminApiReferenceId("id", "description")));
    String dossierId = createRelocation(relocation).getDossier().getDossierId();
    // when
    AdminApiIntraMunicipalRelocation response = getRelocation(dossierId);
    // then
    assertEquals("created", response.getDossier().getStatus().getCode());
  }

  @Test
  void deletedRelocationMustNotBeInDb() throws Exception {
    // given
    AdminApiIntraMunicipalRelocation relocation = newRelocation(
        singleton(new AdminApiReferenceId("id", "description")));
    String dossierId = createRelocation(relocation).getDossier().getDossierId();
    // when delete
    mockMvc.perform(delete("/admin/api/v1/dossiers/{dossierId}", dossierId)
        .headers(apiAccess.authorization())
        .accept(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andReturn();
    // then record must not exist in database
    List<Tuple> list = (List<Tuple>) em.createNativeQuery("SELECT * FROM doss" +
        " WHERE casenr = ?", Tuple.class)
        .setParameter(1, dossierId)
        .getResultList();
    assertEquals(0, list.size());
  }

  private AdminApiIntraMunicipalRelocation createRelocation(AdminApiIntraMunicipalRelocation relocation)
      throws Exception {
    MvcResult response = postRelocation(relocation)
        // then
        .andExpect(status().isCreated())
        .andReturn();
    return mapper.readValue(response.getResponse().getContentAsByteArray(), AdminApiIntraMunicipalRelocation.class);
  }

  private ResultActions postRelocation(AdminApiIntraMunicipalRelocation relocation) throws Exception {
    String requestJson = mapper.writeValueAsString(relocation);
    return mockMvc.perform(post("/admin/api/v1/relocations/intra")
        .headers(apiAccess.authorization())
        .content(requestJson)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private AdminApiIntraMunicipalRelocation getRelocation(String dossierId) throws Exception {
    MvcResult response = mockMvc.perform(get("/admin/api/v1/relocations/intra/{dossierId}", dossierId)
        .headers(apiAccess.authorization())
        .accept(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andReturn();
    return mapper.readValue(response.getResponse().getContentAsByteArray(), AdminApiIntraMunicipalRelocation.class);
  }

}
